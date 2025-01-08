package uz.demo.card_procession_app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.demo.card_procession_app.dto.request.AddCardDto;
import uz.demo.card_procession_app.dto.request.TransactionDto;
import uz.demo.card_procession_app.dto.response.CardResponse;
import uz.demo.card_procession_app.dto.response.DataDto;
import uz.demo.card_procession_app.dto.response.TransactionResponse;
import uz.demo.card_procession_app.entity.auth.User;
import uz.demo.card_procession_app.entity.contants.Currency;
import uz.demo.card_procession_app.entity.contants.Status;
import uz.demo.card_procession_app.entity.contants.TransactionType;
import uz.demo.card_procession_app.entity.data.Card;
import uz.demo.card_procession_app.entity.data.CurrencyRate;
import uz.demo.card_procession_app.entity.data.IdEmPotency;
import uz.demo.card_procession_app.entity.data.Transaction;
import uz.demo.card_procession_app.exceptions.BadRequestException;
import uz.demo.card_procession_app.exceptions.NotFoundException;
import uz.demo.card_procession_app.mapper.CardMapper;
import uz.demo.card_procession_app.mapper.TransactionMapper;
import uz.demo.card_procession_app.repository.auth.UserRepository;
import uz.demo.card_procession_app.repository.card.CardRepository;
import uz.demo.card_procession_app.repository.card.IdEmPotencyRepository;
import uz.demo.card_procession_app.repository.card.TransactionRepository;
import uz.demo.card_procession_app.service.CardService;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final IdEmPotencyRepository idEmPotencyRepository;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);
    private final CurrencyService currencyService;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<DataDto<CardResponse>> addCard(AddCardDto addCardDto, UUID idempotencyKey, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("User try to create a card: {}", addCardDto.getUserId());
        Optional<IdEmPotency> optionalIdEmPotency = idEmPotencyRepository.findByIdEmPotencyKey(idempotencyKey);
        if (optionalIdEmPotency.isPresent()) {
            Card card = cardRepository.findById(optionalIdEmPotency.get().getCardId())
                    .orElseThrow(() -> {
                        logger.error("with this IdEmPotency Card available: {}. IdEmPotency id: {}", addCardDto.getUserId(), idempotencyKey);

                        return new RuntimeException(messageSource.getMessage("error.id_em_potency_key_card_not_found", null, locale));
                    });
            CardResponse cardResponse = cardMapper.toResponse(card);
            return ResponseEntity.status(201).body(new DataDto<>(cardResponse));
        }

        if (cardRepository.countCardByUserId(addCardDto.getUserId()) == 3) {
            logger.error("User has already 3 cards: {}", addCardDto.getUserId());
            throw new BadRequestException(messageSource.getMessage("error.limit_card", null, locale));
        }

        User user = userRepository.findById(addCardDto.getUserId())
                .orElseThrow(() ->
                        {
                            logger.error("with this id user not found: {}. IdEmPotency id: {}", addCardDto.getUserId(), idempotencyKey);
                            return new UsernameNotFoundException(messageSource.getMessage("error.user_not_found", null, locale));
                        }
                );

        Card card = new Card();
        UUID uuid = UUID.randomUUID();
        card.setId(uuid);
        card.setCurrency(Currency.UZS.name());
        card.setBalance(new BigDecimal(0));
        card.setNumber(generateCardNumber());
        card.setExpirationDate(generateExpirationDate());
        card.setUserId(user.getId());
        card.setType("UzCard");
        card.setStatus(Status.ACTIVE.name());
        Card savedCard = cardRepository.save(card);

        IdEmPotency idEmPotency = new IdEmPotency();
        idEmPotency.setIdEmPotencyKey(idempotencyKey);
        idEmPotency.setCardId(uuid);
        idEmPotencyRepository.save(idEmPotency);
        logger.info("Card created: {}. Card id: {}", addCardDto.getUserId(), uuid);
        CardResponse cardResponse = cardMapper.toResponse(savedCard);
        return ResponseEntity.status(201).body(new DataDto<>(cardResponse));
    }

    @Override
    public DataDto<CardResponse> getCardById(UUID cardId, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("Try to get card by id: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            logger.error("Card with this id not found: {}", cardId);
            throw new NotFoundException(messageSource.getMessage("error.card_not_found", null, locale));
        }
        CardResponse cardResponse = cardMapper.toResponse(optionalCard.get());
        return new DataDto<>(cardResponse);
    }

    @Override
    public DataDto<CardResponse> blockCard(UUID cardId, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("User try to block card by id: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            logger.error("Card with this id not found: {}", cardId);
            throw new NotFoundException(messageSource.getMessage("error.card_not_found", null, locale));
        }
        if (optionalCard.get().getStatus().equals(Status.ACTIVE.name())) {
            Card card = optionalCard.get();
            card.setStatus(Status.BLOCKED.name());
            Card blockedCard = cardRepository.save(card);
            CardResponse cardResponse = cardMapper.toResponse(blockedCard);
            return new DataDto<>(cardResponse);
        }
        else throw new BadRequestException("Card status is not active!");

    }

    @Override
    public void unBlockCard(UUID cardId, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("User try to unblock card by id: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            logger.error("Card with this id not found: {}", cardId);
            throw new NotFoundException(messageSource.getMessage("error.card_not_found", null, locale));
        }
        if (optionalCard.get().getStatus().equals(Status.BLOCKED.name())) {
            Card card = optionalCard.get();
            card.setStatus(Status.ACTIVE.name());
            cardRepository.save(card);
        }
        else throw new BadRequestException("Card status is not blocked!");

    }

    @Override
    public DataDto<TransactionResponse> debit(UUID cardId, TransactionDto transactionDto, UUID idempotencyKey, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("User try to debit : {}", cardId);
        Optional<IdEmPotency> optionalIdEmPotency = idEmPotencyRepository.findByIdEmPotencyKey(idempotencyKey);
        if (optionalIdEmPotency.isPresent()) {
            Transaction transaction = transactionRepository.findById(optionalIdEmPotency.get().getCardId())
                    .orElseThrow(() -> {
                        logger.error("with this IdEmPotency transaction available: {}. IdEmPotency id: {}", transactionDto.getExternalId(), idempotencyKey);

                        return new RuntimeException(messageSource.getMessage("error.id_em_potency_key_card_not_found", null, locale));
                    });
            TransactionResponse transactionResponse = transactionMapper.toResponse(transaction);
            return new DataDto<>(transactionResponse);
        }
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            logger.error("Card with this id not found: {}", cardId);
            throw new NotFoundException(messageSource.getMessage("error.card_not_found", null, locale));
        }

        if (optionalCard.get().getBalance().compareTo(BigDecimal.valueOf(transactionDto.getAmount())) <0) {
            throw new BadRequestException(messageSource.getMessage("error.insufficient_fund", null, locale));
        }

        Card card = optionalCard.get();
        card.setBalance(card.getBalance().subtract(BigDecimal.valueOf(transactionDto.getAmount())));
        Card savedCard = cardRepository.save(card);
        logger.info("Card - debit new balance: {}", card.getBalance());

        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setId(UUID.randomUUID());
        transaction.setType(TransactionType.CREDIT.name());
        transaction.setCardId(cardId);
        transaction.setAfterBalance(savedCard.getBalance().longValue());
        transaction.setExchangeRate(currencyService.getCurrencyRateById());
        transaction.setCreatedAt(createdAt);

        IdEmPotency idEmPotency = new IdEmPotency();
        idEmPotency.setIdEmPotencyKey(idempotencyKey);
        idEmPotency.setCardId(cardId);
        idEmPotencyRepository.save(idEmPotency);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Card - debit transaction id: {}", savedTransaction.getId());

        TransactionResponse transactionResponse = transactionMapper.toResponse(savedTransaction);

        return new DataDto<>(transactionResponse);
    }

    @Override
    public DataDto<TransactionResponse> credit(UUID cardId, TransactionDto transactionDto, UUID idempotencyKey, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("User try to credit : {}", cardId);

        Optional<IdEmPotency> optionalIdEmPotency = idEmPotencyRepository.findByIdEmPotencyKey(idempotencyKey);
        if (optionalIdEmPotency.isPresent()) {
            Transaction transaction = transactionRepository.findById(optionalIdEmPotency.get().getCardId())
                    .orElseThrow(() -> {
                        logger.error("with this IdEmPotency transaction available: {}. IdEmPotency id: {}", transactionDto.getExternalId(), idempotencyKey);

                        return new RuntimeException(messageSource.getMessage("error.id_em_potency_key_card_not_found", null, locale));
                    });
            TransactionResponse transactionResponse = transactionMapper.toResponse(transaction);
            return new DataDto<>(transactionResponse);
        }

        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            logger.error("Card with this id not found: {}", cardId);
            throw new NotFoundException(messageSource.getMessage("error.card_not_found", null, locale));
        }

        Card card = optionalCard.get();
        card.setBalance(card.getBalance().add(BigDecimal.valueOf(transactionDto.getAmount())));
        Card savedCard = cardRepository.save(card);
        logger.info("Card - debit new balance: {}", card.getBalance());

        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Transaction transaction = transactionMapper.toEntity(transactionDto);

        transaction.setId(UUID.randomUUID());
        transaction.setType(TransactionType.CREDIT.name());
        transaction.setCardId(cardId);
        transaction.setAfterBalance(savedCard.getBalance().longValue());
        transaction.setExchangeRate(currencyService.getCurrencyRateById());
        transaction.setCreatedAt(createdAt);

        IdEmPotency idEmPotency = new IdEmPotency();
        idEmPotency.setIdEmPotencyKey(idempotencyKey);
        idEmPotency.setCardId(cardId);
        idEmPotencyRepository.save(idEmPotency);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Card - debit transaction id: {}", savedTransaction.getId());
        TransactionResponse transactionResponse = transactionMapper.toResponse(savedTransaction);

        return new DataDto<>(transactionResponse);
    }

    @Override
    public DataDto<Page<TransactionResponse>> getTransactions(int page, int size, UUID cardId, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        logger.info("User try to get transaction history : {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            logger.error("Card with this id not found: {}", cardId);
            throw new NotFoundException(messageSource.getMessage("error.card_not_found", null, locale));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> all = transactionRepository.findAll(pageable);
        List<TransactionResponse> transactionResponses= all.getContent().stream()
                .map(transactionMapper::toResponse)
                .toList();
        Page<TransactionResponse> responses = new PageImpl<>(transactionResponses, pageable, all.getContent().size());
        return new DataDto<>(responses);
    }
    private String generateCardNumber() {
        return "9680 0204 " + (1000 + (int) (Math.random() + 9000)) + " " + (1000 + (int) (Math.random() + 9000));
    }

    private String generateExpirationDate() {
        LocalDate expireDate = LocalDate.now().plusYears(5);
        return expireDate.getMonthValue() + "/" + expireDate.getYear();
    }
}

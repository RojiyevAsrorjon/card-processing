package uz.demo.card_procession_app.controller.card;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.demo.card_procession_app.dto.request.AddCardDto;
import uz.demo.card_procession_app.dto.request.TransactionDto;
import uz.demo.card_procession_app.dto.response.CardResponse;
import uz.demo.card_procession_app.dto.response.DataDto;
import uz.demo.card_procession_app.dto.response.TransactionResponse;
import uz.demo.card_procession_app.service.CardService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    @PreAuthorize(value = "hasRole('Client')")
    @PostMapping
    public ResponseEntity<DataDto<CardResponse>> addCard(@Valid @RequestBody AddCardDto addCardDto, @RequestHeader(name = "Idempotency-Key") UUID idempotencyKey, @RequestHeader(defaultValue = "en") String mobileLang) {
        return cardService.addCard(addCardDto, idempotencyKey, mobileLang);
    }

    @Cacheable(value = "cards", key = "#cardId+'_cards'")
    @PreAuthorize(value = "hasRole('ROLE_Client')")
    @GetMapping("/{cardId}")
    public DataDto<CardResponse> getCardById(@PathVariable UUID cardId, @RequestHeader(defaultValue = "en") String mobileLang) {
        return cardService.getCardById(cardId, mobileLang);
    }

    @PreAuthorize(value = "hasRole('ROLE_Client')")
    @PostMapping("/{cardId}/block")
    public DataDto<CardResponse> blockCard(
            @PathVariable UUID cardId,
            @RequestHeader(defaultValue = "en") String mobileLang
    ){

        return cardService.blockCard(cardId, mobileLang);
    }

    @PreAuthorize(value = "hasRole('ROLE_Client')")
    @PostMapping("/{cardId}/unblock")
    public ResponseEntity<Void> unBlockCard(
            @PathVariable UUID cardId,
          @RequestHeader(defaultValue = "en") String mobileLang
    ){
        cardService.unBlockCard(cardId, mobileLang);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(value = "hasRole('ROLE_Client')")
    @PostMapping("/{cardId}/debit")
    public ResponseEntity<DataDto<TransactionResponse>> debit(@PathVariable UUID cardId, @RequestBody TransactionDto transactionDto, @RequestHeader(name = "Idempotency-Key") UUID idempotencyKey, @RequestHeader(defaultValue = "en") String mobileLang) {
        return ResponseEntity.ok(cardService.debit(cardId,transactionDto, idempotencyKey, mobileLang));
    }

    @PreAuthorize(value = "hasRole('ROLE_Client')")
    @PostMapping("/{cardId}/credit")
    public ResponseEntity<DataDto<TransactionResponse>> credit(@PathVariable UUID cardId, @RequestBody TransactionDto transactionDto, @RequestHeader(name = "Idempotency-Key") UUID idempotencyKey, @RequestHeader(defaultValue = "en") String mobileLang) {
        return ResponseEntity.ok(cardService.credit(cardId,transactionDto, idempotencyKey, mobileLang));
    }

    @Cacheable(value = "transactions", key = "#cardId + '_transactions'")
    @PreAuthorize(value = "hasRole('ROLE_Client')")
    @GetMapping("/{cardId}/transactions")
    public DataDto<Page<TransactionResponse>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID cardId,
            @RequestHeader(defaultValue = "en") String mobileLang
    ){
        return cardService.getTransactions(page, size, cardId, mobileLang);
    }
}

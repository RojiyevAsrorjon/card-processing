package uz.demo.card_procession_app.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.demo.card_procession_app.dto.request.AddCardDto;
import uz.demo.card_procession_app.dto.request.TransactionDto;
import uz.demo.card_procession_app.dto.response.CardResponse;
import uz.demo.card_procession_app.dto.response.DataDto;
import uz.demo.card_procession_app.dto.response.TransactionResponse;
import uz.demo.card_procession_app.entity.data.Transaction;

import java.util.UUID;

@Service
public interface CardService {
    ResponseEntity<DataDto<CardResponse>> addCard(AddCardDto addCardDto, UUID idempotencyKey, String mobileLang);

    DataDto<CardResponse> getCardById(UUID cardId, String mobileLang);

    DataDto<CardResponse> blockCard(UUID cardId,String mobileLang);

    void unBlockCard(UUID cardId, String mobileLang);

    DataDto<TransactionResponse> debit(UUID cardId,TransactionDto transactionDto, UUID idempotencyKey, String mobileLang);

    DataDto<TransactionResponse> credit(UUID cardId, TransactionDto transactionDto, UUID idempotencyKey, String mobileLang);

    DataDto<Page<TransactionResponse>> getTransactions(int page, int size, UUID cardId, String mobileLang);
}

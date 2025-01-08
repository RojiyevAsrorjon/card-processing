package uz.demo.card_procession_app.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import uz.demo.card_procession_app.dto.request.TransactionDto;
import uz.demo.card_procession_app.dto.response.TransactionResponse;
import uz.demo.card_procession_app.entity.data.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toEntity(TransactionDto transactionDto);

    TransactionResponse toResponse(Transaction transaction);
}

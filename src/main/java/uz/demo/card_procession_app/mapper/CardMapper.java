package uz.demo.card_procession_app.mapper;

import org.mapstruct.Mapper;
import uz.demo.card_procession_app.dto.response.CardResponse;
import uz.demo.card_procession_app.entity.data.Card;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardResponse toResponse(Card card);
}

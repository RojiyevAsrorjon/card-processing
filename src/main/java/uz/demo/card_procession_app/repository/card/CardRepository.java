package uz.demo.card_procession_app.repository.card;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.demo.card_procession_app.entity.data.Card;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    int countCardByUserId(@NotBlank(message = "Missing required field(s): user_id") Long userId);
}

package uz.demo.card_procession_app.repository.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.demo.card_procession_app.entity.data.IdEmPotency;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdEmPotencyRepository extends JpaRepository<IdEmPotency, Long> {
    Optional<IdEmPotency> findByIdEmPotencyKey(UUID idempotencyKey);
}

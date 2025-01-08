package uz.demo.card_procession_app.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.demo.card_procession_app.entity.auth.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT p from users p where p.phoneNumber=:username")
    Optional<User> findByPhoneNumber(String username);
}

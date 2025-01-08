package uz.demo.card_procession_app.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.demo.card_procession_app.entity.auth.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}

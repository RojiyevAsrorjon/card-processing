package uz.demo.card_procession_app.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.demo.card_procession_app.entity.auth.Role;
import uz.demo.card_procession_app.entity.auth.User;
import uz.demo.card_procession_app.repository.auth.RoleRepository;
import uz.demo.card_procession_app.repository.auth.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found : " + username)
        );
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new RuntimeException("User role not found" + username));
        return new CustomUserDetails(user, role);
    }
}

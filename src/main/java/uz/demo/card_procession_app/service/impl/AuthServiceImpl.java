package uz.demo.card_procession_app.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.demo.card_procession_app.configuration.CustomUserDetails;
import uz.demo.card_procession_app.configuration.JwtProvider;
import uz.demo.card_procession_app.controller.auth.AuthController;
import uz.demo.card_procession_app.dto.request.LoginDto;
import uz.demo.card_procession_app.dto.response.DataDto;
import uz.demo.card_procession_app.entity.auth.Role;
import uz.demo.card_procession_app.entity.auth.User;
import uz.demo.card_procession_app.repository.auth.RoleRepository;
import uz.demo.card_procession_app.service.AuthService;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final MessageSource messageSource;
    private final JwtProvider jwtProvider;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Override
    public DataDto<String> login(LoginDto loginDto, String mobileLang) {
        Locale locale = Locale.forLanguageTag(mobileLang);
        try {
            logger.info("Login attempt for user: {}", loginDto.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
            Optional<Role> optionalRole = roleRepository.findById(user.getId());
            if (optionalRole.isEmpty()) {
                logger.error("Role not found for user: {}", loginDto.getUsername());
                throw new RuntimeException(messageSource.getMessage("error.role_not_found", null, locale));
            }
            logger.info("User {} authenticated successfully with role: {}", user.getPhoneNumber(), optionalRole.get().getName());
            String token = jwtProvider.generateToken(user.getPhoneNumber(), optionalRole.get().getName());
            return new DataDto<>(token);
        } catch (RuntimeException e) {
            logger.error("Failed login attempt for user: {}. Error: {}", loginDto.getUsername(), e.getMessage());
            throw new UsernameNotFoundException(messageSource.getMessage("error.login_password_incorrect", null, locale));
        }
    }
}

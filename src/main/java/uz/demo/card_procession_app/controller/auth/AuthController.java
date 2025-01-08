package uz.demo.card_procession_app.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.demo.card_procession_app.dto.request.LoginDto;
import uz.demo.card_procession_app.dto.response.DataDto;
import uz.demo.card_procession_app.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<DataDto<String>> login(@RequestBody LoginDto loginDto, @RequestHeader(defaultValue = "en") String mobileLang) {
        return ResponseEntity.ok(authService.login(loginDto, mobileLang));
    }
}

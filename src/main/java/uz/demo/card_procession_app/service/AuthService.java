package uz.demo.card_procession_app.service;

import org.springframework.stereotype.Service;
import uz.demo.card_procession_app.dto.request.LoginDto;
import uz.demo.card_procession_app.dto.response.DataDto;

@Service
public interface AuthService {
    DataDto<String> login(LoginDto loginDto, String mobileLang);
}

package project.akhir.danapprentechteam3.login.service;

import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.models.EmailToken;

@Service
public interface EmailVerifyService
{
    EmailToken findByCodeVerify(String code);
    EmailToken findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    void deleteByMobileNumber(String mobileNumber);
    EmailToken findByEmail(String email);
    EmailToken save(EmailToken emailToken);
}

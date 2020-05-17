package project.akhir.danapprentechteam3.login.service;

import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.models.SmsOtp;

@Service
public interface SmsOtpService
{
    SmsOtp findByMobileNumber(String otp);
    void deleteByMobileNumber(String mobileNumber);
    SmsOtp findByEmail(String id);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    String createOtp();
    Message sendSMS(String noTelepon, String otp);
    SmsOtp save(SmsOtp otp);
}

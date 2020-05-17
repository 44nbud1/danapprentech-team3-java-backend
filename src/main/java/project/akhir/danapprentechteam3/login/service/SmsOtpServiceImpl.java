package project.akhir.danapprentechteam3.login.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.models.SmsOtp;
import project.akhir.danapprentechteam3.login.repository.SmsOtpRepository;

import java.util.Random;

@Service
public class SmsOtpServiceImpl implements SmsOtpService
{
    public static final String ACCOUNT_SID = "ACb3c4fe3afb030fc4975038ed77135694";
    public static final String AUTH_TOKEN = "d53e919b28592508299ebedd054a64f9";

    @Autowired
    private SmsOtpRepository smsOtpRepository;

    @Override
    public SmsOtp findByMobileNumber(String otp)
    {
        return smsOtpRepository.findByMobileNumber(otp);
    }

    @Override
    public void deleteByMobileNumber(String mobileNumber)
    {
        smsOtpRepository.deleteByMobileNumber(mobileNumber);
    }

    @Override
    public SmsOtp findByEmail(String email)
    {
        return smsOtpRepository.findByEmail(email);
    }

    @Override
    public boolean existsByMobileNumber(String mobileNumber)
    {
        return smsOtpRepository.existsByMobileNumber(mobileNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return smsOtpRepository.existsByEmail(email);
    }

    @Override
    public String createOtp()
    {
        Random rand = new Random();
        String newCodeOtp = "";
        for (int i=0; i<4; i++)
        {
            newCodeOtp += rand.nextInt(9);
        }

        return newCodeOtp;
    }

    @Override
    public Message sendSMS(String noTelepon, String otp)
    {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(noTelepon),
                new com.twilio.type.PhoneNumber("+19416769743"),
                "Your Otp is "+ otp)
                .create();

        return message;
    }

    @Override
    public SmsOtp save(SmsOtp otp) {
        return smsOtpRepository.save(otp);
    }
}

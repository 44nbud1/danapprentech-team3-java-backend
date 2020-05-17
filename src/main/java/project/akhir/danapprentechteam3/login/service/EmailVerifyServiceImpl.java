package project.akhir.danapprentechteam3.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.models.EmailToken;
import project.akhir.danapprentechteam3.login.repository.EmailVerifyRepository;

@Service
public class EmailVerifyServiceImpl implements EmailVerifyService {

    @Autowired
    private EmailVerifyRepository emailVerifyRepository;

    @Override
    public EmailToken findByCodeVerify(String code) {
        return emailVerifyRepository.findByCodeVerify(code);
    }

    @Override
    public EmailToken findByMobileNumber(String mobileNumber)
    {
        return emailVerifyRepository.findByMobileNumber(mobileNumber);
    }

    @Override
    public boolean existsByEmail(String email)
    {
        return emailVerifyRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByMobileNumber(String mobileNumber)
    {
        return emailVerifyRepository.existsByMobileNumber(mobileNumber);
    }

    @Override
    public void deleteByMobileNumber(String mobileNumber)
    {
        emailVerifyRepository.deleteByMobileNumber(mobileNumber);
    }

    @Override
    public EmailToken findByEmail(String email) {
        return emailVerifyRepository.findByEmail(email);
    }

    @Override
    public EmailToken save(EmailToken emailToken) {
        return emailVerifyRepository.save(emailToken);
    }
}

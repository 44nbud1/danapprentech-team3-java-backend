package project.akhir.danapprentechteam3.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.akhir.danapprentechteam3.login.models.EmailToken;

@Repository
public interface EmailVerifyRepository extends JpaRepository<EmailToken, Long>
{
    EmailToken findByCodeVerify(String code);
    EmailToken findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    void deleteByMobileNumber(String mobileNumber);
    EmailToken findByEmail(String email);
}

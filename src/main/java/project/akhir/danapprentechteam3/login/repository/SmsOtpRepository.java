package project.akhir.danapprentechteam3.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.akhir.danapprentechteam3.login.models.SmsOtp;

@Repository
public interface SmsOtpRepository extends JpaRepository<SmsOtp,Long>
{
   SmsOtp findByMobileNumber(String otp);
   void deleteByMobileNumber(String mobileNumber);
   SmsOtp findByEmail(String id);
   boolean existsByMobileNumber(String mobileNumber);
   boolean existsByEmail(String email);

}

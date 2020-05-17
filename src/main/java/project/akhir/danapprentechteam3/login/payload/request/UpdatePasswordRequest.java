package project.akhir.danapprentechteam3.login.payload.request;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
public class UpdatePasswordRequest {

        private Long idReset;

        private String token ;

        private String otp ;

        private boolean statusOtp ;

        private Date createdDate;

        private String email;

        private String noTelepon;

        private String newPassword;

        private String confirmPassword;

        public UpdatePasswordRequest()
        {
                createdDate = new Date();
                token = UUID.randomUUID().toString();
        }
}

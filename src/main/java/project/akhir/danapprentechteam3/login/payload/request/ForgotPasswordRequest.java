package project.akhir.danapprentechteam3.login.payload.request;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
public class ForgotPasswordRequest<U, L extends Number> {

    @Column(name = "id_reset")
    private Long idReset;
    @Column(name = "token_reset_password")
    private String token ;

    private String otp ;

    @Column
    private boolean statusOtp ;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private String email;
    private String noTelepon;

    @Transient
    private String newPassword;

    @Transient
    private String confirmPassword;

    public ForgotPasswordRequest()
    {
        createdDate = new Date();
        token = UUID.randomUUID().toString();
    }
}

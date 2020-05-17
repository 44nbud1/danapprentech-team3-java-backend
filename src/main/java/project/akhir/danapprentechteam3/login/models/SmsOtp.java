package project.akhir.danapprentechteam3.login.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class SmsOtp {

    @Id
    @NotBlank
    @NotNull
    @Column(length = 4)
    private String codeOtp;
    private String email;
    private String mobileNumber;
    private boolean statusOtp;

}

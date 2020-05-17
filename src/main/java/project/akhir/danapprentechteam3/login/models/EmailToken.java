package project.akhir.danapprentechteam3.login.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Entity
@Table(name = "email_token")
public class EmailToken {

    @Id
    @NotBlank
    @NotNull
    @Column(length = 200)
    private String codeVerify;
    private String email;
    private String mobileNumber;
    private boolean statusEmailVerify;

}


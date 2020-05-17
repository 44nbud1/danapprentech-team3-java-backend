package project.akhir.danapprentechteam3.login.payload.request;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
public class EmailVerifyRequest
{

    @Column(name = "token_id")
    private Long id;

    @Column(name = "confirmation_token")
    private String confirmationToken;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    private boolean statusEmail = false;

    public EmailVerifyRequest()
    {
        statusEmail = true;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

}

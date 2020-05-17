package project.akhir.danapprentechteam3.transaksi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import project.akhir.danapprentechteam3.login.models.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaksi_user")
public class CustomerChoice
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id_transaksi_user")
    private int id; //

    private String namaProvider; //
    private Long harga; //
    private String paketData; //

    @CreatedDate
    private Date waktuTransaksi; //

    private boolean statusTransaksi = false; //
    private String nomorPaketData;//

    @Transient
    private String status;
    @Transient
    private String message;

    private String noTelepon; //


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User users;

//    @OneToOne(fetch = FetchType.LAZY,
//            cascade =  CascadeType.ALL,
//            mappedBy = "transaksisUser")
//    private DetailTransaksi detailTransaksi;

}

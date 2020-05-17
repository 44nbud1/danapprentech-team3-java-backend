package project.akhir.danapprentechteam3.transaksi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "detail_transaksi")
public class DetailTransaksi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idTransaksi;

   private Long id_pembayaran;
   private Long id_upload;
   private Long id_transaksi_user;
   private Date tanggalPembayaran;
//
//   @OneToOne(fetch = FetchType.LAZY, optional = false)
//   @JoinColumn(name = "id_pembayaran", nullable = false)
//   private Pembayaran pembayaran;
//
//   @OneToOne(fetch = FetchType.LAZY, optional = false)
//   @JoinColumn(name = "id_upload", nullable = false)
//   private Upload upload;
//
//   @OneToOne(fetch = FetchType.LAZY, optional = false)
//   @JoinColumn(name = "id_transaksi_user", nullable = false)
//   private CustomerChoice transaksisUser;
}

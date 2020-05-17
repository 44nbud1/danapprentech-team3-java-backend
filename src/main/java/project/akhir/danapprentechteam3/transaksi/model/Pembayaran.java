//package project.akhir.danapprentechteam3.transaksi.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@Data
//@AllArgsConstructor
//public class Pembayaran
//{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String jenis_pembayaran;
//
////    @OneToOne(fetch = FetchType.LAZY,
////            cascade =  CascadeType.ALL,
////            mappedBy = "pembayaran")
////    private DetailTransaksi detailTransaksi;
//
//    public Pembayaran()
//    {
//        Pembayaran virtualAccount = new Pembayaran();
//        virtualAccount.setId(1001L);
//        virtualAccount.setJenis_pembayaran("VirtualAccount");
//
//        Pembayaran E_wallet = new Pembayaran();
//
//        E_wallet.setId(1002L);
//        E_wallet.setJenis_pembayaran("E-Wallet");
//    }
//}

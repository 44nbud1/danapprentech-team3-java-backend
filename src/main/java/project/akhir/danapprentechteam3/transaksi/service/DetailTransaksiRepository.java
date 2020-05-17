package project.akhir.danapprentechteam3.transaksi.service;

import org.springframework.data.jpa.repository.JpaRepository;
import project.akhir.danapprentechteam3.transaksi.model.DetailTransaksi;

public interface DetailTransaksiRepository extends JpaRepository<DetailTransaksi,Long> {
}

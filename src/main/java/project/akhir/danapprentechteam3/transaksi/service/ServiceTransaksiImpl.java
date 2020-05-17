package project.akhir.danapprentechteam3.transaksi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.transaksi.model.Transaksi;

@Service
public class ServiceTransaksiImpl  implements ServiceTransaksi
{

    @Autowired
    private ServiceTransaksi serviceTransaksi;

    @Override
    public Transaksi saveTransaksi(Transaksi transaksi)
    {
        return serviceTransaksi.saveTransaksi(transaksi);
    }

    @Override
    public boolean statusPembayaran(String statusPembayaran) {
        return serviceTransaksi.statusPembayaran(statusPembayaran);
    }

    @Override
    public Transaksi findByNoTelepon(String noTelepon)
    {
        return findByNoTelepon(noTelepon);
    }



}

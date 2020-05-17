package project.akhir.danapprentechteam3.login.service;

import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.models.User;

@Service
public interface UserService
{
    User findByNoTelepon(String noTelepon);
    void deleteByNoTelepon(String noTelepon);
    Boolean existsByNoTelepon(String noTelepon);
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    User save(User user);
}

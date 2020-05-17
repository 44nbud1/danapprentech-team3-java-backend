package project.akhir.danapprentechteam3.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.models.User;
import project.akhir.danapprentechteam3.login.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByNoTelepon(String noTelepon) {
        return userRepository.findByNoTelepon(noTelepon);
    }

    @Override
    public void deleteByNoTelepon(String noTelepon) {
        userRepository.deleteByNoTelepon(noTelepon);
    }

    @Override
    public Boolean existsByNoTelepon(String noTelepon) {
        return userRepository.existsByNoTelepon(noTelepon);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

}

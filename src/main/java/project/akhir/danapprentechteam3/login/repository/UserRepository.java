package project.akhir.danapprentechteam3.login.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import project.akhir.danapprentechteam3.login.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	User findByNoTelepon(String noTelepon);
	void deleteByNoTelepon(String noTelepon);
	Boolean existsByNoTelepon(String noTelepon);
	Boolean existsByEmail(String email);
	User findByEmail(String email);
}

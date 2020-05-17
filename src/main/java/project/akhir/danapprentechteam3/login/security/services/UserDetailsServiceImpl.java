package project.akhir.danapprentechteam3.login.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.akhir.danapprentechteam3.login.models.User;
import project.akhir.danapprentechteam3.login.payload.response.MessageResponse;
import project.akhir.danapprentechteam3.login.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{

	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String noTelp) throws UsernameNotFoundException {
		User user = userRepository.findByNoTelepon(noTelp);

		if (user == null){
			return (UserDetails) ResponseEntity.badRequest().body(new MessageResponse(
					"User Not Found with Phone number : " + noTelp,"400"));
		}

		return UserDetailsImpl.build(user);
	}

//	public void updatePassword(String password, String email) {
//		forgotPasswordRepository.updatePassword(password, email);
//	}

}

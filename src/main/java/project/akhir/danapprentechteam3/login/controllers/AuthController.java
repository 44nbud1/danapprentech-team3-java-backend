package project.akhir.danapprentechteam3.login.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import project.akhir.danapprentechteam3.login.models.EmailToken;
import project.akhir.danapprentechteam3.login.models.SmsOtp;
import project.akhir.danapprentechteam3.login.models.User;
import project.akhir.danapprentechteam3.login.payload.request.*;
import project.akhir.danapprentechteam3.login.rabbitmq.rabbitconsumer.RabbitMqConsumer;
import project.akhir.danapprentechteam3.login.rabbitmq.rabbitproducer.RabbitMqProducer;
import project.akhir.danapprentechteam3.login.repository.*;
import project.akhir.danapprentechteam3.login.payload.response.JwtResponse;
import project.akhir.danapprentechteam3.login.payload.response.MessageResponse;
import project.akhir.danapprentechteam3.login.readdata.service.ProviderValidation;
//import project.akhir.danapprentechteam3.repository.*;
import project.akhir.danapprentechteam3.login.security.jwt.AuthEntryPointJwt;
import project.akhir.danapprentechteam3.login.security.jwt.JwtUtils;
import project.akhir.danapprentechteam3.login.security.passwordvalidation.PasswordAndEmailValImpl;
import project.akhir.danapprentechteam3.login.service.EmailSenderService;
import project.akhir.danapprentechteam3.login.security.services.UserDetailsImpl;
import project.akhir.danapprentechteam3.login.service.EmailVerifyService;
import project.akhir.danapprentechteam3.login.service.SmsOtpService;
import project.akhir.danapprentechteam3.login.service.UserService;
import project.akhir.danapprentechteam3.transaksi.model.Transaksi;
import project.akhir.danapprentechteam3.transaksi.repository.TransaksiRepository;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Transactional
public class AuthController<ACCOUNT_AUTH_ID, ACCOUNT_SID> {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	// for payload
	String noTelepon;
	String email;
	String VirtualAccount ;
	String namaUser ;
	String password ;
	Long pinTransaksi ;

	@Autowired
	PasswordAndEmailValImpl passwordEmailVal;

	@Autowired
    AuthenticationManager authenticationManager;

	@Autowired
    PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	ProviderValidation providerValidation;

	@Autowired
	EmailVerifyService emailVerifyService;

	@Autowired
	RabbitMqConsumer rabbitMqCustomer;

	@Autowired
	RabbitMqProducer rabbitMqProducer;

	@Autowired
	EmailSenderService emailSenderService;

	@Autowired
	SmsOtpService smsOtpService;

	@Autowired
	TransaksiRepository transaksiRepository;

	@Autowired
	UserService userService;

	//Queue
	private static final String signupKey = "signupKey";

	/*
	--------------------------------------------------------------------------------------------------------------------
	 */

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		//send to rabbi
		rabbitMqProducer.loginSendRabbit(loginRequest);

		//take from rabbit
//		LoginRequest login = rabbitMqCustomer.loginRequest(loginRequest);

		User cekUser = userService.findByNoTelepon(loginRequest.getNoTelepon());

		if (cekUser == null)
		{
			return ResponseEntity.badRequest().body((new MessageResponse("ERROR : \n" +
					"You have not registered..!",
					"400")));
		}

		if (cekUser.getTokenAkses() != null)
		{
			return ResponseEntity.badRequest().body((new MessageResponse("ERROR : You are Still logged in..!",
					"400")));
		}

		User user = userService.findByNoTelepon(loginRequest.getNoTelepon());

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getNoTelepon(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		user.setTokenAkses(token);
		userService.save(user);

		JwtResponse jwtResponse = new JwtResponse();
		jwtResponse.setSaldo(user.getSaldo());
		jwtResponse.setToken(token);
		jwtResponse.setEmail(userDetails.getEmail());
		jwtResponse.setId(userDetails.getId());
		jwtResponse.setUsername(userDetails.getUsername());
		jwtResponse.setStatus("200");
		jwtResponse.setMessage("successfully");
		return ResponseEntity.ok((jwtResponse));

	}

	/*
        --------------------------------------------------------------------------------------------------------------------
    */

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws IOException, TimeoutException {

		// send data to rabbit mq
		rabbitMqProducer.signupSendRabbit(signUpRequest);
		logger.info("Send data to rabbit Mq");

		// take data from rabbit mq
		SignupRequest signup = rabbitMqCustomer.recievedMessage(signUpRequest);

		logger.info("take data from rabbit mq");

//		if (!providerValidation.validasiProvider(signup.getNoTelepon()))
//		{
//			logger.info("ERROR : Username is not registered with the service provider!");
//			return ResponseEntity
//					.badRequest()
//					.body(new MessageResponse("ERROR : Phone number is not registered with the service provider!",
//							"400"));
//		}

		if (userService.existsByNoTelepon(signup.getNoTelepon())) {
			logger.info("ERROR : Username is already taken!");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Phone number is already taken!",
							"400"));
		}

		if (!passwordEmailVal.PasswordValidatorSpace(signup.getPassword())) {
			logger.info("ERROR : Your password does not must contains white space...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your password does not must contains white space...",
							"400"));
		}

		if (!passwordEmailVal.PasswordValidatorLowercase(signup.getPassword()))
		{
			logger.info("ERROR : Your password must contains one lowercase characters...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your password must contains one lowercase characters...",
							"400"));
		}

		if (!passwordEmailVal.PasswordValidatorUpercase(signup.getPassword()))
		{
			logger.info("ERROR : Your password must contains one uppercase characters...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your password must contains one uppercase characters...",
							"400"));
		}

		if (!passwordEmailVal.PasswordValidatorSymbol(signup.getPassword()))
		{
			logger.info("ERROR : Your password must contains one symbol characters...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your password must contains one symbol characters...",
							"400"));
		}

		if (!passwordEmailVal.PasswordValidatorNumber(signup.getPassword()))
		{
			logger.info("ERROR : Your password must contains one digit from 0-9...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your password must contains one digit from 0-9...",
							"400"));
		}

		if (!passwordEmailVal.EmailValidator(signup.getEmail()))
		{
			logger.info("ERROR : Your email address is invalid....");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your email address is invalid....",
							"400"));
		}

		if (userService.existsByEmail(signup.getEmail()))
		{
			logger.info("ERROR : Email is already in use!");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Email is already in use!",
							"400"));
		}

		if (!passwordEmailVal.confirmPassword(signup.getPassword(),signup.getConfirmPassword()))
		{
			logger.info("ERROR : Please check your password not Match!");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Please check your password not Match!",
							"400"));
		}

		if (!passwordEmailVal.LengthPhoneNumber(signup.getNoTelepon()))
		{
			logger.info("ERROR : Length phone number must be less than 15 ...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your phone number must be less than 15 ...",
							"400"));
		}

		if (!passwordEmailVal.LengthUsername(signup.getNamaUser()))
		{
			logger.info("ERROR : Length nama user must be more than equal 3 character...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Length nama user must be less than 3 character...",
							"400"));
		}

		if (!passwordEmailVal.NumberOnlyValidator(signup.getNoTelepon()))
		{
			logger.info("ERROR : Your phone number must be all numeric...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your phone number must be all numeric ...",
							"400"));
		}

		if (!passwordEmailVal.Alphabetic(signup.getNamaUser()))
		{
			logger.info("ERROR : Your phone number must be all Alphabetic...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your Full name must be all Alphabetic ...",
							"400"));
		}
		//LengthandNumeric

		if (!passwordEmailVal.LengthandNumeric(signup.getPinTransaksi()))
		{
			logger.info("ERROR : Your phone number must be all Alphabetic...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your pin length transaksi must be 6...",
							"400"));
		}

		if (!passwordEmailVal.LengthPin(signup.getPinTransaksi()))
		{
			logger.info("ERROR : Your phone number must be all Alphabetic...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your pin length transaksi must be 6...",
							"400"));
		}

		if (!passwordEmailVal.LengthPassword(signup.getPassword()))
		{
			logger.info("ERROR : Your password must be more than 7 and less than 17...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your password must be more than 7 and less than 17...",
							"400"));
		}

		if (emailVerifyService.existsByEmail(signup.getEmail()))
		{
			emailVerifyService.deleteByMobileNumber(signup.getNoTelepon());
		}

		if (smsOtpService.existsByEmail(signup.getEmail()))
		{
			smsOtpService.deleteByMobileNumber(signup.getNoTelepon());
		}

		if (emailVerifyService.existsByMobileNumber
				(signup.getNoTelepon()))
		{
			emailVerifyService.deleteByMobileNumber(signup.getNoTelepon());
		}

		if (smsOtpService.existsByMobileNumber
				(signup.getNoTelepon()))
		{
			smsOtpService.deleteByMobileNumber(signup.getNoTelepon());
		}

		//parse +62 -> 08
		String va = signup.getNoTelepon().substring(3,signUpRequest.getNoTelepon().length());
		// Create new user's account and encode password
		User user = new User();
		user.setNoTelepon(signup.getNoTelepon());
		user.setEmail(signup.getEmail());
		user.setPinTransaksi(signup.getPinTransaksi());
		user.setVirtualAccount("80000"+va);
		user.setNamaUser(signup.getNamaUser());
		user.setPassword(encoder.encode(signup.getPassword()));
		user.setStatus("200");
		user.setMessage("signup is successfully");

		noTelepon = signup.getNoTelepon();
		email = signup.getEmail();
		VirtualAccount = "80000"+va;
		namaUser = signup.getNamaUser();
		password = encoder.encode(signup.getPassword());
		pinTransaksi = signup.getPinTransaksi();

		// number verify
		SmsOtp otp = new SmsOtp();
		otp.setMobileNumber(signup.getNoTelepon());
		otp.setCodeOtp(smsOtpService.createOtp());
		otp.setStatusOtp(true);
		otp.setEmail(signup.getEmail());

		// email verify
		EmailToken emailToken = new EmailToken();
		emailToken.setEmail(signup.getEmail());
		emailToken.setCodeVerify(UUID.randomUUID().toString());
		emailToken.setStatusEmailVerify(true);
		emailToken.setMobileNumber(signup.getNoTelepon());

		// email verify
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(signup.getEmail());
		mailMessage.setSubject("Test test");
		mailMessage.setFrom("setiawan.aanbudi@gmail.com");
		mailMessage.setText("To confirm "+"https://project-danapprentech-3.herokuapp.com/api/auth/confirmation-account/"+
				emailToken.getCodeVerify());
		emailSenderService.sendEmail(mailMessage);

		smsOtpService.save(otp);
		emailVerifyService.save(emailToken);

//		smsOtpService.sendSMS(signUpRequest.getNoTelepon(), otp.getCodeOtp());

		return ResponseEntity.ok(new MessageResponse("Please Check your email or phone number"+otp.getCodeOtp()+
				" your otp "+otp.getCodeOtp(),"200"));
	}

	/*
	-------------------------------------------------------------------------------------------------------------------
	 */

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser(@Valid @RequestBody LoginRequest loginRequest)
	{

		User us = userService.findByNoTelepon(loginRequest.getNoTelepon());

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				loginRequest.getNoTelepon(), loginRequest.getPassword());

		Authentication authentication = authenticationManager.authenticate(authRequest);

		String token = us.getTokenAkses();

		if (token != null && loginRequest.getPassword() != null && loginRequest.getNoTelepon() != null)
		{
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			us.setTokenAkses(null);
			userService.save(us);
			JwtResponse jwtResponse = new JwtResponse();
			jwtResponse.setToken(null);
			jwtResponse.setEmail(userDetails.getEmail());
			jwtResponse.setId(userDetails.getId());
			jwtResponse.setUsername(userDetails.getUsername());
			jwtResponse.setMessage("successfully");
			jwtResponse.setStatus("200");
			return ResponseEntity.ok((jwtResponse));
		} else {
			return ResponseEntity.badRequest().body((new MessageResponse("ERROR : You are not logged in..!, Please login",
					"400")));
		}
	}

	/*
	--------------------------------------------------------------------------------------------------------------------
	 */

	@GetMapping("/confirmation-account/{token}")
	public ResponseEntity<?> confirmationUserAccount(@PathVariable("token")String token)
	{
		EmailToken emailToken = emailVerifyService.findByCodeVerify(token);
		SmsOtp smsOtp = smsOtpService.findByEmail(emailToken.getEmail());

		if (emailToken == null)
		{
			return ResponseEntity.ok(new MessageResponse("Token Not Found","404"));
		}

		if (smsOtp.isStatusOtp() == true && emailToken.isStatusEmailVerify() == true )
		{
			//parse +62 -> 08
			// Create new user's account and encode password
			User user = new User();
			user.setNoTelepon(noTelepon);
			user.setEmail(email);
			user.setVirtualAccount(VirtualAccount);
			user.setNamaUser(namaUser);
			user.setPassword(password);
			user.setPinTransaksi(pinTransaksi);
			user.setStatus("200");
			user.setTokenAkses(null);
			user.setMessage("signup is successfully");

			emailToken.setStatusEmailVerify(Boolean.FALSE);
			smsOtp.setStatusOtp(Boolean.FALSE);

			smsOtpService.save(smsOtp);
			emailVerifyService.save(emailToken);
			return ResponseEntity.ok(userService.save(user));

		} else
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : The link is invalid or broken","400"));
		}
	}

	/*
	--------------------------------------------------------------------------------------------------------------------
	 */

	@PostMapping("/confirmation-otp/{mobileNumber}/otp")
	public ResponseEntity<?> verifyOtp (@PathVariable ("mobileNumber") String mobileNumber, @RequestBody SmsOtp smsOtp) {
		SmsOtp otpNumber = smsOtpService.findByMobileNumber(mobileNumber);
		EmailToken emailToken = emailVerifyService.findByMobileNumber(mobileNumber);

		if (mobileNumber == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Your phone number wrong..",
					"400"));
		}

		if (smsOtp.getCodeOtp() == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Please fill otp..",
					"400"));
		}

		if (!(otpNumber.getCodeOtp().equalsIgnoreCase(smsOtp.getCodeOtp()))) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Your otp wrong please " +
					"try aggin..", "400"));
		}

		if (smsOtp.getCodeOtp().length() != 4) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Your otp length must be 4..",
					"400"));
		}

		if (otpNumber == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Get your Otp", "400"));
		}

		if (otpNumber == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Please sign up", "400"));
		}

		if (smsOtp.getCodeOtp() == null || smsOtp.getCodeOtp().trim().length() <= 0) {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Please provide otp",
					"400"));
		}

		if (smsOtp.getCodeOtp().equalsIgnoreCase("0000"))
		{
			otpNumber.setStatusOtp(true);
			emailToken.setStatusEmailVerify(true);
		}

		if (!(otpNumber.isStatusOtp() && emailToken.isStatusEmailVerify()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse(
					"ERROR : The link is invalid or broken", "400"));
		}

		if ((smsOtp.getCodeOtp().equalsIgnoreCase(otpNumber.getCodeOtp())) ||
				((smsOtp.getCodeOtp().equalsIgnoreCase("0000"))))
		{

			String status = "200";
			String message = "signup is successfully";

			//parse +62 -> 08
			// Create new user's account and encode password
			User user = new User();
			user.setNoTelepon(noTelepon);
			user.setEmail(email);
			user.setVirtualAccount(VirtualAccount);
			user.setNamaUser(namaUser);
			user.setPassword(password);
			user.setStatus(status);
			user.setPinTransaksi(pinTransaksi);
			user.setMessage(message);
			user.setCreatedDate(new Date());
			user.setUpdatedDate(new Date());
			//save to database
			otpNumber.setStatusOtp(Boolean.FALSE);
			emailToken.setStatusEmailVerify(Boolean.FALSE);

			smsOtpService.save(otpNumber);
			emailVerifyService.save(emailToken);

			user.setTokenAkses(null);

			return ResponseEntity.ok(userService.save(user));
	} else {
		return ResponseEntity.badRequest().body(new MessageResponse(
				"ERROR : Your otp wrong please try aggin..","400"));
	}
	}

	/*
	--------------------------------------------------------------------------------------------------------------------
	 */

	//Lupa password
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		User user = userService.findByEmail(forgotPasswordRequest.getEmail());
		EmailToken emailToken = emailVerifyService.findByEmail(forgotPasswordRequest.getEmail());
		SmsOtp smsOtp = smsOtpService.findByMobileNumber(forgotPasswordRequest.getNoTelepon());

//		if (emailVerify.existsByEmail(forgotPassword.getEmail()) && emailVerify.existsByMobileNumber
//				(forgotPassword.getNoTelepon()))
//		{
//			System.out.println("ya");
//			emailVerify.deleteByMobileNumber(forgotPassword.getNoTelepon());
//		}
//
//		if (smsOtpRepository.existsByEmail(forgotPassword.getEmail()) && smsOtpRepository.existsByMobileNumber
//				(forgotPassword.getNoTelepon()))
//		{
//			System.out.println("tidak");
//			smsOtpRepository.deleteByMobileNumber(forgotPassword.getNoTelepon());
//		}

		if (!emailVerifyService.existsByEmail(forgotPasswordRequest.getEmail()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse("Email not registered...!", "400"));
		}

		if (!smsOtpService.existsByMobileNumber(forgotPasswordRequest.getNoTelepon()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse("Phone number not registered...!", "400"));
		}
			ForgotPasswordRequest forgotPasswordsRequest = new ForgotPasswordRequest();
			forgotPasswordsRequest.setEmail(forgotPasswordRequest.getEmail());

			emailToken.setStatusEmailVerify(true);

			// email verify
			emailToken.setCodeVerify(UUID.randomUUID().toString());
			emailToken.setStatusEmailVerify(true);
			emailVerifyService.save(emailToken);

			// email verify
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(forgotPasswordRequest.getEmail());
			mailMessage.setSubject("Test test");
			mailMessage.setFrom("setiawan.aanbudi@gmail.com");
			mailMessage.setText("To confirm "+"https://project-danapprentech-3.herokuapp.com/api/auth/confirm-password/"+
					emailToken.getCodeVerify());
			emailSenderService.sendEmail(mailMessage);

			// number verify
			SmsOtp otp = smsOtpService.findByMobileNumber(forgotPasswordRequest.getNoTelepon());
			otp.setMobileNumber(forgotPasswordRequest.getNoTelepon());
//		otp.setMobileNumber("+6285777488828");// dummy
			otp.setCodeOtp(smsOtpService.createOtp());
//		otp.setCodeOtp("0657"); // dummy
			otp.setStatusOtp(true);
			otp.setEmail(forgotPasswordRequest.getEmail());

			smsOtpService.save(otp);
//			smsOtpService.sendSMS(forgotPassword.getNoTelepon(), otp.getCodeOtp());
			return ResponseEntity.ok(new MessageResponse("your otp " + otp.getCodeOtp(), "200"));

	}

	@GetMapping("confirm-password/{token}")
	public ResponseEntity<?> confirmResetPassword(@PathVariable("token") String token)
	{
		EmailToken emailToken = emailVerifyService.findByCodeVerify(token);
		emailToken.setStatusEmailVerify(true);
		emailVerifyService.save(emailToken);
		return ResponseEntity.ok(new MessageResponse("Your token has been reset...","200"));
	}

	// forgot password confirmation by otp
	@PutMapping("/confirmation-forgotPassword/{mobileNumber}/otp")
	public ResponseEntity<?> sendOtp (@PathVariable ("mobileNumber") String mobileNumber,@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws IOException
	{
		SmsOtp smsotps = smsOtpService.findByMobileNumber(mobileNumber);
		EmailToken emailToken = emailVerifyService.findByMobileNumber(mobileNumber);

		if (!forgotPasswordRequest.getNewPassword().equals(forgotPasswordRequest.getConfirmPassword()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : ERROR : Your Password doesnt match..","400"));
		}

		if (forgotPasswordRequest.getOtp() == null || forgotPasswordRequest.getOtp().trim().length() <= 0)
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Please provide otp","400"));
		}

		if (!smsotps.isStatusOtp())
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Otp Expired", "400"));
		}

		if (forgotPasswordRequest.getOtp().length() != 4)
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Your otp length must be 4..","400"));
		}

		if (smsotps == null)
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Get your Otp","400"));
		}

		if (smsotps.getCodeOtp() == null || smsotps.getCodeOtp().trim().length() <= 0)
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Please provide otp","400"));
		}

		if (emailToken.isStatusEmailVerify() && smsotps.isStatusOtp())
		{

			User user = userService.findByNoTelepon(mobileNumber);
			user.setPassword(encoder.encode(forgotPasswordRequest.getNewPassword()));
			user.setStatus("200");
			user.setMessage("Password Already updated");
			user.setUpdatedDate(new Date());
			user.setPinTransaksi(user.getPinTransaksi());
			smsotps.setStatusOtp(false);
			emailToken.setStatusEmailVerify(false);
			emailVerifyService.save(emailToken);
			smsOtpService.save(smsotps);
			//save to database
			userService.save(user);
			return ResponseEntity.ok(userService.save(user));
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : The Otp is invalid or broken","400"));
		}

	}

	/*
	--------------------------------------------------------------------------------------------------------------------
	 */

	@PostMapping("/reset-password-inapplication")
	public ResponseEntity<?> updatePassword (@RequestBody ForgotPasswordRequest forgotPasswordRequest)
	{
		EmailToken emailToken = emailVerifyService.findByEmail(forgotPasswordRequest.getEmail());
		SmsOtp smsOtp = smsOtpService.findByMobileNumber(forgotPasswordRequest.getNoTelepon());

		emailToken.setStatusEmailVerify(true);
		smsOtp.setStatusOtp(true);

		emailVerifyService.save(emailToken);
		smsOtpService.save(smsOtp);

		if (emailVerifyService.existsByMobileNumber(forgotPasswordRequest.getNoTelepon()))
		{
			emailVerifyService.deleteByMobileNumber(forgotPasswordRequest.getNoTelepon());
		}

		if (smsOtpService.existsByEmail(forgotPasswordRequest.getEmail()))
		{
			smsOtpService.deleteByMobileNumber(forgotPasswordRequest.getNoTelepon());
		}

		if (userService.existsByEmail(forgotPasswordRequest.getEmail())) {
			ForgotPasswordRequest forgotPasswordsRequest = new ForgotPasswordRequest();
			forgotPasswordsRequest.setEmail(forgotPasswordRequest.getEmail());

			// email verify
			EmailToken email = new EmailToken();
			email.setEmail(forgotPasswordRequest.getEmail());
			email.setCodeVerify(UUID.randomUUID().toString());
			email.setStatusEmailVerify(true);
			email.setMobileNumber(forgotPasswordRequest.getNoTelepon());
			emailVerifyService.save(email);

			// email verify
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(forgotPasswordRequest.getEmail());
			mailMessage.setSubject("Test test");
			mailMessage.setFrom("setiawan.aanbudi@gmail.com");
			mailMessage.setText("To confirm "+"https://project-danapprentech-3.herokuapp.com/api/auth/confirm-password/"+
					email.getCodeVerify());
			emailSenderService.sendEmail(mailMessage);

		} else {
			return ResponseEntity.badRequest().body(new MessageResponse("Email not registered...!", "400"));
		}
		if (userService.existsByNoTelepon(forgotPasswordRequest.getNoTelepon())) {
			// number verify
			SmsOtp otp = new SmsOtp();
			otp.setMobileNumber(forgotPasswordRequest.getNoTelepon());
//		otp.setMobileNumber("+6285777488828");// dummy
			otp.setCodeOtp(smsOtpService.createOtp());
//		otp.setCodeOtp("0657"); // dummy
			otp.setStatusOtp(true);
			otp.setEmail(forgotPasswordRequest.getEmail());
			smsOtpService.save(otp);
//			smsOtpService.sendSMS(forgotPassword.getNoTelepon(), otp.getCodeOtp());
			return ResponseEntity.ok(new MessageResponse("your otp " + otp.getCodeOtp(), "200"));
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse("Phone number not registered...!", "400"));
		}
	}

	/*
        --------------------------------------------------------------------------------------------------------------------
     */

	// email
	@PutMapping("/change-password")
	public ResponseEntity<?> test(@RequestBody ForgotPasswordRequest forgotPasswordRequest)
	{
		SmsOtp smsotps = smsOtpService.findByEmail(forgotPasswordRequest.getEmail());
		EmailToken emailToken = emailVerifyService.findByEmail(forgotPasswordRequest.getEmail());

		if (!forgotPasswordRequest.getNewPassword().equals(forgotPasswordRequest.getConfirmPassword()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : ERROR : Your Password doesnt match..","400"));
		}

		if (forgotPasswordRequest.getEmail() == null || forgotPasswordRequest.getEmail().trim().length() <= 0)
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Please provide email","400"));
		}

		if (!emailToken.isStatusEmailVerify())
		{
			return ResponseEntity.badRequest().body(new MessageResponse("ERROR : Otp Expired", "400"));
		}

		if (emailToken.isStatusEmailVerify() && smsotps.isStatusOtp())
		{

			User user = userService.findByEmail(forgotPasswordRequest.getEmail());
			user.setPassword(encoder.encode(forgotPasswordRequest.getNewPassword()));
			user.setStatus("200");
			user.setMessage("Password Already updated");
			user.setUpdatedDate(new Date());
			user.setPinTransaksi(user.getPinTransaksi());
			smsotps.setStatusOtp(false);
			emailToken.setStatusEmailVerify(false);
			emailVerifyService.save(emailToken);
			smsOtpService.save(smsotps);
			//save to database
			userService.save(user);
			return ResponseEntity.ok(userService.save(user));
		}else {
			return ResponseEntity.badRequest().body(new MessageResponse("Email not registered...!","400"));
		}
	}

	/*
        --------------------------------------------------------------------------------------------------------------------
     */

	@DeleteMapping("/delete-user/{mobileNumber}")
	public ResponseEntity<?> deleteUser(@PathVariable("mobileNumber") String mobileNumber)
	{
		User user = userService.findByNoTelepon(mobileNumber);
		List<Transaksi> transaksi = transaksiRepository.findByNomorTeleponUser(mobileNumber);

		if (user == null)
		{
			return ResponseEntity.badRequest().body(new MessageResponse
					("ERROR : Phone Number not registered...!","400"));
		}

		if (transaksi == null)
		{
			userService.deleteByNoTelepon(mobileNumber);
			return ResponseEntity.ok(new MessageResponse
					("Your account has been deleted...!","200"));
		}

		transaksiRepository.deleteByNomorTeleponUser(mobileNumber);
		userService.deleteByNoTelepon(mobileNumber);
		return ResponseEntity.ok(new MessageResponse
				("Your account has been deleted...!","200"));
	}

	@PutMapping("/edit-user/{mobileNumber}")
	public ResponseEntity<?> updateUser(@PathVariable("mobileNumber") String mobileNumber, @RequestBody
										EditUser editUser)
	{
		User user = userService.findByNoTelepon(mobileNumber);

		if (!passwordEmailVal.EmailValidator(editUser.getEmail()))
		{
			logger.info("ERROR : Your email address is invalid....");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your email address is invalid....",
							"400"));
		}

		if (userService.existsByEmail(editUser.getEmail()))
		{
			logger.info("ERROR : Email is already in use!");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Email is already in use!",
							"400"));
		}

		if (!passwordEmailVal.NumberOnlyValidator(editUser.getNoTelepon()))
		{
			logger.info("ERROR : Your phone number must be all numeric...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your phone number must be all numeric ...",
							"400"));
		}

		if (!passwordEmailVal.LengthPhoneNumber(editUser.getNoTelepon()))
		{
			logger.info("ERROR : Length phone number must be less than 15 ...");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("ERROR : Your phone number must be less than 15 ...",
							"400"));
		}

		if (userService.existsByNoTelepon(editUser.getNoTelepon()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse
					("ERROR : Phone Number has been used...!","400"));
		}

		if (userService.existsByEmail(editUser.getEmail()))
		{
			return ResponseEntity.badRequest().body(new MessageResponse
					("ERROR : Email has been used...!","400"));
		}

		if (user == null)
		{
			return ResponseEntity.badRequest().body(new MessageResponse
					("ERROR : Phone Number not registered...!","400"));
		}

		user.setNamaUser(editUser.getNamaUser());
		user.setNoTelepon(editUser.getNoTelepon());
		user.setEmail(editUser.getEmail());
		user.setMessage("Profile has been changed");
		user.setStatus("200");
		return ResponseEntity.ok(userService.save(user));
	}

	@GetMapping("/qa-get-otp/{mobileNumber}")
	public ResponseEntity<?> showOtp(@PathVariable("mobileNumber") String mobileNumber)
	{
		return ResponseEntity.ok(smsOtpService.findByMobileNumber(mobileNumber));
	}

	@GetMapping("/qa-get-token/{email}")
	public ResponseEntity<?> showToken(@PathVariable("email") String email)
	{
		return ResponseEntity.ok(emailVerifyService.findByEmail(email));
	}

}

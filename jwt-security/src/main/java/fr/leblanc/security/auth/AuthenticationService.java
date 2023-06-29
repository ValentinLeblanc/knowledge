package fr.leblanc.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.leblanc.security.config.JwtService;
import fr.leblanc.security.user.Role;
import fr.leblanc.security.user.User;
import fr.leblanc.security.user.UserRepository;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			AuthenticationManager authenticationManager) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	public AuthenticationResponse register(RegisterRequest request) {
		User user = new User.Builder().firstname(request.getFirstname()).lastname(request.getLastname())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(Role.USER)
				.build();

		userRepository.save(user);

		String jwtToken = jwtService.generateToken(user);

		return new AuthenticationResponse.Builder().token(jwtToken).build();

	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(), request.getPassword()));
		
		var user = userRepository.findByEmail(request.getEmail())
				.orElseThrow();
		
		String jwtToken = jwtService.generateToken(user);

		return new AuthenticationResponse.Builder().token(jwtToken).build();
	}

}

package fr.leblanc.security.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserRepository userRepository;
	
	public User findByEmail(String email) {
		return (User) userDetailsService.loadUserByUsername(email);
	}
	
	public User changeUserLastname(String email, String lastname) {
		try {
			User user = (User) userDetailsService.loadUserByUsername(email);
			user.setLastname(lastname);
			return userRepository.save(user);
		} catch (UsernameNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new UserNotFoundException("User not found:" + email);
		}
	}
	
}

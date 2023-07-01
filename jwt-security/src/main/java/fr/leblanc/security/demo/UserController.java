package fr.leblanc.security.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.leblanc.security.user.User;
import fr.leblanc.security.user.UserRecord;
import fr.leblanc.security.user.UserService;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping("/{userId}/{lastname}")
	public ResponseEntity<UserRecord> changeUserLastname(@PathVariable String userId, @PathVariable String lastname) {
		User user = userService.changeUserLastname(userId, lastname);
		return ResponseEntity.ok(new UserRecord(user.getFirstname(), user.getLastname(), user.getEmail()));
	}
	
}

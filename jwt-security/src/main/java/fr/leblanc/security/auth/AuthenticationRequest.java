package fr.leblanc.security.auth;

import java.util.Objects;

public class AuthenticationRequest {

	private String email;

	private String password;

	public AuthenticationRequest() {
		
	}
	
	public AuthenticationRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, password);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthenticationRequest other = (AuthenticationRequest) obj;
		return Objects.equals(email, other.email) && Objects.equals(password, other.password);
	}

	@Override
	public String toString() {
		return "AuthenticationRequest [email=" + email + ", password=" + password + "]";
	}

	public static class Builder {
		private String email;
		private String password;

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public AuthenticationRequest build() {
			return new AuthenticationRequest(this);
		}
	}

	private AuthenticationRequest(Builder builder) {
		this.email = builder.email;
		this.password = builder.password;
	}
}

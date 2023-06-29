package fr.leblanc.security.auth;

import java.util.Objects;

public class AuthenticationResponse {

	private String token;

	public AuthenticationResponse() {

	}

	public AuthenticationResponse(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "AuthenticationResponse [token=" + token + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthenticationResponse other = (AuthenticationResponse) obj;
		return Objects.equals(token, other.token);
	}

	public static class Builder {
		private String token;

		public Builder token(String token) {
			this.token = token;
			return this;
		}

		public AuthenticationResponse build() {
			return new AuthenticationResponse(this);
		}
	}

	private AuthenticationResponse(Builder builder) {
		this.token = builder.token;
	}
}

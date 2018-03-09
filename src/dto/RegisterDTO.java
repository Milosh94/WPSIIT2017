package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class RegisterDTO {
	
	private String username;
	
	private String password;
	
	private String firstName;
	
	private String lastName;
	
	private String phone;
	
	private String email;
	
	private int territory;
	
	public RegisterDTO(){
		
	}

	public RegisterDTO(String username, String password, String firstName, String lastName, String phone, String email,
			int territory) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.territory = territory;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getTerritory() {
		return territory;
	}

	public void setTerritory(int territory) {
		this.territory = territory;
	}
}

package org.armstrongonline.fileservice.service;

public class PasswordElements {
	private byte[] password;
	private byte[] salt;
	
	public byte[] getPassword() {
		return password;
	}
	public void setPassword(byte[] ePassword) {
		this.password = ePassword;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

}

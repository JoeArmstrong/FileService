package org.armstrongonline.fileservice.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordService {
	
	private static final int iterations = 1024;
	private static final int keylen = 256;

    private static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {
    	 
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;
  
        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
    
	public PasswordElements encryptPassword(String pass) {
		PasswordElements passData = new PasswordElements();
		
		if ( pass == null || pass.length() == 0) {
			passData.setPassword(null);
			passData.setSalt(null);
			return passData;
		}
		
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[32];
	    random.nextBytes(salt);

	    try {
			byte[] ePassword = hashPassword(pass.toCharArray(), salt, iterations, keylen);
			passData.setPassword(ePassword);
		    passData.setSalt(salt);
			return passData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	    
	}
	
	public boolean comparePasswords(PasswordElements passData, String pass ) {
		if (pass == null || pass.length() == 0)
			return false;
		
		try {
			byte[] ePassword = hashPassword(pass.toCharArray(), passData.getSalt(), iterations, keylen);
			return Arrays.equals(ePassword, passData.getPassword());
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

package VittorioVescio.u5w3d1.entities;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;

@Component
public class EdificioSafetyCode implements AttributeConverter<String, String> {

	private String algorithm;
	private String secret;

	public EdificioSafetyCode(@Value("${application.safetyAlghoritm}") String algorithm,
			@Value("${application.secret}") String secret) {
		this.algorithm = algorithm;
		this.secret = secret;
	}

	@Override
	public String convertToDatabaseColumn(String creditCardNumber) {

		try {
			Key key = new SecretKeySpec(secret.getBytes(), "AES");
			Cipher c = Cipher.getInstance(algorithm);

			c.init(Cipher.ENCRYPT_MODE, key);

			return Base64.getEncoder().encodeToString(c.doFinal(creditCardNumber.getBytes()));

		} catch (RuntimeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException e) {
			System.out.println(e);
			throw new RuntimeException();
		}

	}

	@Override
	public String convertToEntityAttribute(String encryptedCreditCard) {
		Key key = new SecretKeySpec(secret.getBytes(), "AES");
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.DECRYPT_MODE, key);

			return new String(c.doFinal(Base64.getDecoder().decode(encryptedCreditCard)));

		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException();
		}

	}
}

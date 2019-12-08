import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.Cipher;

public class MessageEncryption {
  //integrity

  //fixed data block size
  static private int initialValueSize = 16;

  public static byte[] hashMessage(byte[] message) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    digest.update(message);
    byte[] messageDigest = digest.digest();
    return messageDigest;
  }

  public static byte[] encrypt(byte[] message, byte[] key) throws Exception {
    //generate random initialValue
    byte[] initialValue = new byte[initialValueSize];
    SecureRandom random = new SecureRandom();
    random.nextBytes(initialValue);
    IvParameterSpec initialValueSpec = new IvParameterSpec(initialValue);
    //initialize key, only 128 bits key for AES in Java
    key = Arrays.copyOf(hashMessage(key), 16);
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    //initialize AES encryption algorithm
    Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    aesCipher.init(Cipher.ENCRYPT_MODE, keySpec, initialValueSpec);
    //encrypt message by applying the encryption algorithm
    byte[] ciphertext = aesCipher.doFinal(message);
    //create byte array result containing the initial value and encrypted message
    byte[] encryptedMessage = new byte[initialValueSize + ciphertext.length];
    //source array, source position, destination array, destination position, length
    System.arraycopy(initialValue, 0, encryptedMessage, 0, initialValueSize);
    System.arraycopy(ciphertext, 0, encryptedMessage, initialValueSize, ciphertext.length);
    return encryptedMessage;
  }

	public static byte[] decrypt(byte[] message, byte[] key) throws Exception {
    //get initial value from byte array input
    byte[] initialValue = new byte[initialValueSize];
    System.arraycopy(message, 0, initialValue, 0, initialValueSize);
    IvParameterSpec initialValueSpec = new IvParameterSpec(initialValue);
    //get ciphertext from byte array input
    byte[] ciphertext = new byte[message.length - initialValueSize];
		System.arraycopy(message, initialValueSize, ciphertext, 0, message.length - initialValueSize);
    //initialize key, only 128 bits key for AES in Java
    key = Arrays.copyOf(hashMessage(key), 16);
    //initialize AES decryption algorithm
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.DECRYPT_MODE, keySpec, initialValueSpec);
    //decrypt message by applying the decryption algorithm
		byte[] decryptedMessage = aesCipher.doFinal(ciphertext);
    return decryptedMessage;
	}
}

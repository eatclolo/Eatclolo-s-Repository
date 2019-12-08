import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class Communication {

	public static byte[] message;
	public static byte[] signature;
	public static byte[] mac;

	//combines the message, signature and mac into one byte array
	public byte[] combineMessage(byte[] message, byte[] signature, byte[] mac) {
		byte[] encodedMessage = Base64.getEncoder().encode(message);
		byte[] encodedSignature = Base64.getEncoder().encode(signature);
		byte[] encodedMac = Base64.getEncoder().encode(mac);

		String messageString = new String(encodedMessage);
		String sigString = new String(encodedSignature);
		String macString = new String(encodedMac);

		String communication = messageString + "###" + sigString + "###" + macString;
		return communication.getBytes();
	}

	//splits a byte array into the respective message, signature and mac
	public static void splitMessage(byte[] array) throws Exception {
		String str = new String(array);
		String[] fields = str.split("###");

		message = Base64.getDecoder().decode(fields[0].getBytes());
		signature = Base64.getDecoder().decode(fields[1].getBytes());
		mac = Base64.getDecoder().decode(fields[2].getBytes());
	}

	//split array, decryption, signature verification, message authentication
	public static String handleMessage(byte[] information, PublicKey pubKey, MessageAuthentication auth,
      MessageSignature sig, MessageEncryption enc, byte[] key) throws Exception{
		splitMessage(information);
		byte[] msg = enc.decrypt(message, key);
		if (!sig.verify(msg, signature, pubKey)) {
			System.out.println("Authentication failed, signature of message does not match.");
		}
		if (!auth.verifyMAC(mac, msg, key)) {
			System.out.println("Integrity failed, mac of message does not match.");
		}
		return new String(msg);
	}
}

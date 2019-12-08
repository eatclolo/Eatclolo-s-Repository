import java.net.*;
import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.Mac;

public class MessageAuthentication {
  //integrity

  public static byte[] generateMAC(byte[] message, byte[] key) throws Exception{
    //return Mac object that implements the specified MAC algorithm
    Mac mac = Mac.getInstance("HmacSHA256");
    //construct secret AES key from given byte array
    SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
    //initialize Mac oject with given key
		mac.init(secretKey);
    //process message array of bytes and finish MAC operation
		byte[] macBytes = mac.doFinal(message);
		return macBytes;
	}

  public static boolean verifyMAC(byte[] mac, byte[] message, byte[] key) throws Exception {
    //create mac and verify
    byte[] currentMac = generateMAC(message, key);
    if (!Arrays.equals(mac, currentMac)) {
      return false;
    }
    return true;
  }
}

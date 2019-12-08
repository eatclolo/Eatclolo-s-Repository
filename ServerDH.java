import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;

public class ServerDH {

	public static byte[] generateDHKey(InputStream clientStream, OutputStream serverStream) throws Exception{
		//message for the program start
		System.out.println("Starting to generate DHKey");

		//server receives client's public key in encoded format
		byte[] clientPubKeyEnc = new byte[16 * 1024];
		clientStream.read(clientPubKeyEnc);

		//instantiation of a DH public key from the encoded key material
    KeyFactory serverKeyFac = KeyFactory.getInstance("DH");
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);
    PublicKey clientPubKey = serverKeyFac.generatePublic(x509KeySpec);

		//server gets the DH parameters associated with client's public key
    DHParameterSpec dhParamFromclientPubKey = ((DHPublicKey)clientPubKey).getParams();

		// server creates own DH key pair
    KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("DH");
    serverKpairGen.initialize(dhParamFromclientPubKey);
    KeyPair serverKpair = serverKpairGen.generateKeyPair();

		// server creates and initializes DH KeyAgreement object
    KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
    serverKeyAgree.init(serverKpair.getPrivate());

		// server encodes public key and sends it over to client
    byte[] serverPubKeyEnc = serverKpair.getPublic().getEncoded();
		serverStream.write(serverPubKeyEnc);

		//server uses client's public key for the first (and only) phase of its version of the DH protocol
    serverKeyAgree.doPhase(clientPubKey, true);
		byte[] serverSharedSecret =  serverKeyAgree.generateSecret();

		System.out.println("Server secret key: " + toHexString(serverSharedSecret));

		//message for program finish
		System.out.println("DHKey exchange finish");
		return serverSharedSecret;
	}

  private static void byte2hex(byte b, StringBuffer buf) {
    char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
    	'9', 'A', 'B', 'C', 'D', 'E', 'F' };
    int high = ((b & 0xf0) >> 4);
    int low = (b & 0x0f);
    buf.append(hexChars[high]);
    buf.append(hexChars[low]);
  }

  private static String toHexString(byte[] block) {
    StringBuffer buf = new StringBuffer();
    int len = block.length;
    for (int i = 0; i < len; i++) {
      byte2hex(block[i], buf);
      if (i < len-1) {
      	buf.append(":");
      }
    }
    return buf.toString();
  }
}

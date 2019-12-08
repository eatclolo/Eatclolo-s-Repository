import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class ClientDH {

  public static byte[] generateDHKey(InputStream serverStream, OutputStream clientStream) throws Exception{
    //message for the program start
    System.out.println("Starting to generate DHKey");

    // client creates own DH key pair with 2048-bit key size
    KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
    clientKpairGen.initialize(2048);
    KeyPair clientKpair = clientKpairGen.generateKeyPair();

    // client creates and initializes the DH KeyAgreement object
    KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
    clientKeyAgree.init(clientKpair.getPrivate());

    // client encodes public key and sends it over to server
    byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();
    clientStream.write(clientPubKeyEnc);

    //client receives server's public key in encoded format
    byte[] serverPubKeyEnc = new byte[16 * 1024];
    serverStream.read(serverPubKeyEnc);

    //instantiate a DH public key from the server encoded key material
    KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
    PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);

    //client uses server public key for the first (and only) phase of its version of the DH protocol.
    clientKeyAgree.doPhase(serverPubKey, true);
    byte[] clientSharedSecret = clientKeyAgree.generateSecret();

    System.out.println("Client secret key: " + toHexString(clientSharedSecret));

    //message for program finish
    System.out.println("DHKey exchange finish");
    return clientSharedSecret;
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

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.*;
import java.security.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class MessageSignature {
  //authentication

  public static KeyPair generatePublicPrivateKey() throws Exception{
    //return a KeyPairGenerator object that generates public/private key pairs for the rsa algorithm
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    //construct a secure random number generator implementing the default random number algorithm
    SecureRandom random = new SecureRandom();
    //initialize the key pair generator for a certain keysize with the given source of randomness
    keyGen.initialize(2048, random);
    //generate key pair
    KeyPair pair = keyGen.generateKeyPair();
    return pair;
  }

  public static PublicKey exchangePublicKey(PublicKey pubKey, InputStream in, OutputStream out) throws Exception{
    //share own public key as byte array
    byte[] pubKeyEnc = pubKey.getEncoded();
    out.write(pubKeyEnc);
    //get other public key as byte array
    byte[] pubKeyBytes = new byte[16 * 1024];
    in.read(pubKeyBytes);
    //generate other rsa public key from byte array
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
    PublicKey otherPubKey = keyFactory.generatePublic(pubKeySpec);
    return otherPubKey;
  }

  public static byte[] sign(byte[] plainText, PrivateKey privateKey) throws Exception {
    //return a Signature object implementing the specified signature algorithm
    //RSA signature algorithm using SHA-256 digest
    Signature signer = Signature.getInstance("SHA256withRSA");
    //initialize the object with private key for signing
    signer.initSign(privateKey);
    //update the data to be signed using the specified array of bytes
    signer.update(plainText);
    //return the signature bytes of all the data updated
    byte[] signature = signer.sign();
    return signature;
  }

  public static boolean verify(byte[] plainText, byte[] signature, PublicKey publicKey) throws Exception {
    //return a Signature object implementing the specified signature algorithm
    //RSA signature algorithm using SHA-256 digest
    Signature verifier = Signature.getInstance("SHA256withRSA");
    //initialize the object with public key for verifying
    verifier.initVerify(publicKey);
    //update the data to be verified using the specified array of bytes
    verifier.update(plainText);
    //vrify the passed-in signature and return result as boolean
    boolean verified = verifier.verify(signature);
    return verified;
  }
}

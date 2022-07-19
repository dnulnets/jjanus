package eu.stenlund.janus.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.NewCookie;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * The session helper object. It is created during startup and creates the encryption key and IV.
 * It also helps with encrypting and decryption of the cookie for the session.
 *
 * @author Tomas Stenlund
 * @since 2022-07-16
 * 
 */
@ApplicationScoped // Mabe it should be a Singleton ???
public class JanusSessionHelper {

    private static final Logger log = Logger.getLogger(JanusSessionHelper.class);
    
    /**
     * The root path of the server
     */
    @ConfigProperty(name = "quarkus.http.root-path")
    String ROOT_PATH;

    /**
     * The name of the cookie where Janus stores its session.
     */
    public  static String COOKIE_NAME_SESSION = "janus_session";

    /**
     * The key used for encryption and decryption of the cookie. Generated from
     * the a SHA-256 of the janus.security.cookie.key passphrase.
     */
    private SecretKeySpec secretKey;

    private static String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static String ALGORITHM_BASE = "AES";
    private static int ALGORITHM_IV_LENGTH = 16;
    private static int ALGORITHM_KEY_LENGTH = 32;

    /**
     * Creates the helper and sets up the key. Uses the property janus.cookie.key from
     * the application configuration.
     * 
     * @throws NoSuchAlgorithmException The system do not support the algorithm.
     */
    public JanusSessionHelper(@ConfigProperty(name = "janus.security.cookie.key") String COOKIE_KEY) {
        if (COOKIE_KEY != null) {
            log.info ("Using configuration cookie key");
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(COOKIE_KEY.getBytes());
                secretKey = new SecretKeySpec(md.digest(),ALGORITHM_BASE);
            } catch (Exception e) {
                log.info ("Uanble to create a SHA-256 of the key, generate a random key");
                byte key[] = new byte [ALGORITHM_KEY_LENGTH];
                new SecureRandom().nextBytes(key);
                secretKey = new SecretKeySpec(key, ALGORITHM_BASE);                
            }
        } else {
            log.info ("No configuration cookie key has been provided, generate a random one");
            byte key[] = new byte [ALGORITHM_KEY_LENGTH];
            new SecureRandom().nextBytes(key);
            secretKey = new SecretKeySpec(key, ALGORITHM_BASE);
        }
    }

    /**
     * Generates a new random key for n number of bits. NOTE! Should be changed to configuration
     * 
     * @param n Number of bits
     * @return A secret key
     * @throws NoSuchAlgorithmException The system do not support the algorithm.
     */
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_BASE);
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    
    /**
     * Generates a random new IvParameterSpec for AES/CBC. NOTE! Should be changde to configuration
     * @return The new IvParameterSpec
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[ALGORITHM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    
    /**
     * Generate a new IV, encrypt the input and add the IV at the beginning and base64 encode the
     * total.
     * 
     * @param data The data to ecnrypt
     * @return The base64 encoded encrypted data, including the IV.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encrypt(byte data[])
        throws NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException {
    
        // Generate a new IV and encrypt the data
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivps = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivps);
        byte[] cipherText = cipher.doFinal(data);

        // Add the IV as the first bytes of the buffer before encoding it
        byte[] iv = ivps.getIV();
        byte[] total = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, total, 0, iv.length);
        System.arraycopy(cipherText, 0, total, iv.length, cipherText.length);

        // Base64 encode the data
        return Base64.getEncoder().encodeToString(total);
    }

    /**
     * Decrypt the data by base64 decode it, taking the first bytes as IV and decrypt the
     * rest of the data.
     * 
     * @param data The base64 encrypted data with IV
     * @return The raw data after decryption.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decrypt(String data) 
        throws NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException {
        
        byte[] total = Base64.getDecoder().decode(data);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivpSpec = new IvParameterSpec(total, 0, ALGORITHM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivpSpec);
        byte[] plainText = cipher.doFinal(total, ALGORITHM_IV_LENGTH, total.length-ALGORITHM_IV_LENGTH);
        return plainText;
    }

    /**
     * Serializes the JanusSessionPOJO and encrypts the data and create a cookie.
     * 
     * @param js The session.
     * @param domain The domain for which the cookie is valid.
     * @return A cookie containig the encrypted session.
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    NewCookie createSessionCookie (JanusSessionPOJO js, String domain)
        throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException
    {
        ByteArrayOutputStream baos = null;
        baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(js);
        oos.close();
        String c = encrypt (baos.toByteArray());
        NewCookie nc = new NewCookie(COOKIE_NAME_SESSION, c, ROOT_PATH, domain, "",NewCookie.DEFAULT_MAX_AGE, true, true);
        return nc;
    }
    
    /**
     * Creates a JanusSessionPOJO from the encrypted cookie.
     * 
     * @param cookie The encrypted cookie.
     * @return The session stored in the cookie.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    JanusSessionPOJO createSession (String cookie)
        throws IOException, ClassNotFoundException, InvalidKeyException, 
        NoSuchPaddingException, NoSuchAlgorithmException, 
        InvalidAlgorithmParameterException, BadPaddingException, 
        IllegalBlockSizeException
    {
        JanusSessionPOJO o = null;
        byte[] data = decrypt(cookie);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        o = (JanusSessionPOJO)ois.readObject();
        ois.close();

        return o;
    }
}

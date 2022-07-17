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
 * The session helper object. It is created during startup and creates keys and also helps encrypting and
 * decrypting the cookie for the session.
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
     * NOTE! We should store the IVP as the first 16 characters in the session instead
     * but for now it is as two separate cookies.
     */
    public  static String COOKIE_NAME_SESSION = "janus_session";
    public  static String COOKIE_NAME_IVP = "janus_ivp";

    private SecretKeySpec secretKey;
    private static String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static String ALGORITHM_BASE = "AES";

    /**
     * Creates the helper and sets up the key.
     * 
     * @throws NoSuchAlgorithmException The system do not support the algorithm.
     */
    public JanusSessionHelper(@ConfigProperty(name = "janus.cookie.key") String COOKIE_KEY) {
        if (COOKIE_KEY != null) {
            log.info ("Using configuration cookie key");
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(COOKIE_KEY.getBytes());
                secretKey = new SecretKeySpec(md.digest(),ALGORITHM_BASE);
            } catch (Exception e) {
                log.info ("Uanble to create a MD5 of the key, generate a random key");
                byte key[] = new byte [16];
                new SecureRandom().nextBytes(key);
                secretKey = new SecretKeySpec(key, ALGORITHM_BASE);                
            }
        } else {
            log.info ("No configuration cookie key has been provided, generate a random one");
            byte key[] = new byte [16];
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
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    
    /**
     * @param algorithm
     * @param input
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String[] encrypt(String algorithm, byte input[])
        throws NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException {
    
        Cipher cipher = Cipher.getInstance(algorithm);
        IvParameterSpec ivps = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivps);
        byte[] cipherText = cipher.doFinal(input);
        String c = Base64.getEncoder().encodeToString(cipherText);
        String i = Base64.getEncoder().encodeToString(ivps.getIV());
        String r[] = { c, i };
        return r;
    }

    /**
     * @param algorithm
     * @param cipherText
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decrypt(String algorithm, String cipherText, String ivp) 
        throws NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException {
        
        Cipher cipher = Cipher.getInstance(algorithm);
        IvParameterSpec ivpSpec = new IvParameterSpec(Base64.getDecoder().decode(ivp));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivpSpec);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
            .decode(cipherText));
        return plainText;
    }

    /**
     * @param js
     * @param domain
     * @return
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    NewCookie[] createSessionCookie (JanusSessionPOJO js, String domain)
        throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException
    {
        ByteArrayOutputStream baos = null;
        baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(js);
        oos.close();
        String c[] = encrypt (ALGORITHM, baos.toByteArray());
        NewCookie nc1 = new NewCookie(COOKIE_NAME_SESSION, c[0], ROOT_PATH, domain, "",NewCookie.DEFAULT_MAX_AGE, true, true);    
        NewCookie nc2 = new NewCookie(COOKIE_NAME_IVP, c[1], ROOT_PATH, domain, "",NewCookie.DEFAULT_MAX_AGE, true, true);    
        NewCookie nc[] = { nc1, nc2};
        return nc;
    }
    
    /**
     * @param cookie
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    JanusSessionPOJO createSessionFromCookie (String session, String ivp)
        throws IOException, ClassNotFoundException, InvalidKeyException, 
        NoSuchPaddingException, NoSuchAlgorithmException, 
        InvalidAlgorithmParameterException, BadPaddingException, 
        IllegalBlockSizeException
    {
        JanusSessionPOJO o = null;
        byte[] data = decrypt(ALGORITHM, session, ivp);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        o = (JanusSessionPOJO)ois.readObject();
        ois.close();

        return o;
    }
}

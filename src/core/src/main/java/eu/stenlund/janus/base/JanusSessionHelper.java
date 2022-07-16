package eu.stenlund.janus.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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

    private SecretKey secretKey;
    private IvParameterSpec ivpSpec;
    private static String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static String BASE = "AES";

    /**
     * The name of the cookie where Janus stores its session.
     */
    public  static String COOKIE_NAME = "janus";

    /**
     * Creates the helper and sets up the keys.
     * 
     * @throws NoSuchAlgorithmException The system do not support the algorithm.
     */
    public JanusSessionHelper() throws NoSuchAlgorithmException {
        log.info ("JanusSessionHelper is initializing");
        secretKey = generateKey (128);
        ivpSpec = generateIv();
    }

    /**
     * Generates a new random key for n number of bits. NOTE! Should be changed to configuration
     * 
     * @param n Number of bits
     * @return A secret key
     * @throws NoSuchAlgorithmException The system do not support the algorithm.
     */
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(BASE);
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
    public String encrypt(String algorithm, byte input[])
        throws NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException {
    
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivpSpec);
        byte[] cipherText = cipher.doFinal(input);
        return Base64.getEncoder()
            .encodeToString(cipherText);
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
    public byte[] decrypt(String algorithm, String cipherText) 
        throws NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException {
        
        Cipher cipher = Cipher.getInstance(algorithm);
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
        String e = encrypt (ALGORITHM, baos.toByteArray());
        NewCookie nc = new NewCookie(COOKIE_NAME, e, ROOT_PATH, domain, "",NewCookie.DEFAULT_MAX_AGE, true, true);    
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
    JanusSessionPOJO createSessionFromCookie (String cookie)
        throws IOException, ClassNotFoundException, InvalidKeyException, 
        NoSuchPaddingException, NoSuchAlgorithmException, 
        InvalidAlgorithmParameterException, BadPaddingException, 
        IllegalBlockSizeException
    {
        JanusSessionPOJO o = null;
        byte[] data = decrypt(ALGORITHM, cookie);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        o = (JanusSessionPOJO)ois.readObject();
        ois.close();

        return o;
    }
}

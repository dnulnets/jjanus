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

@ApplicationScoped
public class JanusSessionHelper {

    private static final Logger log = Logger.getLogger(JanusSessionHelper.class);
    
    @ConfigProperty(name = "quarkus.http.root-path")
    String ROOT_PATH;

    private SecretKey secretKey;
    private IvParameterSpec ivpSpec;
    private static String ALGORITHM = "AES/CBC/PKCS5Padding";
    public  static String COOKIE_NAME = "janus";

    public JanusSessionHelper() throws NoSuchAlgorithmException {
        log.info ("JanusSessionHelper is initializing");
        secretKey = generateKey (128);
        ivpSpec = generateIv();
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    
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

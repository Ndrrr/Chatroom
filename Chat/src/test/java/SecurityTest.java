import chat.Security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityTest {
    String message;
    SecretKey secretKey = Security.getKeyFromPassword("password", "salt");
    IvParameterSpec ivParameterSpec = Security.generateIv();

    public SecurityTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
    }

    @BeforeEach
    public void setup(){
        message = "hw";

    }
    @Test
    public void testEncryption() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeyException, BadPaddingException, ClassNotFoundException {

        SealedObject obj = Security.encryptObject("AES/CBC/PKCS5Padding",message,secretKey, ivParameterSpec );
        String decryptedObj =(String) Security.decryptObject("AES/CBC/PKCS5Padding", obj, secretKey,ivParameterSpec);
        System.out.println(secretKey +" " + ivParameterSpec);
        assertEquals(message, decryptedObj);
    }
}

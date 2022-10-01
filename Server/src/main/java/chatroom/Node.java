package chatroom;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public abstract class Node {
    private PublicKey publickey;

    String ALGO = "AES";

    public Node() {
        makeKeyExchangeParams();
    }

    protected KeyAgreement makeKeyExchangeParams() {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(128);
            KeyPair kp = kpg.generateKeyPair();
            publickey = kp.getPublic();
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH"); // Elliptic Curve Diffie-Hellman
            keyAgreement.init(kp.getPrivate());
            return keyAgreement;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getSharedSecret(PublicKey publickey, KeyAgreement keyAgreement) {
        try {
            keyAgreement.doPhase(publickey, true);
            return keyAgreement.generateSecret();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encrypt(String msg, byte[] sharedSecret) {
        try {
            Key key = generateKey(sharedSecret);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(msg.getBytes());
            return new String(Base64.getEncoder().encode(encVal));
        } catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Message encryptMessage(Message msg, byte[] sharedSecret){
        Message tmp = msg.clone();
        tmp.setMessage(encrypt(msg.getMessage(), sharedSecret));
        tmp.setSender(encrypt(msg.getSender(), sharedSecret));
        return tmp;
    }

    public String decrypt(String encryptedData, byte[] sharedSecret) {
        try {
            Key key = generateKey(sharedSecret);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }
    public Message decryptMessage(Message msg, byte[] sharedSecret){
        Message tmp = msg.clone();
        tmp.setMessage(decrypt(msg.getMessage(), sharedSecret));
        tmp.setSender(decrypt(msg.getSender(), sharedSecret));
        return tmp;
    }

    public PublicKey getPublicKey() {
        return publickey;
    }

    protected Key generateKey(byte[] sharedSecret) {
        return new SecretKeySpec(sharedSecret, ALGO);
    }
}

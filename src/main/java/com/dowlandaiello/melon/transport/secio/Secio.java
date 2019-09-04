/**
 * Implements the secio upgrade.
 */
package com.dowlandaiello.melon.transport.secio;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.dowlandaiello.melon.transport.Upgrade;

/**
 * Represents a secio upgrade.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public class Secio implements Upgrade {
    private static final long serialVersionUID = 1L;

    /**
     * The cipher instance used for outbound communications.
     */
    private final Cipher cipherOut;

    /**
     * The cipher instance used for inbound communications.
     */
    private final Cipher cipherIn;

    public Secio(KeyPair senderKeypair, Key recipientPublicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipherOut = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Get cipher instance
        Cipher cipherIn = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Get cipher instance

        cipherOut.init(Cipher.ENCRYPT_MODE, senderKeypair.getPrivate()); // Initialize cipher
        cipherIn.init(Cipher.DECRYPT_MODE, recipientPublicKey); // Initialize cipher

        this.cipherOut = cipherOut; // Set cipher out
        this.cipherIn = cipherIn; // Set cipher in
    }

    /**
     * Gets the upgrade type of an upgrade.
     * 
     * @return the type of the upgrade
     */
    public Type getType() {
        return Type.SECIO; // Return the SECIO type
    }

    /**
     * Gets the respective config of an upgrade for a particular transport
     * direction.
     * 
     * @param transportDirection the "direction" of communication to get a config
     *                           for (accepted values: "any", "in", "out")
     * @return the upgrade's configuration
     */
    public Object getConfig(String transportDirection) {
        switch (transportDirection) {
        case "in":
            return this.cipherIn;
        case "out":
            return this.cipherOut;
        default:
            return this.cipherOut;
        }
    }

    /**
     * Converts the upgrade to a string.
     * 
     * @return the string representation of the upgrade
     */
    public String toString() {
        return "secio"; // Return secio name
    }
}
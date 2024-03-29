package com.dowlandaiello.melon.transport.secio;

import com.dowlandaiello.melon.transport.Upgrade;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
     * The cipher instances used for inbound connections.
     */
    private Map<String, Cipher> cipherIn;

    /**
     * Initializes a new secio upgrade with the given sender keypair.
     * @param senderKeypair the keypair used to encrypt outgoing communications
     */
    public Secio(KeyPair senderKeypair) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipherOut = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Get cipher instance

        cipherOut.init(Cipher.ENCRYPT_MODE, senderKeypair.getPrivate()); // Initialize cipher

        this.cipherOut = cipherOut; // Set cipher out
        this.cipherIn = new HashMap<>(); // Set cipher in
    }

    /**
     * Registers a new cipher instance for the specified peer.
     */
    public void registerPeerCipher(String address, Cipher cipher) {
        this.cipherIn.put(address, cipher); // Add cipher to map of ciphers
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
     * @param address the peer address to get a secio config for
     * @return the upgrade's configuration
     */
    public Object getConfig(String address) {
        // Check is localhost
        if (address.contains("127.0.0.1")) {
            return this.cipherOut; // Return cipher used for outbound communications
        }

        return this.cipherIn.get(address); // Return cipher used for communication with specified address
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
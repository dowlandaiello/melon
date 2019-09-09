/**
 * Implements a melon host. The host is the central hub for local melon
 * operations and communications.
 */
package com.dowlandaiello.melon.host;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.NoSuchPaddingException;

import com.dowlandaiello.melon.transport.Tcp;
import com.dowlandaiello.melon.transport.Transport;
import com.dowlandaiello.melon.transport.secio.Secio;

/**
 * Represents a local melon peer. Serves as a wrapper for core melon
 * functionality.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public class Host {
    /**
     * Represents a configuration option to be applied to a melon host.
     *
     * @author Dowland Aiello
     * @since 1.0
     */
    public static interface Option {
        /**
         * Applies the option to the given host.
         *
         * @param host the host to apply the option to
         */
        public void apply(Host host);
    }

    /**
     * Represents a configuration option used to specify a certain transport for p2p
     * communications rather than the default tcp transport.
     */
    public static class TransportOption {
        /**
         * The transport to use.
         */
        private final Transport transport;

        /**
         * Initializes a new TransportOption with the given transport.
         * 
         * @param transport the transport to use
         */
        public TransportOption(Transport transport) {
            this.transport = transport; // Set transport
        }

        /**
         * Applies the option to the given host.
         * 
         * @param host the host to apply the option to
         */
        public void apply(Host host) {
            host.transport = this.transport; // Set transport
        }
    }

    /**
     * The unique identifier of the host.
     */
    public byte[] peerId;

    /**
     * The keypair used to derive the peerId of the host.
     */
    public KeyPair keypair;

    /**
     * The transport used to make connections with other peers.
     */
    public Transport transport;

    /**
     * Initializes a new host, and applies all of the given options.
     * 
     * @param opts the options to apply to an initialized host
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public Host(Option... opts) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, NoSuchPaddingException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC"); // Initialize keypair generator
        generator.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom()); // Initialize keypair gen

        this.keypair = generator.generateKeyPair(); // Create keypair
        this.peerId = this.keypair.getPublic().getEncoded(); // Hash public key

        // Initialize a tcp transport to make connections from
        this.transport = new Tcp().withUpgrade(new Secio(this.keypair));

        // Iterate through provided options
        for (int i = 0; i < opts.length; i++) {
            opts[i].apply(this); // Apply option
        }
    }
}
package com.dowlandaiello.melon.host;

import com.dowlandaiello.melon.transport.Tcp;
import com.dowlandaiello.melon.transport.Transport;
import com.dowlandaiello.melon.transport.secio.Secio;

import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

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
    public interface Option {
        /**
         * Applies the option to the given host.
         *
         * @param host the host to apply the option to
         */
        void apply(Host host);
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
     * Represents a configuration option used to specify a certain keypair for
     * p2p communications rather than simply generating one.
     */
    public static class IdentityOption {
        /**
         * The keypair to use.
         */
        private final KeyPair keypair;

        /**
         * Initializes a new IdentityOption with the given identity.
         *
         * @param identity the keypair to use for p2p communications encryption
         *                 and identification
         */
        public IdentityOption(KeyPair identity) {
            this.keypair = identity; // Set identity
        }

        /**
         * Applies the option to the given host.
         *
         * @param host the host to apply the option to
         */
        public void apply(Host host) {
            host.peerId = this.keypair.getPublic().getEncoded(); // Derive the new peerId from the given keypair
            host.keypair = this.keypair; // Set the host of the keypair to be the given keypair
        }
    }

    /**
     * The unique identifier of the host.
     */
    private byte[] peerId;

    /**
     * The keypair used to derive the peerId of the host.
     */
    private KeyPair keypair;

    /**
     * The transport used to make connections with other peers.
     */
    public Transport transport;

    /**
     * Initializes a new host, and applies all of the given options.
     * 
     * @param opts the options to apply to an initialized host
     */
    public Host(Option... opts) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC"); // Initialize keypair generator
        generator.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom()); // Initialize keypair gen

        this.keypair = generator.generateKeyPair(); // Create keypair
        this.peerId = this.keypair.getPublic().getEncoded(); // Hash public key

        // Initialize a tcp transport to make connections from
        this.transport = new Tcp().withUpgrade(new Secio(this.keypair));

        // Iterate through provided options
        for (Option opt : opts) {
            opt.apply(this); // Apply option
        }
    }
}
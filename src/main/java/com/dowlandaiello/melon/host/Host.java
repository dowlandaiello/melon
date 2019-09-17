package com.dowlandaiello.melon.host;

import com.dowlandaiello.melon.common.CommonTypes;
import com.dowlandaiello.melon.pubsub.SubscriptionManager;
import com.dowlandaiello.melon.transport.Tcp;
import com.dowlandaiello.melon.transport.Transport;
import com.dowlandaiello.melon.transport.connection.Connection;
import com.dowlandaiello.melon.transport.secio.Secio;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
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
     *
     * @author Dowland Aiello
     * @since 1.0
     */
    public static class TransportOption implements Option{
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
     *
     * @author Dowland Aiello
     * @since 1.0
     */
    public static class IdentityOption implements Option {
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
     * Represents a configuration option used to specify a callback to use to
     * handle incoming connections, rather than the default handler.
     *
     * @author Dowland Aiello
     * @since 1.0
     */
    public static class ConnectionHandlerOption implements Option {
        /**
         * The callback to use.
         */
        private final Transport.Callback callback;

        /**
         * Initializes a new CallbackOption with the given callback.
         *
         * @param callback the callback used to handle incoming connections
         */
        public ConnectionHandlerOption(Transport.Callback callback) {
            this.callback = callback; // Set callback
        }

        /**
         * Applies the option to the given host.
         *
         * @param host the host to apply the option to
         */
        public void apply(Host host) {
            host.connectionHandler = this.callback; // Set connection handler to self callback
        }
    }

    /**
     * Represents the standard pubsub-based connection handler.
     */
    public class StandardConnectionHandler implements Transport.Callback {
        /**
         * The subscription manager.
         */
        public final SubscriptionManager subManager;

        /**
         * Initializes a new connection handler.
         *
         * @param subManager the subscription manager to use to handle connections
         */
        public StandardConnectionHandler(SubscriptionManager subManager) {
            this.subManager = subManager; // Set the sub manager
        }

        /**
         *
         * @param conn the connection passed into the callback
         */
        public void doCallback(Connection conn) throws ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException {
            this.subManager.handleConnection(conn); // Handle the connection
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
     * The general method used to handle incoming connections, regardless of topic.
     */
    public Transport.Callback connectionHandler;

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

        this.connectionHandler = new StandardConnectionHandler(new SubscriptionManager()); // Set the connection handler to the standard connection handler

        // Iterate through provided options
        for (Option opt : opts) {
            opt.apply(this); // Apply option
        }
    }

    /**
     * Listens on a given multiaddress.
     *
     * @param multiaddress the multiaddress to listen on
     */
    public void listen(String multiaddress) throws CommonTypes.MultiAddress.InvalidMultiAddressException, IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException {
        this.transport.listen(multiaddress, this.connectionHandler); // Listen
    }
}
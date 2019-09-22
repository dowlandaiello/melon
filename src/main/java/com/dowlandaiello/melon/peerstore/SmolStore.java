package com.dowlandaiello.melon.peerstore;

import com.dowlandaiello.melon.common.CommonTypes;
import com.dowlandaiello.melon.transport.Transport;
import com.dowlandaiello.melon.transport.connection.Connection;
import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Represents the standard, very smol, but still very useful peerstore.
 *
 * @author Dowland Aiello
 * @since 1.0
 */
public class SmolStore implements Peerstore {
    /**
     * The actual store (very smol, I know).
     */
    private HashMap<String, Connection> connections;

    /**
     * Initializes a new SmolStore instance.
     */
    public SmolStore() {
        this.connections = new HashMap<>(); // Initialize the connections hash map
    }

    /**
     * Commits a generic connection to a remote peer to the peer store. If a
     * connection already exists to the specified peer, it is overwritten in
     * the store.
     *
     * @param multiaddress the multiaddress of the peer
     * @param connection the connection used to communicate with the peer
     */
    public void registerPeer(String multiaddress, Connection connection) {
        this.connections.put(multiaddress, connection); // Add the connection to the connections map
    }

    /**
     * Checks whether or not the specific peer exists in the peerstore.
     *
     * @param multiaddress the multiaddress of the peer
     * @return whether or not the peer exists in the store
     */
    public boolean peerExists(String multiaddress) {
        return this.connections.containsKey(multiaddress); // Return whether or not the store contains the key
    }

    /**
     * Gets a hashmap of registered peers.
     *
     * @return the registered peers
     */
    public HashMap<String, Connection> getRegisteredPeers() {
        return this.connections; // Return the registered peers map
    }

    /**
     * Attempts to find an existing connection to a remote peer. Throws a
     * PeerNotFound exception if the peer does not exist in the peerstore.
     *
     * @param multiaddress the multiaddress of the peer to search for
     * @return the existing connection
     */
    public Connection getExistingConnection(String multiaddress) throws StoreException.PeerNotFoundException {
        // Check has a connection with the given peer
        if (this.peerExists(multiaddress)) {
            return this.connections.get(multiaddress); // Return the corresponding connection
        }

        throw new StoreException.PeerNotFoundException(multiaddress); // Throw an exception
    }

    /**
     * Attempts to retrieve a copy of the peerstore from a remote peer.
     *
     * @param bootstrapPeerAddress the address of the peer to bootstrap from
     * @param dialer the transport to connect to the bootstrap peer with
     */
    public void bootstrap(String bootstrapPeerAddress, Transport dialer, Key dialingKey) throws StoreException, IOException, NoSuchAlgorithmException, InvalidKeyException, Transport.UnsupportedTransportException, NoSuchPaddingException, CommonTypes.MultiAddress.InvalidMultiAddressException, DecoderException, ClassNotFoundException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        Connection bootstrapConnection; // Declare a connection to use with the bootstrap peer

        // Check a connection to the given bootstrap peer already exists
        if (this.peerExists(bootstrapPeerAddress)) {
            bootstrapConnection = this.getExistingConnection(bootstrapPeerAddress); // Get a connection to the bootstrap peer
        } else {
            bootstrapConnection = dialer.dial(bootstrapPeerAddress, dialingKey); // Dials the specified peer
        }

        CommonTypes.Message message = new CommonTypes.Message(new BootstrapMessage(null), CommonTypes.Message.Type.BOOTSTRAP); // Initialize a new message

        bootstrapConnection.writeObject(message); // Write the bootstrap message to the peer

        Object resp = bootstrapConnection.readObject(); // Read a response from the peer

        CommonTypes.Message messageResp = resp instanceof CommonTypes.Message ? (CommonTypes.Message) resp : null; // Perform a safe cast

        if (messageResp == null || !(messageResp.contents instanceof BootstrapMessage)) {
            return; // Lol
        }

        BootstrapMessage bootstrapMessage = (BootstrapMessage) messageResp.contents; // Perform a safe cast

        ExecutorService exec = Executors.newFixedThreadPool(bootstrapMessage.peerAddresses.size()); // Initialize an execution service for the amount of peers responded

        Semaphore mutex = new Semaphore(1); // Initialize a semaphore

        // Iterate through the bootstrapped peer addresses, connect to each
        for (String peerAddress : bootstrapMessage.peerAddresses) {
            exec.execute(() -> {
                try {
                    Connection conn = dialer.dial(bootstrapPeerAddress, dialingKey); // Dial the specified peer

                    try {
                        mutex.acquire(); // Acquire a permit

                        try {
                            this.connections.put(peerAddress, conn); // Put the connection in the table
                        } finally {
                            mutex.release(); // Release a lock
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // Log an exception
                    }
                } catch (IOException | Transport.UnsupportedTransportException | CommonTypes.MultiAddress.InvalidMultiAddressException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | DecoderException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }); // Connect to the specified peer
        }

        exec.shutdown(); // Queue a shutdown of the pool

        try {
            if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
                exec.shutdownNow(); // Forcefully shutdown the thread pool
            }
        } catch (InterruptedException ex) {
            exec.shutdownNow(); // Forcefully shutdown the thread pool

            Thread.currentThread().interrupt(); // Idk I just copied this from so don't get mad at me
        }
    }
}

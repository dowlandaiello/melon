package com.dowlandaiello.melon.peerstore;

import com.dowlandaiello.melon.transport.connection.Connection;

import java.util.HashMap;

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
}

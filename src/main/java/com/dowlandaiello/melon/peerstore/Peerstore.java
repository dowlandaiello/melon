package com.dowlandaiello.melon.peerstore;

import com.dowlandaiello.melon.transport.connection.Connection;

/**
 * Represents a generic, non-persistent virtual storage device.
 *
 * @author Dowland Aiello
 * @since 1.0
 */
public interface Peerstore {
    /**
     * Commits a generic connection to a remote peer to the peer store.
     *
     * @param multiaddress the multiaddress of the peer
     * @param connection the connection used to communicate with the peer
     */
    void registerPeer(String multiaddress, Connection connection);

    /**
     * Checks whether or not the specific peer exists in the peerstore.
     *
     * @param multiaddress the multiaddress of the peer
     * @return whether or not the peer exists in the store
     */
    boolean peerExists(String multiaddress);

    /**
     * Attempts to find an existing connection to a remote peer. Throws a
     * PeerNotFound exception if the peer does not exist in the peerstore.
     *
     * @param multiaddress the multiaddress of the peer to search for
     * @return the corresponding connection
     */
    Connection getExistingConnection(String multiaddress) throws StoreException.PeerNotFoundException;
}
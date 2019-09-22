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
     * Gets a hashmap of registered peers.
     *
     * @return the registered peers
     */
    HashMap<String, Connection> getRegisteredPeers();

    /**
     * Attempts to find an existing connection to a remote peer. Throws a
     * PeerNotFound exception if the peer does not exist in the peerstore.
     *
     * @param multiaddress the multiaddress of the peer to search for
     * @return the corresponding connection
     */
    Connection getExistingConnection(String multiaddress) throws StoreException.PeerNotFoundException;

    /**
     * Attempts to retrieve a copy of the peerstore from a remote peer.
     *
     * @param bootstrapPeerAddress the address of the peer to bootstrap from
     */
    void bootstrap(String bootstrapPeerAddress, Transport dialer, Key dialingKey) throws StoreException, IOException, NoSuchAlgorithmException, InvalidKeyException, Transport.UnsupportedTransportException, NoSuchPaddingException, CommonTypes.MultiAddress.InvalidMultiAddressException, DecoderException, ClassNotFoundException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException;
}

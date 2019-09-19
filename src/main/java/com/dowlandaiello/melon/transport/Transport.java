package com.dowlandaiello.melon.transport;

import com.dowlandaiello.melon.common.CommonTypes.MultiAddress.InvalidMultiAddressException;
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

/**
 * Represents a generic, upgradable transport.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public interface Transport {
    /**
     * Represents an exception regarding an unsupported transport.
     */
    class UnsupportedTransportException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Initialize a new UnsupportedTransportException.
         * 
         * @param message the exception message
         */
        UnsupportedTransportException(String message) {
            super(message); // Make the exception
        }
    }

    /**
     * A callback executed after an incoming connection is successfully established.
     *
     * @since 1.0
     */
    interface Callback {
        /**
         * Executes the target callback.
         *
         * @param conn the connection passed into the callback
         */
        void doCallback(Connection conn) throws ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException;
    }

    /**
     * Applies a particular upgrade to a transport.
     * 
     * @param upgrade the upgrade to apply to the transport
     * @return the upgraded transport
     */
    Transport withUpgrade(Upgrade upgrade);

    /**
     * Constructs a new transport that falls back to the given fallback transport,
     * should a particular transport protocol denoted by the destination address not
     * be supported (i.e. /ws in dest addr).
     * 
     * @param fallback the transport to fall back to
     * @return the updated transport
     */
    Transport withFallback(Transport fallback);

    /**
     * Listens on the given multiaddress, and executes the given callback with
     * each successfully established connection.
     *
     * @param multiaddress the multiaddress to listen on
     * @param callback the callback to run after successfully establishing a
     *                 connection
     */
    void listen(String multiaddress, Callback callback) throws InvalidMultiAddressException, IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException;

    /**
     * Dials a given address, and returns the socket after connecting.
     * 
     * @param address the address of the peer to dial
     * @return the connected socket
     */
    Connection dial(String address, Key sendingPublicKey) throws IOException, InvalidMultiAddressException,
            UnsupportedTransportException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, DecoderException, InvalidKeySpecException;
}
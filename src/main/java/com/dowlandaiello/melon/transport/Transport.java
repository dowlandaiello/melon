package com.dowlandaiello.melon.transport;

import com.dowlandaiello.melon.common.CommonTypes;
import com.dowlandaiello.melon.transport.connection.Connection;
import org.apache.commons.codec.DecoderException;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
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
     * Dials a given address, and returns the socket after connecting.
     * 
     * @param address the address of the peer to dial
     * @return the connected socket
     */
    Connection dial(String address) throws IOException, CommonTypes.MultiAddress.InvalidMultiAddressException,
            UnsupportedTransportException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, DecoderException, InvalidKeySpecException;
}
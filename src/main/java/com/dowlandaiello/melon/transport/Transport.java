/**
 * Implements a set of upgradable, generic network transports.
 */
package com.dowlandaiello.melon.transport;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import com.dowlandaiello.melon.common.CommonTypes;
import com.dowlandaiello.melon.transport.connection.Connection;

import org.apache.commons.codec.DecoderException;

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
    public class UnsupportedTransportException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Initialize a new UnsupportedTransportException.
         * 
         * @param message the exception message
         */
        public UnsupportedTransportException(String message) {
            super(message); // Make the exception
        }
    }

    /**
     * Applies a particular upgrade to a transport.
     * 
     * @param upgrade the upgrade to apply to the transport
     * @return the upgraded transport
     */
    public Transport withUpgrade(Upgrade upgrade);

    /**
     * Constructs a new transport that falls back to the given fallback transport,
     * should a particular transport protocol denoted by the destination address not
     * be supported (i.e. /ws in dest addr).
     * 
     * @param fallback the transport to fall back to
     * @return the updated transport
     */
    public Transport withFallback(Transport fallback);

    /**
     * Dial a given address, and return the socket after connecting.
     * 
     * @param address the address of the peer to dial
     * @return the connected socket
     * @throws DecoderException
     * @throws InvalidKeySpecException
     */
    public Connection dial(String address) throws IOException, CommonTypes.MultiAddress.InvalidMultiAddressException,
            UnsupportedTransportException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, DecoderException, InvalidKeySpecException;
}
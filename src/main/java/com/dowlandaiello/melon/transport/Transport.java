/**
 * Implements a set of upgradable, generic network transports.
 */
package com.dowlandaiello.melon.transport;

import java.io.IOException;
import java.net.Socket;

import com.dowlandaiello.melon.common.CommonTypes;

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
     * A generic upgrade to a particular transport.
     */
    public interface Upgrade {
        /**
         * Applies a particular upgrade to a transport.
         * 
         * @param transport the transport to apply the upgrade to
         */
        public void apply(Transport transport);
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
     */
    public Socket dial(String address)
            throws IOException, CommonTypes.MultiAddress.InvalidMultiAddressException, UnsupportedTransportException;
}
package com.dowlandaiello.melon.peerstore;

/**
 * Defines and implements a set of peerstore-related exceptions.
 *
 * @author Dowland Aiello
 * @since 1.0
 */
public class StoreException extends Exception {
    /**
     * Initializes a new StoreException with the given message.
     *
     * @param message the message to initialize the StoreException with
     */
    public StoreException(String message) {
        super(message); // Call the exception class initializer
    }

    /**
     * Represents an exception regarding a null multiaddress connection.
     */
    public static class PeerNotFoundException extends StoreException {
        /**
         * Initializes a new PeerNotFoundException with the given multiaddress.
         *
         * @param multiaddress the multiaddress of the peer targeted by the exception
         */
        public PeerNotFoundException(String multiaddress) {
            super("No connection exists for a peer with the multiaddress "+multiaddress); // Initialize exception
        }
    }
}

/**
 * Implements a set of commonly used types and helper methods.
 */
package com.dowlandaiello.melon.common;

import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * Represents an implementation of some common types and helper methods.
 */
public class CommonTypes {
    /**
     * Represents an IPFS-style MultiAddress.
     */
    public static class MultiAddress {
        /**
         * Represents an exception regarding an invalid MultiAddress.
         */
        public static class InvalidMultiAddressException extends Exception {
            private static final long serialVersionUID = 1L;

            /**
             * Initialize a new InvalidMultiAddressException.
             * 
             * @param message the exception message
             */
            public InvalidMultiAddressException(String message) {
                super(message); // Make the exception
            }
        }

        /**
         * Determine if the contents of the MultiAddress are indeed valid.
         * 
         * @return whether or not the MultiAddress is valid
         */
        public static boolean isValid(String address) {
            String[] segments = address.split("/"); // Split address

            InetAddressValidator validator = InetAddressValidator.getInstance(); // Initialize an ip address validator

            // Attempt to parse the port number
            try {
                int port = Integer.parseInt(segments[3]); // Parse port

                // Check port out of range
                if (port > 65535) {
                    return false; // Invalid port
                }
            } catch (NumberFormatException e) {
                return false; // Invalid port
            }

            return segments.length == 5 && segments[0].matches("ip(4|6)") && validator.isValid(segments[1])
                    && segments[2].matches("[a-z]{2,3}|quic"); // Return is valid
        }

        /**
         * Get the transport of a particular MultiAddress.
         * 
         * @param address the address to parse
         * @return the parsed transport
         * @throws InvalidMultiAddressException
         */
        public static String parseTransport(String address) throws InvalidMultiAddressException {
            String[] segments = address.split("/"); // Split address

            // Check for invalid MultiAddress
            if (segments.length != 5) {
                throw new InvalidMultiAddressException("attempted to parse malformed address"); // Invalid
            }

            return segments[2]; // Return transport
        }

        /**
         * Get the ip address of a particular MultiAddress.
         * 
         * @param address the address to parse
         * @return the parsed ip
         * @throws InvalidMultiAddressException
         */
        public static String parseInetAddress(String address) throws InvalidMultiAddressException {
            String[] segments = address.split("/"); // Split address

            // Check for invalid MultiAddress
            if (segments.length != 5) {
                throw new InvalidMultiAddressException("attempted to parse malformed address"); // Invalid
            }

            return segments[1]; // Return IP
        }

        /**
         * Get the port of a particular MultiAddress.
         * 
         * @param address the address to parse
         * @return the parsed port
         * @throws InvalidMultiAddressException
         */
        public static int parsePort(String address) throws InvalidMultiAddressException {
            String[] segments = address.split("/"); // Split address

            // Check for invalid MultiAddress
            if (segments.length != 5) {
                throw new InvalidMultiAddressException("attempted to parse malformed address"); // Invalid
            }

            return Integer.parseInt(segments[3]); // Return the parsed port
        }
    }
}
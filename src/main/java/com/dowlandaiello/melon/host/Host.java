/**
 * Implements a melon host. The host is the central hub for local melon
 * operations and communications.
 */
package com.dowlandaiello.melon.host;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.dowlandaiello.melon.crypto.Hash;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

/**
 * Represents a local melon peer. Serves as a wrapper for core melon
 * functionality.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public class Host {
    /**
     * Represents a configuration option to be applied to a melon host.
     *
     * @author Dowland Aiello
     * @since 1.0
     */
    public static interface Option {
        /**
         * Applies the option to the given host.
         *
         * @param host the host to apply the option to
         */
        public void apply(Host host);
    }

    /**
     * The unique identifier of the host.
     */
    Hash peerId;

    /**
     * The keypair used to derive the peerId of the host.
     */
    ECKeyPair keypair;

    /**
     * Initializes a new host, and applies all of the given options.
     * 
     * @param opts the options to apply to an initialized host
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    public Host(Option... opts)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        this.keypair = Keys.createEcKeyPair(); // Create keypair
        this.peerId = Hash.sha3(keypair.getPublicKey().toByteArray()); // Hash public key

        // Iterate through provided options
        for (int i = 0; i < opts.length; i++) {
            opts[i].apply(this); // Apply option
        }
    }
}
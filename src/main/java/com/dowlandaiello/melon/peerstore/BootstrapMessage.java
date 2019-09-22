package com.dowlandaiello.melon.peerstore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a message regarding the bootstrapping of a dht.
 *
 * @author Dowland Aiello
 * @since 1.0
 */
public class BootstrapMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The addresses included in the bootstrap message.
     */
    public ArrayList<String> peerAddresses;

    /**
     * Initializes a new BootstrapMessage with the given peer address list.
     *
     * @param peerAddresses the addresses of the peers
     */
    public BootstrapMessage(ArrayList<String> peerAddresses) {
        this.peerAddresses = peerAddresses; // Set the addresses of the peers in the instance
    }
}

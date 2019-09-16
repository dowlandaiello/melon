package com.dowlandaiello.melon.transport.connection;

import com.dowlandaiello.melon.transport.Upgrade;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a response from an initial connection to a peer, in which
 * identifying information is first exchanged.
 */
public class Negotiation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The cipher used to decrypt communications.
     */
    public Cipher cipher;

    /**
     * The upgrades supported by the connected peer.
     */
    public ArrayList<Upgrade> availableUpgrades;

    /**
     * Initializes a new negotiation instance with the given public key and
     * upgrade set.
     *
     * @param cipher the cipher to use
     * @param availableUpgrades the upgrades to use
     */
    public Negotiation(Cipher cipher, ArrayList<Upgrade> availableUpgrades) {
        this.cipher = cipher; // Set cipher
        this.availableUpgrades = availableUpgrades; // Set upgrades
    }
}

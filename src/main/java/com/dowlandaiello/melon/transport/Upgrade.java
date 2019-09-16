package com.dowlandaiello.melon.transport;

import java.io.Serializable;

/**
 * A generic upgrade to a particular transport.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public interface Upgrade extends Serializable {
    /**
     * Represents the type of feature added by an upgrade.
     */
    enum Type {
        SECIO
    }

    /**
     * Gets the upgrade type of an upgrade.
     * 
     * @return the type of the upgrade
     */
    Type getType();

    /**
     * Gets the respective config of an upgrade for a particular transport
     * direction.
     * 
     * @param address the peer address to get a secio config for (i.e. "127.0.0.1:3003"/"any.remote.address"")
     * @return the upgrade's configuration
     */
    Object getConfig(String address);

    /**
     * Converts the upgrade to a string.
     * 
     * @return the string representation of the upgrade
     */
    String toString();
}
/**
 * Implements a set of upgradable, generic network transports.
 */
package com.dowlandaiello.melon.transport;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A generic upgrade to a particular transport.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public interface Upgrade extends Serializable {
    /**
     * Represents a set of upgrades.
     * 
     * @author Dowland Aiello
     * @since 1.0
     */
    public static class UpgradeSet implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * The upgrades.
         */
        public ArrayList<Upgrade> upgrades;

        /**
         * Initializes a new upgrade set.
         * 
         * @param upgrades the upgrades to contain in the upgrade set
         */
        public UpgradeSet(ArrayList<Upgrade> upgrades) {
            this.upgrades = upgrades; // Set upgrades
        }
    }

    /**
     * Represents the type of feature added by an upgrade.
     */
    public enum Type {
        SECIO
    }

    /**
     * Gets the upgrade type of an upgrade.
     * 
     * @return the type of the upgrade
     */
    public Type getType();

    /**
     * Gets the respective config of an upgrade for a particular transport
     * direction.
     * 
     * @param transportDirection the "direction" of communication to get a config
     *                           for (accepted values: "any", "in", "out")
     * @return the upgrade's configuration
     */
    public Object getConfig(String transportDirection);

    /**
     * Converts the upgrade to a string.
     * 
     * @return the string representation of the upgrade
     */
    public String toString();
}
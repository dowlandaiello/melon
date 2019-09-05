/**
 * Implements a set of upgradable, generic network transports.
 */
package com.dowlandaiello.melon.transport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import com.dowlandaiello.melon.common.CommonTypes;
import com.dowlandaiello.melon.common.CommonTypes.Message;
import com.dowlandaiello.melon.transport.Upgrade.UpgradeSet;
import com.dowlandaiello.melon.transport.connection.Connection;
import com.dowlandaiello.melon.transport.connection.TcpSocket;

import org.apache.commons.codec.DecoderException;

/**
 * Represents an upgradable tcp transport.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public class Tcp implements Transport {
    /**
     * The transports to fall back to.
     */
    private Transport fallbackTransport;

    /**
     * The upgrades to use.
     */
    private Map<Upgrade.Type, Upgrade> upgrades;

    public Tcp() {
        this.fallbackTransport = null; // No fallback transports
        this.upgrades = new HashMap<Upgrade.Type, Upgrade>(); // Initialize upgrades map
    }

    /**
     * Applies a particular upgrade to a transport.
     * 
     * @param upgrade the upgrade to apply to the transport
     * @return the upgraded transport
     */
    public Transport withUpgrade(Upgrade upgrade) {
        this.upgrades.put(upgrade.getType(), upgrade); // Add upgrade

        return this; // Allow chaining of withUpgrade statements
    }

    /**
     * Constructs a new transport that falls back to the given fallback transport,
     * should a particular transport protocol denoted by the destination address not
     * be supported (i.e. /ws in dest addr). Does not remove existing fallback
     * transport rules.
     * 
     * @param fallback the transport to fall back to
     * @return the updated transport
     */
    public Transport withFallback(Transport fallback) {
        // Check we already have a fallback transport
        if (this.fallbackTransport != null) {
            this.fallbackTransport = this.fallbackTransport.withFallback(fallback); // Use fallback

            return this; // Allow chaining of withFallback statements
        }

        this.fallbackTransport = fallback; // Set fallback transport

        return this; // Allow chaining of withFallback statements
    }

    /**
     * Dial a given address, and return the socket after connecting.
     * 
     * @param address the address of the peer to dial
     * @return the connected socket
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws DecoderException
     * @throws InvalidKeySpecException
     */
    public Connection dial(String address) throws IOException, CommonTypes.MultiAddress.InvalidMultiAddressException,
            UnsupportedTransportException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, DecoderException, InvalidKeySpecException {
        // Check multiAddr invalid
        if (!CommonTypes.MultiAddress.isValid(address)) {
            // Throw exception
            throw new CommonTypes.MultiAddress.InvalidMultiAddressException(
                    "attempted to dial improperly formatted address");
        }

        // Parse the desired connection transport, address, pub key, and port so we can
        // check for
        // compatibility
        String transport = CommonTypes.MultiAddress.parseTransport(address);
        String inetAddress = CommonTypes.MultiAddress.parseInetAddress(address);
        int port = CommonTypes.MultiAddress.parsePort(address);
        PublicKey peerPublicKey = CommonTypes.MultiAddress.parsePublicKey(address);

        // Check is not using tcp
        if (transport != "tcp") {
            // Check no fallback
            if (this.fallbackTransport == null) {
                // Throw exception indicating use of unsupported transport
                throw new UnsupportedTransportException(
                        String.format("attempted to dial a peer using an unsupported transport (%s)", transport)); // Throw
            }

            return this.fallbackTransport.dial(address); // Try dialing with fallback
        }

        // Initialize upgrade set for negotiation
        UpgradeSet upgrades = new UpgradeSet(
                new ArrayList<Upgrade>(Arrays.asList((Upgrade[]) this.upgrades.values().toArray())));

        // Initialize a negotiation message
        Message availableUpgradesMessage = new Message(upgrades, Message.Type.NEGOTIATION);

        Socket baseSocket = new Socket(inetAddress, port); // Connect without upgrading
        ObjectOutputStream outStream = new ObjectOutputStream(baseSocket.getOutputStream()); // Get output stream
        ObjectInputStream inStream = new ObjectInputStream(baseSocket.getInputStream()); // Get input stream

        outStream.writeObject(availableUpgradesMessage); // Write to connection

        Message response = (Message) inStream.readObject(); // Read an incoming message

        // Check is negotiation
        if (response.type == Message.Type.NEGOTIATION) {
            // Cast message contents to upgrade set (the connected peer's supported
            // upgrades)
            ArrayList<Upgrade> peerSupportedUpgrades = ((UpgradeSet) response.contents).upgrades;

            // Initialize usable upgrades map
            HashMap<Upgrade.Type, Upgrade> usableUpgrades = new HashMap<Upgrade.Type, Upgrade>();

            // Iterate through upgrades
            for (int i = 0; i < peerSupportedUpgrades.size(); i++) {
                // Iterate through locally supported upgrades
                for (int x = 0; x < upgrades.upgrades.size(); x++) {
                    // Check both supported
                    if (upgrades.upgrades.get(x).getType() == peerSupportedUpgrades.get(i).getType()) {
                        // Add upgrade to usable upgrades list
                        usableUpgrades.put(upgrades.upgrades.get(x).getType(), upgrades.upgrades.get(x));

                        break; // Break
                    }
                }
            }

            baseSocket.close(); // Close the base socket

            Socket finalSocket = new Socket(inetAddress, port); // Connect

            return new TcpSocket(finalSocket, usableUpgrades, peerPublicKey); // Return final socket
        } else {
            return new TcpSocket(baseSocket, null, null); // Nothing to negotiate
        }
    }
}
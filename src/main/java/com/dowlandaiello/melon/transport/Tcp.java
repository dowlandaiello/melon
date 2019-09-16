package com.dowlandaiello.melon.transport;

import com.dowlandaiello.melon.common.CommonTypes;
import com.dowlandaiello.melon.common.CommonTypes.Message;
import com.dowlandaiello.melon.common.CommonTypes.MultiAddress.InvalidMultiAddressException;
import com.dowlandaiello.melon.transport.connection.Connection;
import com.dowlandaiello.melon.transport.connection.Negotiation;
import com.dowlandaiello.melon.transport.connection.TcpSocket;
import org.apache.commons.codec.DecoderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    private HashMap<Upgrade.Type, Upgrade> upgrades;

    /**
     * Initializes a new TCP transport.
     */
    public Tcp() {
        this.fallbackTransport = null; // No fallback transports
        this.upgrades = new HashMap<>(); // Initialize upgrades map
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
     * Listens on the given multiaddress, and executes the given callback with
     * each successfully established connection.
     *
     * @param multiaddress the multiaddress to listen on
     * @param callback the callback to run after successfully establishing a
     *                 connection
     */
    public void listen(String multiaddress, CallbackInterface callback) throws InvalidMultiAddressException, IOException, ClassNotFoundException {
        int port = CommonTypes.MultiAddress.parsePort(multiaddress); // Get the port we'll be listening on

        ServerSocket serverSocket = new ServerSocket(port); // Initialize a server socket for the given port

        // Do while the server socket is open
        while(!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept(); // Accept a socket

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // Get an object input stream for the socket

            Message resp = (Message) in.readObject(); // Read a response from the peer

            Cipher inCipher = null; // We'll set this once we find out the remote peer's public key

            // Check is negotiation
            if (resp.type == Message.Type.NEGOTIATION) {
                Negotiation peerNegotiation = resp.contents != null ? (Negotiation) resp.contents : null; // Get the peer's negotiation
                Negotiation selfNegotiation; // We'll construct a negotiation to send to the remote peer once we've determined which protocols we have in common

                // Check no common upgrades
                if (peerNegotiation == null || peerNegotiation.availableUpgrades.size() == 0) {
                    callback.doCallback(new TcpSocket(socket)); // Just use a bare socket

                    continue; // Continue
                }

                inCipher = peerNegotiation.cipher; // Set in cipher

                ArrayList<Upgrade> supportedUpgrades = new ArrayList<>(); // Initialize supported upgrades array list

                // Iterate through available upgrades
                for (Upgrade upgrade : peerNegotiation.availableUpgrades) {
                    // Check has upgrade
                    if (this.upgrades.containsKey(upgrade.getType())) {
                        supportedUpgrades.add(this.upgrades.get(upgrade.getType())); // Add upgrade to supported upgrades list
                    }
                }

                // Check has secio upgrade
                if (this.upgrades.containsKey(Upgrade.Type.SECIO)) {
                    selfNegotiation = new Negotiation((Cipher) this.upgrades.get(Upgrade.Type.SECIO).getConfig("127.0.0.1"), supportedUpgrades); // Initialize negotiation
                } else {
                    selfNegotiation = new Negotiation(null, supportedUpgrades); // Initialize negotiation
                }

                (new ObjectOutputStream(socket.getOutputStream())).writeObject(selfNegotiation); // Write negotiation
            }

            // Check has secio upgrade
            if (this.upgrades.containsKey(Upgrade.Type.SECIO)) {
                callback.doCallback(new TcpSocket(socket, this.upgrades, inCipher)); // Do callback
            }
        }
    }

    /**
     * Dials a given address, and returns the socket after connecting.
     * 
     * @param address the address of the peer to dial
     * @return the connected socket
     */
    public Connection dial(String address) throws IOException, InvalidMultiAddressException,
            UnsupportedTransportException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, DecoderException, InvalidKeySpecException {
        // Check multiAddr invalid
        if (!CommonTypes.MultiAddress.isValid(address)) {
            // Throw exception
            throw new InvalidMultiAddressException(
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
        if (!transport.equals("tcp")) {
            // Check no fallback
            if (this.fallbackTransport == null) {
                // Throw exception indicating use of unsupported transport
                throw new UnsupportedTransportException(
                        String.format("attempted to dial a peer using an unsupported transport (%s)", transport)); // Throw
            }

            return this.fallbackTransport.dial(address); // Try dialing with fallback
        }

        ArrayList<Upgrade> upgrades = new ArrayList<>(Arrays.asList((Upgrade[]) this.upgrades.values().toArray())); // Convert upgrade map to ArrayList

        // Initialize a negotiation
        Negotiation negotiation = this.upgrades.containsKey(Upgrade.Type.SECIO) ? new Negotiation(((Cipher) this.upgrades.get(Upgrade.Type.SECIO).getConfig("127.0.0.1")), upgrades) : new Negotiation(null, upgrades);

        // Initialize a negotiation message
        Message availableUpgradesMessage = new Message(negotiation, Message.Type.NEGOTIATION);

        Socket baseSocket = new Socket(inetAddress, port); // Connect without upgrading
        ObjectOutputStream outStream = new ObjectOutputStream(baseSocket.getOutputStream()); // Get output stream
        ObjectInputStream inStream = new ObjectInputStream(baseSocket.getInputStream()); // Get input stream

        outStream.writeObject(availableUpgradesMessage); // Write to connection

        Message response = (Message) inStream.readObject(); // Read an incoming message

        // Check is negotiation
        if (response.type == Message.Type.NEGOTIATION) {
            // Cast message contents to upgrade set (the connected peer's supported
            // upgrades)
            ArrayList<Upgrade> peerSupportedUpgrades = response.contents != null ? ((Negotiation) response.contents).availableUpgrades : null;

            // Check no upgrades
            if (peerSupportedUpgrades == null || peerSupportedUpgrades.size() == 0) {
                return new TcpSocket(baseSocket); // Just use a base socket
            }

            // Initialize usable upgrades map
            HashMap<Upgrade.Type, Upgrade> usableUpgrades = new HashMap<>();

            // Iterate through upgrades
            for (Upgrade peerSupportedUpgrade : peerSupportedUpgrades) {
                // Iterate through locally supported upgrades
                for (Upgrade upgrade : upgrades) {
                    // Check both supported
                    if (upgrade.getType() == peerSupportedUpgrade.getType()) {
                        // Add upgrade to usable upgrades list
                        usableUpgrades.put(upgrade.getType(), upgrade);

                        break; // Break
                    }
                }
            }

            return new TcpSocket(baseSocket, usableUpgrades, peerPublicKey); // Return final socket
        } else {
            return new TcpSocket(baseSocket); // Nothing to negotiate
        }
    }
}
/**
 * Implements a set of upgradable, generic network transports.
 */
package com.dowlandaiello.melon.transport;

import java.io.IOException;
import java.net.Socket;

import com.dowlandaiello.melon.common.CommonTypes;

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

    public Tcp() {
        this.fallbackTransport = null; // No fallback transports
    }

    /**
     * Applies a particular upgrade to a transport.
     * 
     * @param upgrade the upgrade to apply to the transport
     * @return the upgraded transport
     */
    public Transport withUpgrade(Upgrade upgrade) {
        upgrade.apply(this); // Apply upgrade

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
     */
    public Socket dial(String address)
            throws IOException, CommonTypes.MultiAddress.InvalidMultiAddressException, UnsupportedTransportException {
        // Check multiAddr invalid
        if (!CommonTypes.MultiAddress.isValid(address)) {
            // Throw exception
            throw new CommonTypes.MultiAddress.InvalidMultiAddressException(
                    "attempted to dial improperly formatted address");
        }

        // Parse the desired connection transport, address, and port so we can check for
        // compatibility
        String transport = CommonTypes.MultiAddress.parseTransport(address);
        String inetAddress = CommonTypes.MultiAddress.parseInetAddress(address);
        int port = CommonTypes.MultiAddress.parsePort(address);

        // Check is not using tcp
        if (transport != "tcp") {
            // Check no fallback
            if (this.fallbackTransport == null) {
                // Throw exception indicating use of unsupported transport
                throw new UnsupportedTransportException(
                        String.format("attempted to dial a peer using an unsupported transport (%s)", transport)); // Throw
            }
        }

        return new Socket(inetAddress, port); // Connect
    }
}
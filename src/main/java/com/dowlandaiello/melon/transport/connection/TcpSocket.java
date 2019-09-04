/**
 * Implements a set of upgradable, generic connection types.
 */
package com.dowlandaiello.melon.transport.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

import com.dowlandaiello.melon.transport.Upgrade;

/**
 * Represents an upgradable TCP connection.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public class TcpSocket implements Connection {
    /**
     * The socket attached to the connection.
     */
    private final Socket socket;

    /**
     * The output stream to write data to.
     */
    private final DataOutputStream dataOutStream;

    /**
     * The input stream to read data from.
     */
    private final DataInputStream dataInStream;

    /**
     * The output stream to write objects to.
     */
    private final ObjectOutputStream objOutStream;

    /**
     * The input stream to read objects from.
     */
    private final ObjectInputStream objInStream;

    /**
     * The cipher-supporting output stream to write data to.
     */
    private final CipherOutputStream cipherOutStream;

    /**
     * The cipher-supporting input stream to read data from.
     */
    private final CipherInputStream cipherInStream;

    /**
     * The cipher associated with the connection's cipher in stream.
     */
    private final Cipher cipherIn;

    /**
     * The cipher associated with the connection's cipher out stream.
     */
    private final Cipher cipherOut;

    /**
     * Initializes a new Tcp connection with a given socket and upgrade set.
     */
    public TcpSocket(Socket socket, HashMap<Upgrade.Type, Upgrade> upgrades) throws IOException {
        this.socket = socket; // Set socket
        this.dataOutStream = new DataOutputStream(socket.getOutputStream()); // Set data output stream
        this.dataInStream = new DataInputStream(socket.getInputStream()); // Set data input stream
        this.objOutStream = new ObjectOutputStream(socket.getOutputStream()); // Set object output stream
        this.objInStream = new ObjectInputStream(socket.getInputStream()); // Set object input stream

        Upgrade secio = upgrades.get(Upgrade.Type.SECIO); // Get secio upgrade

        // Check no secio support
        if (secio != null) {
            Cipher cipherOut = (Cipher) secio.getConfig("out"); // Get outbound cipher
            Cipher cipherIn = (Cipher) secio.getConfig("in"); // Get inbound cipher

            // Set cipher streams
            this.cipherOutStream = new CipherOutputStream(socket.getOutputStream(), cipherOut);
            this.cipherInStream = new CipherInputStream(socket.getInputStream(), cipherIn);

            this.cipherOut = cipherOut; // Set cipher out
            this.cipherIn = cipherIn; // Set cipher in

            return; // Done!
        }

        // Set cipher streams to null since SECIO is not supported
        this.cipherOutStream = null;
        this.cipherInStream = null;

        // Set ciphers to null since SECIO is not supported
        this.cipherOut = null;
        this.cipherIn = null;
    }

    /**
     * Writes a single byte to a connection.
     * 
     * @param b the byte to write to the connection
     * @throws IOException
     */
    public void write(int b) throws IOException {
        // Check has secio upgrade
        if (this.cipherOutStream != null) {
            this.cipherOutStream.write(b); // Write to connection

            return; // Return
        }

        this.dataOutStream.write(b); // Write to connection
    }

    /**
     * Reads a single byte from a connection.
     * 
     * @return the read byte
     * @throws IOException
     */
    public int read() throws IOException {
        // Check has secio upgrade
        if (this.cipherInStream != null) {
            return this.cipherInStream.read(); // Return read byte
        }

        return this.dataInStream.read(); // Return read byte
    }

    /**
     * Writes a byte array to a connection.
     * 
     * @throws IOException
     */
    public void write(byte[] b) throws IOException {
        // Check has secio upgrade
        if (this.cipherOutStream != null) {
            this.cipherOutStream.write(b); // Write to connection w/secio

            return; // Return
        }

        this.dataOutStream.write(b); // Write to connection
    }

    /**
     * Reads some number of bytes from the connection into the buffer b.
     * 
     * @param b the buffer to read into
     * @return the number of read bytes
     */
    public int read(byte[] b) throws IOException {
        // Check has secio upgrade
        if (this.cipherInStream != null) {
            return this.cipherInStream.read(b); // Return number of read bytes
        }

        return this.dataInStream.read(b); // Return number of read bytes
    }

    /**
     * Writes an object to the connection.
     * 
     * @param obj the object to write
     * @throws IOException
     * @throws IllegalBlockSizeException
     */
    public void writeObject(Serializable obj) throws IOException, IllegalBlockSizeException {
        // Check has secio upgrade
        if (this.cipherOutStream != null) {
            SealedObject sealed = new SealedObject(obj, this.cipherOut); // Seal object

            this.objOutStream.writeObject(sealed); // Write sealed object
        }

        this.objOutStream.writeObject(obj); // Write object
    }

    /**
     * Reads an object from the connection.
     * 
     * @return the read object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public Object readObject()
            throws IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        // Check has secio upgrade
        if (this.cipherInStream != null) {
            SealedObject sealed = (SealedObject) this.objInStream.readObject(); // Read sealed object

            return sealed.getObject(this.cipherIn); // Return decrypted object
        }

        return this.objInStream.readObject(); // Return read object
    }

    /**
     * Flushes the connection.
     * 
     * @throws IOException
     */
    public void flush() throws IOException {
        this.dataOutStream.flush(); // Flush data output stream
        this.objOutStream.flush(); // Flush object output stream
        this.cipherOutStream.flush(); // Flush cipher output stream
    }

    /**
     * Closes the connection.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        this.dataOutStream.close(); // Close data outs stream
        this.dataInStream.close(); // Close data in stream
        this.objOutStream.close(); // Close obj out stream
        this.objInStream.close(); // Close obj in stream
        this.cipherOutStream.close(); // Close cipher in stream
        this.cipherInStream.close(); // Close cipher in stream

        this.socket.close(); // Close socket
    }
}
package com.dowlandaiello.melon.transport.connection;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Represents a generic transport connection.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public interface Connection {
    /**
     * Writes a single byte to the connection.
     * 
     * @param b the byte to write to the connection
     */
    void write(int b) throws IOException;

    /**
     * Reads a single byte from the connection.
     * 
     * @return the read byte
     */
    int read() throws IOException;

    /**
     * Writes a byte array to the connection.
     */
    void write(byte[] b) throws IOException;

    /**
     * Reads some number of bytes from the connection into the buffer b.
     * 
     * @param b the buffer to read into
     * @return the number of read bytes
     */
    int read(byte[] b) throws IOException;

    /**
     * Writes an object to the connection.
     * 
     * @param obj the object to write
     */
    void writeObject(Serializable obj) throws IOException, IllegalBlockSizeException;

    /**
     * Reads an object from the connection.
     * 
     * @return the read object
     */
    Object readObject()
            throws IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException;

    /**
     * Flushes the connection.
     */
    void flush() throws IOException;

    /**
     * Closes the connection.
     */
    void close() throws IOException;

    /**
     * Get the multiaddress of the connected peer.
     *
     * @return the multiaddress of the connected peer
     */
    String getRemoteMultiaddress();
}
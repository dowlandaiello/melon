/**
 * Implements a set of upgradable, generic network transports.
 */
package com.dowlandaiello.melon.transport;

import java.io.IOException;

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
     * @throws IOException
     */
    public void write(int b) throws IOException;

    /**
     * Reads a single byte from the connection.
     * 
     * @return the read byte
     * @throws IOException
     */
    public int read() throws IOException;

    /**
     * Writes a byte array to the connection.
     * 
     * @throws IOException
     */
    public void write(byte[] b) throws IOException;

    /**
     * Reads some number of bytes from the connection into the buffer b.
     * 
     * @param b the buffer to read into
     * @return the number of read bytes
     */
    public int read(byte[] b) throws IOException;

    /**
     * Writes an object to the connection.
     * 
     * @param obj the object to write
     * @throws IOException
     */
    public void writeObject(Object ojb) throws IOException;

    /**
     * Reads an object from the connection.
     * 
     * @return the read object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object readObject() throws IOException, ClassNotFoundException;

    /**
     * Flushes the connection.
     * 
     * @throws IOException
     */
    public void flush() throws IOException;

    /**
     * Closes the connection.
     * 
     * @throws IOException
     */
    public void close() throws IOException;
}
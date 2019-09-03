/**
 * Implements a set of commonly used cryptographic types and helper methods.
 */
package com.dowlandaiello.melon.crypto;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.digest.SHA3;

/**
 * Represents a generic hash.
 * 
 * @author Dowland Aiello
 * @since 1.0
 */
public class Hash {
    /**
     * The contents of the hash.
     */
    public final byte[] contents;

    /**
     * Initializes a new Hash.
     * 
     * @param b the desired contents of the hash
     */
    public Hash(byte[] b) {
        this.contents = b; // Set contents
    }

    /**
     * Hash a given input via sha3.
     * 
     * @param b the input to hash
     * @return the hashed input
     */
    public static Hash sha3(byte[] b) {
        SHA3.DigestSHA3 digest = new SHA3.Digest256(); // Initialize sha3 digest

        return new Hash(digest.digest(b)); // Return hash
    }

    /**
     * Convert a given string to a hash.
     * 
     * @param s the string to decode
     */
    public static Hash fromString(String s) {
        try {
            return new Hash((byte[]) Hex.decodeHex(s.toCharArray())); // Return the decoded hash
        } catch (DecoderException e) {
            return new Hash(null); // Return an empty hash
        }
    }

    /**
     * Converts a hash to a hex-encoded string.
     * 
     * @return the hex-encoded string representation of the hash
     */
    public String toString() {
        return Hex.encodeHexString(this.contents); // Return hex-encoded string
    }
}
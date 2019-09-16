package com.dowlandaiello.melon.pubsub;

import java.io.Serializable;

/**
 * Represents a pub-sub message.
 *
 * @author Dowland Aiello
 * @since 1.0
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The topic of the message.
     */
    public String topic;

    /**
     * The contents of the message.
     */
    public Serializable contents;

    public Message(String topic, Serializable contents) {
        this.topic = topic; // Set topic
        this.contents = contents; // Set contents
    }
}

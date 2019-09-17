package com.dowlandaiello.melon.pubsub;

/**
 * Represents an abstract method used to handle incoming pubsub messages.
 */
public interface Handler {
    /**
     * Handles an incoming pubsub message.
     *
     * @param message the message to handle
     */
    public void handleIncomingMessage(Message message);
}

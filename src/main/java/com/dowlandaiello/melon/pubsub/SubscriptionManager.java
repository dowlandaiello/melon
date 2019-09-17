package com.dowlandaiello.melon.pubsub;

import com.dowlandaiello.melon.transport.connection.Connection;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Represents a collection of subscribed-to classes, and their corresponding
 * handler methods.
 */
public class SubscriptionManager {
    /**
     * The register of callbacks per each topic.
     */
    private HashMap<String, Handler> handlers;

    /**
     * Subscribes to a given topic.
     *
     * @param topic the topic to subscribe to
     * @param handler the callback to use to handle the connection
     */
    public void subscribe(String topic, Handler handler) {
        this.handlers.put(topic, handler); // Subscribe to the given topic
    }

    /**
     * Determines whether or not the topic is subscribed to by this manager.
     *
     * @param topic the topic to check the status of
     * @return whether or not the manager has a handler for this topic
     */
    public boolean isSubscribed(String topic) {
        return this.handlers.containsKey(topic); // Return whether or not the manager has the key
    }

    /**
     * Handles the given connection according to the manager's handling methods.
     *
     * @param conn the connection to handle
     */
    public void handleConnection(Connection conn) throws ClassNotFoundException, BadPaddingException, IllegalBlockSizeException, IOException {
        Message pubsubMessage = (Message) conn.readObject(); // Read an incoming object

        if (this.handlers.containsKey(pubsubMessage.topic)) this.handlers.get(pubsubMessage.topic).handleIncomingMessage(pubsubMessage); // Handle the message
    }
}

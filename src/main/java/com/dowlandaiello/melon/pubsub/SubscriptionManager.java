package com.dowlandaiello.melon.pubsub;

import com.dowlandaiello.melon.peerstore.Peerstore;
import com.dowlandaiello.melon.transport.connection.Connection;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.IntStream;

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
     * The peerstore.
     */
    private Peerstore peerstore;

    /**
     * Initializes a new subscription manager with the given peerstore.
     * 
     * @param peerstore the store used in conjunction with the subscription manager
     */
    public SubscriptionManager(Peerstore peerstore) {
        this.peerstore = peerstore; // Set the peerstore of the instance
    }

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
     * Publishes a message.
     *
     * @param message the message to publish
     */
    public void publish(Message message) {
        HashMap<String, Connection> connections = this.peerstore.getRegisteredPeers(); // Get a hashmap of registered peers

        connections.forEach((k, v) -> {
            class Publisher extends Thread {
                public void run() {
                    try {
                        v.writeObject(message); // Write the message
                    } catch (IOException | IllegalBlockSizeException e) {
                        e.printStackTrace(); // Log an encountered exception
                    }
                }
            }

            Publisher publisher = new Publisher(); // Initialize a new publisher
            publisher.start(); // Run the publisher
        }); // Send to each of the connected peers
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

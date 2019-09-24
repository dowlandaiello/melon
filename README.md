# melon

:watermelon: Modular peer-to-peer communications library for Java with minimal overhead.

## Introduction

Melon is a modular peer-to-peer communications library for Java. Melon provides
a flexible, dev-oriented API for networking in Java that can be dissected,
reconfigured, and built upon to serve a user's needs.

### Core Components of the Melon Framework

* `Hosts`

   The `Host` class is the furthest layer of abstraction that Melon provides.
   Hosts serve as fully configured, assembled "kits" of individual building
   blocks. By default, a host has a preconfigured configuration, but individual
   properties of a host can be changed through options passed in to the `Host`
   constructor.
* `Transports`

   The `Transport` interface represents any generic network transport.
   As of yet, only a `Tcp` transport comes with Melon.
* `Multi-Addresses`

   A multiaddress is a way of representing the address, port, and id of a
   specific peer. In Melon, multiaddresses are the main method of communicating
   internet addresses.
   
   Multiaddresses are usually contain the ip protocol, ip address, transport
   protocol, port, and public key of a peer encoded via hexadecimal. For example:
   
   `/ip4/108.41.124.60/tcp/3000/Qmcpo2iLBikrdf1d6QU6vXuNb6P7hwrbNPW9kLAH8eG67z`
* `Peerstores`
    
   A `Peerstore` is the main method of "caching" connections to known peers in
   Melon. Once a connection to a peer is established, it is saved in the
   `Peerstore.`
* `Pubsub`

   Pubsub is a method of message propagation in distributed networks. Melon
   implements this functionality through the `SubscriptionManager` class.
   An instance of this class is included, by default, in all `Hosts`.
* `Upgrades`

    `Upgrades` come in many different forms. The most common kind, transport
    upgrades, can add support for all kinds of features to a given transport,
    simply by calling `.withUpgrade()`. The `Secio` upgrade, for example,
    implements communications encryption for any `Transport`.
* `Options`

    When a `Host` is initialized, a user might want to, for example, use a
    custom transport for communications. This can be achieved through the use
    of the `TransportOption`.
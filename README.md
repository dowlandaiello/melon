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
* `Upgrades`

    `Upgrades` come in many different forms. The most common kind, transport
    upgrades, can add support for all kinds of features to a given transport,
    simply by calling `.withUpgrade()`. The `Secio` upgrade, for example,
    implements communications encryption for any `Transport`.
* `Options`

    When a `Host` is initialized, a user might want to, for example, use a
    custom transport for communications. This can be achieved through the use
    of the `TransportOption`.
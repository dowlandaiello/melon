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
   properties of a host can be changed through options passed in to a `Host`
   constructor.
* `Transports`
* `Upgrades`
* `Options`
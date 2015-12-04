# SmartenIT: Socially-aware Management of New Overlay Application Traffic combined with Energy Efficiency in the Internet

## Overview

The tighter integration of network management, overlay service functionality, and overlay application knowledge, as for instance of Online Social Networks and Quality-of-Experience metrics, can lead to cross-layer optimization of network operations. Therefore, SmartenIT considers this as the promising approach to offer a new business potential in operational perspectives for all stakeholders involved, including cloud providers, Internet Service Providers, telecommunication providers, overlay providers, and application providers.

By addressing accordingly load and traffic patterns of the Internet traffic or special application requirements and by exploiting them at the same time with respect to social awareness (in terms of user relations and interests), QoE-awareness, and energy efficiency with respect to both end-user devices and underlying networking infrastructure, SmartenIT is dedicated to ensure an operationally efficient management of traffic, exemplified with real-life scenarios.

## Description

The SmartenIT software implements 2 traffic management mechanisms: (a) the Dynamic Traffic Management (DTM), which consists of 2 entities, SBox and SDN controller, and (b) the RB-Tracker and Home Router Sharing based on Trust (RB-HORST), including 2 entities, uNaDa and end-user application. Overall, the project is composed of five modules:

* **sbox**: This module includes all the sub-modules of the SBox entity. It implements the functionalities of the Dynamic Traffic Management mechanism.

* **sdn**: This module provides the extension of Floodlight SDN controller, to offer the SmartenIT capabilities. It implements the functionalities of the Dynamic Traffic Management mechanism.

* **unada**: This module includes all the sub-modules of the uNaDa entity. It implements the functionalities of the RB-HORST mechanism.

* **enduser**: This module includes all the sub-modules of the end-user entity. It implements the Android application of the RB-HORST mechanism.

* **dist**: This module generates the release artifacts of the SmartenIT software.


## Instructions

Instructions to build the SmartenIT software and generate the required executables can be found in the BUILD.txt file.

INSTALL.txt file defines the software prerequisites and the instructions to run the SmartenIT executables.


## Resources

Visit the SmartenIT [web site](http://www.smartenit.eu/) where you can find the description of the 2 mechanisms and publications of the SmartenIT project.


- - -

Copyright 2015, the Members of the SmartenIT Consortium

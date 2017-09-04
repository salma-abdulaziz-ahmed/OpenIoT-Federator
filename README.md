# OpenIoT-Federator

# The Project Overview
The objective of this project is the deployment of publish/subscribe IoT Services for multi-query federated support using OpenIoT platform
OpenIoT platform solved data interoperability problem by using semantic technologies like the OpenIoT ontology and linked data concepts. however, different OpenIoT instances deployed in different locations can not discover each other and accordingly they can not query each others’ sensor data. The goal of this project is to enable platform discovery in OpenIoT and interconnect different OpenIoT instances in different locations. AWS IoT is used as a central registry for platform discovery where MQTT publish/subscribe protocol is used to enable federation and exchange management metadata. Using the management metadata, each OpenIoT deployment is aware of the existence of other instances which enables the execution of distributed queries to other platforms accessing their management data

# The Architecture and Data Flow Diagram:
Please see the Wiki


# How ti install and use the code:

1) create an account on AWS IoT
2) create a new OpenIoT thing in Registry 
3) set the policy for OpenIoT connection under security
4) create a certificate under security
5)  Use your account on AWS to download three account related files:
The certificate file
The private key file 
The Public key file 

6) Edit the aws-iot-sdk-samples.properties file found in federator.core/src/main/resources/federator/core with the path to each of the files stated above

7) Edit the same file with the Sparql Endpoint where the OpenIoT data is accessible

8) Run the Federator Module by running the the Main class (Backend)

9) use the publish UI page to publish platform management data

10) use the subscribe UI to subscribe to a certain topic and find the SPARQL endpoint

# The Federator Demo:
 Please see the Wiki
 




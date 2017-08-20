# OpenIoT-Federator

In order to get this code working you need to:

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




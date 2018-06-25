# euclid
### Overview
There are three categories to the backend for the data management and experiment modelling for Hilbert/Euclid framework (
User Management, Experiments, Buckets) . As of now, 
the way to run this backend is to run the Application.java file in the src/main/java/com/sample/ directory of each respective 
category. This is able to run as a jar (i.e. running "mvn clean install" and then "mvn package", but for testing purposes I
have run the Application.java file (through intelliJ) wherein I have also managed to change the port that each respective 
backend component is running in (i.e. UM-port 8090, Exp-port 8190, Buc-port 8290). 

All the dependencies should be in each backend component's pom, and importing into intelliJ will serve as the easiest option. 


### HOW TO RUN
```
cassandra -f
```

```cqlsh```   
FOLLOW the scripts with CQL commands to initialize the keyspaces and column families (future release will script this in Java)
Run Application.java
Modify port number if needed:
```-Dserver.port={PORT #}```
Once everything is up and running, now you can perform the API calls
For example:
```curl "localhost:8090/Experiments/allExperiments/userID=ER45E"```
```curl "localhost:8190/Users/addOneUser?firstName=Bob&lastName=Swagger&email=Bob@you.com&username=user&password=pass&lastPasswordChange=none&authLevel=admin"```
```curl "localhost:8290/Buckets/changeAlloc?bucketID=24233434wsdwe3eas&newAlloc=0.3&userID=34eds3d"```

Looking at the API specs for the respective backend components should shed some light on all the current
capabilities.

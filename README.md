## blobs-example

Build the app using

`mvn clean package`

After building the app use the following commands to:

1. Run the client _(http://localhost:9091)_

`java -jar target/blobExample-service-1.0-SNAPSHOT.jar sampleClient`

2. Run the server _(http://localhost:9090)_

`java -jar target/blobExample-service-1.0-SNAPSHOT.jar sampleServer`
 
 You can check if the application is working by hitting the following url:
 
 `http://localhost:9091/displayMessage?name=SampleClient`
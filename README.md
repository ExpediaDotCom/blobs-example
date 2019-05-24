## blobs-example

Build the app using

```mvn clean package```

>After building the app use the following commands to:

Run the client _(http://localhost:9091)_

```java -jar target/blobExample-service-1.0-SNAPSHOT.jar sampleClient```

Run the server _(http://localhost:9090)_

```java -jar target/blobExample-service-1.0-SNAPSHOT.jar sampleServer```
 
You can check if the application is working by hitting the following url:
 
 ```http://localhost:9091/displayMessage?name=SampleClient```
 
 ## Record Blobs
 
 To enable the recording of the blobs, set _`areBlobsEnabled`_ to true in _`config-client.yaml`_, run the client and server again and hit the above URL.
 
 You will be able to find the blobs created inside _`blobsRelativePath`_ set in the same config file.
 
 **Defaults:**
  
  _areBlobsEnabled: true_
 
 _blobsRelativePath: ./blobs_
 
 ## Run Benchmarks
 
 Build docker image in local:
 
 ```docker build -t blobs:blobs-example .```
 
 Run docker image
 
 ```docker run --name blobsExample -v <Source>:/app/bin/blobs_home/JMH-BenchmarkingResults --cpus <Cores> -m <Memory> <IMAGE_ID>```
 
 Where,
 ```properties
 Source=Full path of the directory you want to save the JMH benchmark run result to.
 
 Cores=Number of CPU cores you want to allocate to the container. Example: 3.0, 5.5, 4.
 
 IMAGE_ID=Docker IMAGE ID which can be found out by running `docker images` in CMD and then looking for `blobs` Repository.
 
 Memory=Container RAM. Example: 8192m.
```
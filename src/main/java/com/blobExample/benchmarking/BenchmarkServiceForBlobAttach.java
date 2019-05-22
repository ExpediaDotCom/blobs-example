package com.blobExample.benchmarking;

import com.blobExample.client.ClientResource;
import com.blobExample.models.ClientRequest;
import com.blobExample.models.ClientResponse;
import com.blobExample.models.ServerResponse;
import com.expedia.blobs.core.io.FileStore;
import java.io.File;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mockito.Mockito;
import org.openjdk.jmh.annotations.*;


import static org.mockito.Mockito.when;

public class BenchmarkServiceForBlobAttach {

    @State(Scope.Benchmark)
    public static class ClientState {

        @Setup(Level.Iteration)
        public void doSetup() {
            System.out.println("doSetup Called");

            template = "Hello ServerResource. I am %s!";
            defaultName = "BlobsBenchmark";
            client = Mockito.mock(Client.class);

            blobStore = setupBlobStore();

            clientRequest = new ClientRequest(defaultName, String.format(template, defaultName));

            serverResponse = new ServerResponse("BlobServer", "");
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            System.out.println("Tear down called");
            if (blobStore != null)
                blobStore.close();
        }

        private FileStore setupBlobStore() {
            File directory = setupBlobsDirectory();

            FileStore.Builder builder = new FileStore.Builder(directory)
                    .withShutdownWaitInSeconds(360)
                    .withManualShutdown();

            return builder.build();
        }

        private File setupBlobsDirectory() {
            String userDirectory = System.getProperty("user.dir");
            String directoryPath = new String(userDirectory).concat("/BenchmarkingBlobs");
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            return directory;
        }

        public ClientRequest clientRequest;
        public ServerResponse serverResponse;
        private FileStore blobStore;
        private String template;
        private String defaultName;
        private Client client;
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public ClientResponse blobsEnabled(ClientState state) {

        ClientResource clientResource = new ClientResource(state.template, state.defaultName, state.client, state.blobStore);

        setupBehaviour(state, clientResource);

        return clientResource.getMessageFromServer(null);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public ClientResponse blobsDisabled(ClientState state) {

        ClientResource clientResource = new ClientResource(state.template, state.defaultName, state.client, null);

        setupBehaviour(state, clientResource);

        return clientResource.getMessageFromServer(null);
    }

    private void setupBehaviour(ClientState state, ClientResource clientResource) {
        Response mockedResponse = Mockito.mock(Response.class);
        Invocation.Builder invocationBuilder = Mockito.mock(Invocation.Builder.class);
        WebTarget webTarget = Mockito.mock(WebTarget.class);

        when(clientResource.getJerseyClient().target("http://localhost:9090/hi")).thenReturn(webTarget);

        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(invocationBuilder);

        when(invocationBuilder.post(
                Mockito.any(Entity.class)
        )).thenReturn(mockedResponse);

        when(mockedResponse.readEntity(ServerResponse.class)).thenReturn(state.serverResponse);
    }
}

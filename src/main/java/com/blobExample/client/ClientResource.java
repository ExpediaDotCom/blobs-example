package com.blobExample.client;

import com.blobExample.BlobResources;
import com.blobExample.models.ClientRequest;
import com.blobExample.models.ClientResponse;
import com.blobExample.models.ServerResponse;
import com.codahale.metrics.annotation.Timed;
import com.expedia.blobs.core.*;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Path("/displayMessage")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private javax.ws.rs.client.Client jerseyClient;
    private BlobResources blobResources;

    public ClientResource(String template, String defaultName, Client jerseyClient, BlobResources blobResources) {
        this.template = template;
        this.defaultName = defaultName;
        counter = new AtomicLong();
        this.jerseyClient = jerseyClient;
        this.blobResources = blobResources;
    }

    @GET
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public ClientResponse getMessageFromServer(@QueryParam("name") String name) {

        final String clientName = name != null ? name : defaultName;
        final String message = String.format(template, clientName);

        final ClientRequest clientRequest = new ClientRequest(clientName, message);

        Blobs requestBlob = createBlob(createBlobFactory());

        if (requestBlob != null) {
            Map<String, String> requestBlobMetadata = new HashMap<>();
            requestBlobMetadata.put("name", name);

            writeBlob(requestBlob, clientRequest, requestBlobMetadata, BlobType.REQUEST);
        }

        WebTarget webTarget = jerseyClient.target("http://localhost:9090/hi");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);

        Response response = invocationBuilder.post(
                Entity.entity(
                        clientRequest,
                        MediaType.APPLICATION_JSON_TYPE
                )
        );

        ServerResponse serverResponse = response.readEntity(ServerResponse.class);

        Blobs responseBlob = createBlob(createBlobFactory());

        if (responseBlob != null) {
            Map<String, String> responseBlobMetadata = new HashMap<>();
            responseBlobMetadata.put("name", serverResponse.getServerName());

            writeBlob(responseBlob, serverResponse, responseBlobMetadata, BlobType.RESPONSE);
        }

        return new ClientResponse(counter.incrementAndGet(), message, serverResponse);
    }

    private void writeBlob(Blobs blob, Object data, Map<String, String> blobMetadata, BlobType blobType) {
        blob.write(blobType,
                ContentType.JSON,
                createDataCallback(data),
                createMetadataCallback(blobMetadata)
        );
    }

    private Consumer<OutputStream> createDataCallback(Object data) {
        Consumer<OutputStream> dataCallback = (outputStream) -> {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(data);
            } catch (IOException ex) {
                //TODO: handle the IO exception
            }
        };

        return dataCallback;
    }

    private Consumer<Metadata> createMetadataCallback(Map<String, String> metadata) {
        Consumer<Metadata> metadataCallback = m -> {
            metadata.forEach((key, value) -> m.add(key, value));
        };

        return metadataCallback;
    }

    private Blobs createBlob(BlobsFactory<BlobContext> blobsFactory) {
        if (blobResources != null && blobResources.getBlobContext() != null) {
            return blobsFactory.create(blobResources.getBlobContext());
        }
        return null;
    }

    private BlobsFactory<BlobContext> createBlobFactory() {
        return new BlobsFactory<>(blobResources.getBlobStore());
    }
}

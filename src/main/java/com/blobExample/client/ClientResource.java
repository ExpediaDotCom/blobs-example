package com.blobExample.client;

import com.blobExample.models.ClientRequest;
import com.blobExample.models.ClientResponse;
import com.blobExample.models.ServerResponse;
import com.codahale.metrics.annotation.Timed;
import com.expedia.blobs.core.*;
import com.expedia.blobs.core.BlobStore;
import com.expedia.blobs.core.Blobs;
import com.expedia.blobs.core.BlobsFactory;
import com.expedia.blobs.core.predicates.BlobsRateLimiter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/displayMessage")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private javax.ws.rs.client.Client jerseyClient;
    private BlobStore blobStore;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientResource.class);

    public ClientResource(String template, String defaultName, Client jerseyClient, BlobStore blobStore) {
        this.template = template;
        this.defaultName = defaultName;
        counter = new AtomicLong();
        this.jerseyClient = jerseyClient;
        this.blobStore = blobStore;
    }

    public Client getJerseyClient() {
        return jerseyClient;
    }

    @GET
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public ClientResponse getMessageFromServer(@QueryParam("name") String name) {

        final String clientName = name != null ? name : defaultName;
        final String message = String.format(template, clientName);
        final ClientRequest clientRequest = new ClientRequest(clientName, message);

        BlobContext blobContext = null;
        BlobsFactory<BlobContext> blobsFactory = null;
        if (blobStore != null) {
            blobContext = new SimpleBlobContext("ServerService", "getMessageFromServer");
            blobsFactory = createBlobFactory();

            Blobs requestBlob = createBlob(blobsFactory, blobContext);

            if (requestBlob != null) {
                Map<String, String> requestBlobMetadata = new HashMap<>();
                requestBlobMetadata.put("name", name);

                writeBlob(requestBlob, clientRequest, requestBlobMetadata, BlobType.REQUEST);
            }
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

        if (blobStore != null && blobContext != null && blobsFactory != null) {
            Blobs responseBlob = createBlob(blobsFactory, blobContext);

            if (responseBlob != null) {
                Map<String, String> responseBlobMetadata = new HashMap<>();
                responseBlobMetadata.put("name", serverResponse.getServerName());

                writeBlob(responseBlob, serverResponse, responseBlobMetadata, BlobType.RESPONSE);
            }
        }

        return new ClientResponse(counter.incrementAndGet(), message, serverResponse);
    }

    private void writeBlob(Blobs blob, Object data, Map<String, String> blobMetadata, BlobType blobType) {
        blob.write(blobType,
                ContentType.JSON,
                (outputStream) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        outputStream.write(objectMapper.writeValueAsBytes(data));
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Exception occured while converting object to bytes", e);
                    } catch (IOException e) {
                        LOGGER.error("Exception occured while writing data to stream for preparing blob", e);
                    }
                },
                m -> blobMetadata.forEach((key, value) -> m.add(key, value))
        );
    }

    private Blobs createBlob(BlobsFactory<BlobContext> blobsFactory, BlobContext blobContext) {
        if (blobStore != null && blobsFactory != null && blobContext != null) {
            return blobsFactory.create(blobContext);
        }
        return null;
    }

    private BlobsFactory<BlobContext> createBlobFactory() {
        BlobsRateLimiter<BlobContext> blobsRateLimiter = createBlobsRateLimiter();

        return new BlobsFactory<>(blobStore, blobsRateLimiter);
    }

    private BlobsRateLimiter<BlobContext> createBlobsRateLimiter() {
        return new BlobsRateLimiter<>(5);
    }
}

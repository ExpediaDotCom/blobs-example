package com.blobExample.models;

import com.blobExample.models.ServerResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientResponse {
    private long hit;
    private String clientMessage;
    private ServerResponse serverResponse;

    public ClientResponse() {
    }

    public ClientResponse(long hit, String clientMessage, ServerResponse serverResponse) {
        this.hit = hit;
        this.clientMessage = clientMessage;
        this.serverResponse = serverResponse;
    }

    @JsonProperty
    public long getHit() {
        return hit;
    }

    @JsonProperty
    public String getClientMessage() {
        return clientMessage;
    }

    @JsonProperty
    public ServerResponse getServerResponse() {
        return serverResponse;
    }
}

package com.blobExample.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientRequest {
    private String name;
    private String message;

    public ClientRequest(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public ClientRequest(){
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public void setMessage(String message) {
        this.message = message;
    }
}

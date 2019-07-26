package com.blobExample;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class CommonConfiguration extends Configuration {
    private BlobsConfiguration blobsConfiguration;

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty
    public BlobsConfiguration getBlobsConfiguration() {
        return blobsConfiguration;
    }

    @JsonProperty
    public void setBlobsConfiguration(BlobsConfiguration blobsConfiguration) {
        this.blobsConfiguration = blobsConfiguration;
    }
}

package com.blobExample;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;


public class BlobsConfiguration {

    private Store store;

    @NotEmpty
    private Boolean areBlobsEnabled;

    @JsonProperty
    public Boolean getAreBlobsEnabled() {
        return areBlobsEnabled;
    }

    @JsonProperty
    public void setAreBlobsEnabled(Boolean areBlobsEnabled) {
        this.areBlobsEnabled = areBlobsEnabled;
    }

    @JsonProperty
    public Store getStore() {
        return store;
    }

    @JsonProperty
    public void setStore(Store store) {
        this.store = store;
    }

    public BlobsConfiguration() {
    }


    public class Store{

        private String name;

        private String blobsRelativePath;

        public Store() {
        }

        @JsonProperty
        public String getName() {
            return name;
        }

        @JsonProperty
        public void setName(String name) {
            this.name = name;
        }

        public Store(String blobsRelativePath) {
            this.blobsRelativePath = blobsRelativePath;
        }

        @JsonProperty
        public String getBlobsRelativePath() {
            return blobsRelativePath;
        }

        @JsonProperty
        public void setBlobsRelativePath(String blobsRelativePath) {
            this.blobsRelativePath = blobsRelativePath;
        }
    }
}

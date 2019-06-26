package com.blobExample.client;

import com.blobExample.BlobsConfiguration;
import com.blobExample.CommonConfiguration;
import com.expedia.blobs.core.BlobStore;
import com.expedia.blobs.stores.io.FileStore;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.File;

public class ClientApplication extends Application<CommonConfiguration> {
    private final String FILE_STORE = "FileStore";
    private final String S3_STORE = "S3Store";

    public void start(String[] args) throws Exception {

        String[] newArgs = new String[]{"server", String.format("config-client.yaml")};

        ClientApplication clientApplication = new ClientApplication();
        clientApplication.run(newArgs);
    }

    @Override
    public String getName() {
        return "ClientResource Application";
    }

    @Override
    public void initialize(Bootstrap<CommonConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(CommonConfiguration commonConfiguration, Environment environment) throws Exception {
        final javax.ws.rs.client.Client client = new JerseyClientBuilder(environment).using(commonConfiguration.getJerseyClientConfiguration())
                .build(getName() + "ClientRequest");

        final ClientResource clientResource = new ClientResource(
                commonConfiguration.getTemplate(),
                commonConfiguration.getDefaultName(),
                client,
                initializeBlobStore(commonConfiguration.getBlobsConfiguration())
        );
        environment.jersey().register(clientResource);
    }

    private BlobStore initializeBlobStore(BlobsConfiguration blobsConfiguration) {
        if (!blobsConfiguration.getAreBlobsEnabled()) {
            return null;
        }

        BlobsConfiguration.Store store = blobsConfiguration.getStore();
        switch (store.getName()) {
            case FILE_STORE: {
                String userDirectory = System.getProperty("user.dir");
                String directoryPath = new String(userDirectory).concat(store.getBlobsRelativePath());
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                return createFileStore(directory);
            }
            case S3_STORE:
                return null;
        }
        return null;
    }

    private FileStore createFileStore(File directory) {
        FileStore.Builder builder = new FileStore.Builder(directory);
        return builder.build();
    }
}

package com.blobExample.client;

import com.blobExample.CommonConfiguration;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ClientApplication extends Application<CommonConfiguration> {
    public void start(String[] args) throws Exception{

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
    public void run(CommonConfiguration commonConfiguration, Environment environment){
        final javax.ws.rs.client.Client client = new JerseyClientBuilder(environment).using(commonConfiguration.getJerseyClientConfiguration())
                .build(getName()+"ClientRequest");

        final ClientResource clientResource = new ClientResource(
                commonConfiguration.getTemplate(),
                commonConfiguration.getDefaultName(),
                client
        );
        environment.jersey().register(clientResource);
    }
}

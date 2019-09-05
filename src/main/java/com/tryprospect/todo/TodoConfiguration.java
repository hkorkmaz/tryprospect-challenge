package com.tryprospect.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TodoConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty("database")
  private DataSourceFactory database;

  @Valid
  @NotNull
  @JsonProperty("nlpEnpoint")
  private String nlpEnpoint;

  @Valid
  @NotNull
  @JsonProperty("jerseyClient")
  private JerseyClientConfiguration jerseyClient;

  public JerseyClientConfiguration getJerseyClientConfiguration() {
    return jerseyClient;
  }

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  public String getNlpEndpoint() {
    return nlpEnpoint;
  }
}

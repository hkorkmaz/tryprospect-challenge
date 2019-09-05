package com.tryprospect.todo.core;

import com.tryprospect.todo.db.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.util.Date;

public class TodoEnricher {
  private static final Logger LOGGER = LoggerFactory.getLogger(TodoEnricher.class);

  private String endpoint;
  private Client httpClient;

  public TodoEnricher(String endpoint, Client httpClient) {
    this.endpoint = endpoint;
    this.httpClient = httpClient;
  }

  public Todo enrich(Todo todo) {
    try {

      if (todo.text == null)  // No need to call NLP api
        return todo;

      NlpResponse response = httpClient
          .target(endpoint)
          .queryParam("text", todo.text)
          .request()
          .get(NlpResponse.class);

      if (response.date != null) {
        todo.dueDate = new Date(response.date);
      }

    } catch (Exception exc) {
      LOGGER.error("", exc);
    }

    return todo;
  }
}

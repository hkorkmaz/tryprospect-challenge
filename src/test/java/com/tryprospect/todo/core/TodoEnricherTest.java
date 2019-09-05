package com.tryprospect.todo.core;

import com.tryprospect.todo.db.Todo;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({DropwizardExtensionsSupport.class})
class TodoEnricherTest {

  private Client CLIENT = ClientBuilder.newClient();
  private String nlpEnpoint = CLIENT_EXTENSION.baseUri().toString() + "/enrich";

  private static final DropwizardClientExtension CLIENT_EXTENSION = new DropwizardClientExtension(new NlpResource());


  @Test
  @DisplayName("should enrich todo with date")
  void testEnrichTodo() {
    TodoEnricher todoEnricher = new TodoEnricher(nlpEnpoint, CLIENT);

    Todo todo = new Todo();
    todo.text = "textWithDate";
    todo.isCompleted = false;


    Todo enrichedTodo = todoEnricher.enrich(todo);

    assertEquals(new Date(0), enrichedTodo.dueDate);
  }

  @Test
  @DisplayName("should not enrich todo if NLP api returns null")
  void testNotEnrichTodo() {
    TodoEnricher todoEnricher = new TodoEnricher(nlpEnpoint, CLIENT);

    Todo todo = new Todo();
    todo.text = "text Without Date";
    todo.isCompleted = false;

    Todo enrichedTodo = todoEnricher.enrich(todo);

    assertNull(enrichedTodo.dueDate);
  }

  @Test
  @DisplayName("should not enrich todo if NLP api fails")
  void testExceptionFromApi() {
    TodoEnricher todoEnricher = new TodoEnricher(nlpEnpoint, CLIENT);

    Todo todo = new Todo();
    todo.text = "brokenCall";
    todo.isCompleted = false;

    Todo enrichedTodo = todoEnricher.enrich(todo);

    assertNull(enrichedTodo.dueDate);
    assertEquals("brokenCall", todo.text);
  }


  @Path("/enrich")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public static class NlpResource {

    @GET
    public Response enrich(@QueryParam("text") String text) {
      NlpResponse nlpResponse = new NlpResponse();

      if (text.equals("textWithDate")) {
        nlpResponse.date = 0L;
      } else if (text.equals("brokenCall")) {
        return Response.serverError().build();
      }

      return Response.ok(nlpResponse).build();
    }
  }
}
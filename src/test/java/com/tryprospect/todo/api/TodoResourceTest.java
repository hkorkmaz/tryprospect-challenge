package com.tryprospect.todo.api;

import com.tryprospect.todo.core.TodoEnricher;
import com.tryprospect.todo.db.Todo;
import com.tryprospect.todo.db.Todos;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({DropwizardExtensionsSupport.class})
class TodoResourceTest {

  private static final Todos TODOS = mock(Todos.class);
  private static final TodoEnricher TODO_ENRICHER = mock(TodoEnricher.class);
  private ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);

  private static final ResourceExtension RESOURCES = ResourceExtension.builder()
      .addResource(new TodoResource(TODOS, TODO_ENRICHER))
      .build();

  @AfterEach
  void tearDown() {
    reset(TODOS);
    reset(TODO_ENRICHER);
  }

  @Test
  @DisplayName("should create todo")
  void testCreateTodo() {
    Todo todo = new Todo();
    todo.text = "some text";
    todo.isCompleted = false;

    Todo enrichedTodo = new Todo();
    enrichedTodo.text = todo.text;
    enrichedTodo.isCompleted = todo.isCompleted;
    enrichedTodo.dueDate = new Date();

    when(TODO_ENRICHER.enrich(any(Todo.class))).thenReturn(enrichedTodo);
    when(TODOS.insert(eq(enrichedTodo))).thenReturn(enrichedTodo);

    Response response = RESOURCES.target("/todos")
        .request(APPLICATION_JSON_TYPE)
        .post(Entity.entity(todo, APPLICATION_JSON_TYPE));


    assertEquals(Response.Status.OK, response.getStatusInfo());
    verify(TODOS).insert(todoCaptor.capture());
    verify(TODO_ENRICHER).enrich(todoCaptor.capture());
  }

  @Test
  @DisplayName("should update todo")
  void testUpdateTodo() {
    Todo todo = new Todo();
    todo.text = "some text";
    todo.isCompleted = false;

    Todo enrichedTodo = new Todo();
    enrichedTodo.text = todo.text;
    enrichedTodo.isCompleted = todo.isCompleted;
    enrichedTodo.dueDate = new Date();

    when(TODO_ENRICHER.enrich(any(Todo.class))).thenReturn(enrichedTodo);
    when(TODOS.update(eq(enrichedTodo))).thenReturn(enrichedTodo);

    Response response = RESOURCES.target("/todos/" + UUID.randomUUID())
        .request(APPLICATION_JSON_TYPE)
        .put(Entity.entity(todo, APPLICATION_JSON_TYPE));


    assertEquals(Response.Status.OK, response.getStatusInfo());
    verify(TODOS).update(todoCaptor.capture());
    verify(TODO_ENRICHER).enrich(todoCaptor.capture());
  }

  @Test
  @DisplayName("should delete todo")
  void testDeleteTodo() {
    Todo todo = new Todo();
    todo.text = "some text";
    todo.isCompleted = false;

    UUID id = UUID.randomUUID();

    doNothing().when(TODOS).deleteById((eq(id)));

    Response response = RESOURCES.target("/todos/" + id)
        .request(APPLICATION_JSON_TYPE)
        .delete();


    assertEquals(Response.Status.NO_CONTENT, response.getStatusInfo());
    verify(TODOS).deleteById(id);
  }

  @Test
  @DisplayName("should returns todo by id")
  void testFindTodo() {
    UUID id = UUID.randomUUID();

    Todo todo = new Todo();
    todo.text = "some text";
    todo.isCompleted = false;
    todo.id = id;

    when(TODOS.findById(eq(id))).thenReturn(Optional.of(todo));

    Response response = RESOURCES.target("/todos/" + id)
        .request(APPLICATION_JSON_TYPE)
        .get();


    assertEquals(Response.Status.OK, response.getStatusInfo());
    verify(TODOS).findById(id);
  }

  @Test
  @DisplayName("should returns todo list")
  void testFindAllTodos() {
    Todo todo1 = new Todo();
    todo1.text = "some text";
    todo1.isCompleted = false;

    Todo todo2 = new Todo();
    todo2.text = "some text 2";
    todo2.isCompleted = true;

    when(TODOS.findAll()).thenReturn(Arrays.asList(todo1, todo2));

    Response response = RESOURCES.target("/todos")
        .request(APPLICATION_JSON_TYPE)
        .get();


    assertEquals(Response.Status.OK, response.getStatusInfo());
    verify(TODOS).findAll();
  }
}
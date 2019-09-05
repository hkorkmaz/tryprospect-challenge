package com.tryprospect.todo.api;

import com.tryprospect.todo.core.TodoEnricher;
import com.tryprospect.todo.db.Todo;
import com.tryprospect.todo.db.Todos;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

  private final TodoEnricher todoEnricher;
  private final Todos todos;

  public TodoResource(Todos todos, TodoEnricher todoEnricher) {
    this.todoEnricher = todoEnricher;
    this.todos = todos;
  }

  @POST
  public Todo createTodo(Todo todo) {
    Todo enrichedTodo = todoEnricher.enrich(todo);
    return todos.insert(enrichedTodo);
  }

  @GET
  public List<Todo> getTodos() {
    return todos.findAll();
  }

  @GET
  @Path("/{id}")
  public Optional<Todo> getTodoById(@PathParam("id") UUID id) {
    return todos.findById(id);
  }

  @DELETE
  @Path("/{id}")
  public void deleteTodoById(@PathParam("id") UUID id) {
    todos.deleteById(id);
  }

  @PUT
  @Path("/{id}")
  public Optional<Todo> updateTodo(@PathParam("id") UUID id, Todo todo) {
    todo.id = id;
    Todo enrichedTodo = todoEnricher.enrich(todo);
    return Optional.ofNullable(todos.update(enrichedTodo));
  }
}

package com.tryprospect.todo.db;

import com.tryprospect.todo.TodoApplication;
import com.tryprospect.todo.TodoConfiguration;
import com.tryprospect.todo.util.EmbeddedPostgresSupport;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({EmbeddedPostgresSupport.class, DropwizardExtensionsSupport.class})
class TodosIntegrationTest {

  private static final DropwizardAppExtension<TodoConfiguration> RULE = new DropwizardAppExtension<>(
      TodoApplication.class,
      ResourceHelpers.resourceFilePath("test-config.yml")
  );

  private Todos todos = new JdbiFactory()
      .build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgresql")
      .onDemand(Todos.class);

  @Test
  @DisplayName("should return todo list")
  void testListTodo() {
    Todo todo1 = new Todo();
    todo1.text = "some text 1";
    todo1.isCompleted = false;

    Todo todo2 = new Todo();
    todo2.text = "some text 2";
    todo2.isCompleted = true;

    todos.insert(todo1);
    todos.insert(todo2);

    List<Todo> todoList = todos.findAll();

    assertNotNull(todoList);
    assertEquals(2, todoList.size());
  }

  @Test
  @DisplayName("should insert new todo")
  void testInsertTodo() {
    Todo todo = new Todo();
    todo.text = "some text 1";
    todo.isCompleted = false;

    Todo inserted = todos.insert(todo);

    assertNotNull(inserted);
    assertNotNull(inserted.id);
    assertEquals("some text 1", inserted.text);
  }

  @Test
  @DisplayName("should find todo by id")
  void testFindById() {
    Todo todo = new Todo();
    todo.text = "some text 1";
    todo.isCompleted = false;

    Todo inserted = todos.insert(todo);

    Optional<Todo> found = todos.findById(inserted.id);

    assertTrue(found.isPresent());
    assertEquals("some text 1", found.get().text);
  }

  @Test
  @DisplayName("should update todo partially")
  void testUpdate() {
    Todo todo = new Todo();
    todo.text = "some text 1";
    todo.isCompleted = false;
    todo.dueDate = null;

    Todo inserted = todos.insert(todo);

    inserted.text = "another text";
    Todo updated = todos.update(inserted);

    assertEquals("another text", updated.text);
    assertEquals(false, inserted.isCompleted);
    assertNull(inserted.dueDate);
    assertTrue(updated.lastModifiedAt.getTime() > inserted.lastModifiedAt.getTime());
  }

  @Test
  @DisplayName("should delete single todo")
  void testDelete() {
    Todo todo = new Todo();
    todo.text = "some text 1";
    todo.isCompleted = false;

    Todo todo2 = new Todo();
    todo2.text = "some text 1";
    todo2.isCompleted = false;

    Todo inserted = todos.insert(todo);
    Todo inserted2 = todos.insert(todo2);

    todos.deleteById(inserted.id);
    List<Todo> todoList = todos.findAll();

    assertEquals(1, todoList.size());
    assertEquals(inserted2.id, todoList.get(0).id);
  }
}
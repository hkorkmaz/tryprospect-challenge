package com.tryprospect.todo.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;
import java.util.UUID;

public class Todo {

  public UUID id;
  public String text;
  public Boolean isCompleted;
  public Date createdAt;
  public Date lastModifiedAt;

  @JsonInclude(Include.NON_NULL) // Ignore 'null'
  public Date dueDate;
}

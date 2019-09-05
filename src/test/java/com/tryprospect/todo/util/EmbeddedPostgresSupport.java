package com.tryprospect.todo.util;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class EmbeddedPostgresSupport implements BeforeAllCallback, BeforeEachCallback, ExtensionContext.Store.CloseableResource {

  private static EmbeddedPostgres pg = null;
  private int pgPort = 50049;

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    if (pg == null) {
      pg = EmbeddedPostgres.builder().setPort(pgPort).start();
      extensionContext.getRoot().getStore(GLOBAL).put("EMBEDDED_POSTGRES", this);
    }
  }

  @Override
  public void close() throws Throwable {
    pg.close();
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    try (Connection conn = pg.getPostgresDatabase().getConnection()) {
      Statement stmt = conn.createStatement();

      // TODO: Clean all tables
      stmt.execute("TRUNCATE TABLE todo");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}

package com.tryprospect.todo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tryprospect.todo.api.TodoResource;
import com.tryprospect.todo.core.TodoEnricher;
import com.tryprospect.todo.db.Todos;
import com.tryprospect.todo.lifecycle.ManagedFlywayMigration;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.client.Client;

public class TodoApplication extends Application<TodoConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TodoApplication().run(args);
    }

    @Override
    public String getName() {
        return "Todo API";
    }

    @Override
    public void initialize(final Bootstrap<TodoConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(final TodoConfiguration configuration, final Environment environment) {
        // DB
        DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();

        // Flyway
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
        // Automatically run Flyway migrations on startup
        environment.lifecycle().manage(new ManagedFlywayMigration(flyway));

        // JDBI
        JdbiFactory factory = new JdbiFactory();
        Jdbi jdbi = factory.build(environment, dataSourceFactory, "postgresql");


        Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());

        // DAOs
        Todos todos = jdbi.onDemand(Todos.class);

        // Services
        TodoEnricher dueDateAdder = new TodoEnricher(configuration.getNlpEndpoint(), client);

        // Resources
        environment.jersey().register(new TodoResource(todos, dueDateAdder));

        // Misc
        environment.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

}

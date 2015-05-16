package ru.hh.database.simulation;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

final class DataGenerator {

  private static final int numOfBatches = 10_000;
  private static final int numOfUsersInBatch = 100;

  public static void main(String... args) {

    final DataSource dataSource = DataSourceFactory.createHikariCP("jdbc:postgresql://127.0.0.1:5432/jdbc_example");
    final SimpleJdbcInsertOperations simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("users").usingGeneratedKeyColumns("user_id");

    for (int batchIndex = 0; batchIndex < numOfBatches; batchIndex++) {
      final Map<String,Object>[] batch = new Map[numOfUsersInBatch];
      for (int userIndex = 0; userIndex < numOfUsersInBatch; userIndex++) {
        final Map<String, Object> user = new HashMap<>();
        user.put("first_name", "fname" + userIndex);
        user.put("last_name", "lname" + userIndex);
        batch[userIndex] = user;
      }
      simpleJdbcInsert.executeBatch(batch);
    }
  }

  private DataGenerator() {
  }
}

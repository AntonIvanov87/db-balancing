package ru.hh.database.simulation;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.hh.collections.IntAccumulator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.System.nanoTime;

final class BackendThread extends Thread {

  private static final int maxUserId = 2_000_000;

  private final NamedParameterJdbcOperations namedParameterJdbcOperations;
  @SuppressWarnings("UnsecureRandomNumberGeneration")
  private final Random random = new Random();

  private volatile int[] timesNs;
  private volatile int failedOpsCount;

  BackendThread(final String name, final DataSource dataSource) {
    this.setName(name);
    this.namedParameterJdbcOperations = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public void run() {

    IntAccumulator timesNsAccumulator = IntAccumulator.empty();
    int failedOpsAccumulator = 0;

    while (!Thread.currentThread().isInterrupted()) {
      long selectStart = nanoTime();
      try {
        selectUser();
      } catch (RuntimeException e) {
        failedOpsAccumulator++;
        System.out.println("failed to select user: " + e.toString());
        continue;
      }
      timesNsAccumulator = timesNsAccumulator.add((int) (nanoTime() - selectStart));
    }

    publishResults(timesNsAccumulator.toArray(), failedOpsAccumulator);
  }

  private void selectUser() {

    final Map<String, Object> params = new HashMap<>();
    params.put("user_id", random.nextInt(maxUserId));

    namedParameterJdbcOperations.query(
            "SELECT * FROM users WHERE user_id = :user_id",
            params,
            rs -> null);
  }

  private void publishResults(final int[] timesNs, final int numOfFailures) {
    this.timesNs = timesNs;
    this.failedOpsCount = numOfFailures;
  }

  int[] getTimesNs() {
    return timesNs;
  }

  int getFailedOpsCount() {
    return failedOpsCount;
  }
}

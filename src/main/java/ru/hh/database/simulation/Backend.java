package ru.hh.database.simulation;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

final class Backend {

  private static final int numOfThreads = 3;
  private static final int testTimeMs = 5000;

  public static void main(String... args) throws InterruptedException {

    final DataSource hikari = DataSourceFactory.createC3P0(
            "jdbc:postgresql://127.0.0.1:6543,127.0.0.1:6544/jdbc_example?readOnly=true&loadBalanceHosts=true");
    while(true) {
      System.gc();
      test(hikari);
    }
  }

  private static void test(final DataSource dataSource) throws InterruptedException {

    final List<BackendThread> threads = createThreads(dataSource);
    final long start = currentTimeMillis();
    threads.forEach(Thread::start);

    Thread.sleep(testTimeMs);

    threads.forEach(Thread::interrupt);
    joinThreads(threads);
    final long stop = currentTimeMillis();

    showResults(threads, (int) (stop - start));
  }

  private static List<BackendThread> createThreads(final DataSource dataSource) {

    final List<BackendThread> threads = new ArrayList<>(numOfThreads);
    for (int i = 0; i < numOfThreads; i++) {
      threads.add(new BackendThread("BackendThread" + i, dataSource));
    }
    return threads;
  }

  private static void joinThreads(List<BackendThread> threads) throws InterruptedException {
    for (BackendThread thread : threads) {
      thread.join();
    }
  }

  private static void showResults(final List<BackendThread> threads, final int testDurationMs) {

    final DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
    int failedOpsCount = 0;

    for (BackendThread thread : threads) {
      for (int timeNs : thread.getTimesNs()) {
        descriptiveStatistics.addValue(timeNs / 1_000_000.0);
      }
      failedOpsCount += thread.getFailedOpsCount();
    }

    final int opsPerSec = (int) (1000 * descriptiveStatistics.getN() / testDurationMs);
    final float failedOpsPercent = (float) (100.0 * failedOpsCount / (failedOpsCount + descriptiveStatistics.getN()));

    final String result = String.format("%d ok ops, %d ops / sec, %.2f ms / op, 99%% %.2f ms / op, %d failed ops, %.2f%% failed ops",
            descriptiveStatistics.getN(), opsPerSec, descriptiveStatistics.getMean(), descriptiveStatistics.getPercentile(99.0),
            failedOpsCount, failedOpsPercent);
    System.out.println(result);
  }

  private Backend() {
  }
}

package ru.hh.database.simulation;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

final class DataSourceFactory {

  static DataSource createC3P0(final String url) {
    final ComboPooledDataSource dataSource = new ComboPooledDataSource();
    dataSource.setJdbcUrl(url);
    dataSource.setUser("jdbc_example");
    dataSource.setPassword("123");

    dataSource.setMaxConnectionAge(30);

    return dataSource;
  }

  static DataSource createHikariCP(final String url) {

    final HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername("jdbc_example");
    config.setPassword("123");

    config.setConnectionTimeout(1000L);
    //config.setIdleTimeout(10_000L);
    config.setMaxLifetime(30_000L);
    config.setValidationTimeout(1000L);
    config.setLeakDetectionThreshold(2_000L);

    return new HikariDataSource(config);
  }

  static DataSource createPGSimple(final String url) {

    final PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUrl(url);
    dataSource.setUser("jdbc_example");
    dataSource.setPassword("123");

    return dataSource;
  }

  private DataSourceFactory() {
  }
}

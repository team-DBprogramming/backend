package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class AssignmentSchemaSqlTest {

  private static final Path ORIGINAL_SCHEMA_PATH = Path.of("src/main/resources/db/schema.sql");
  private static final Path SCHEMA_INIT_DIR = Path.of("src/main/resources/db/init");

  @Test
  void schemaWrapperAndSplitInitFilesExist() {
    assertThat(ORIGINAL_SCHEMA_PATH).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("01-drop.sql")).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("02-tables.sql")).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("03-views.sql")).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("04-functions.sql")).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("05-procedures.sql")).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("06-triggers.sql")).exists();
    assertThat(SCHEMA_INIT_DIR.resolve("99-dummy-data.sql")).exists();
  }

  @Test
  void assignmentSchemaUsesLectureMaterialTableAndColumnNames() throws IOException {
    String schema = schemaSql();

    assertThat(schema).contains("create table student");
    assertThat(schema).contains("s_id varchar2(10)");
    assertThat(schema).contains("s_pwd varchar2(30)");
    assertThat(schema).contains("s_major varchar2(30)");

    assertThat(schema).contains("create table lecture");
    assertThat(schema).contains("id number(2)");
    assertThat(schema).contains("no varchar2(10)");
    assertThat(schema).contains("subject varchar2(20)");
    assertThat(schema).contains("prof varchar2(10)");

    assertThat(schema).contains("create table class");
    assertThat(schema).contains("p_id varchar2(6)");
    assertThat(schema).contains("c_id varchar2(10)");
    assertThat(schema).contains("c_no number");
    assertThat(schema).contains("c_where varchar2(30)");

    assertThat(schema).contains("create table enroll");
    assertThat(schema).contains("e_year number");
    assertThat(schema).contains("e_semester number");

    assertThat(schema).contains("create table registration_period");
    assertThat(schema).contains("period_type varchar2(10)");
    assertThat(schema).contains("target_year number(1) not null");
  }

  @Test
  void assignmentSchemaContainsRequiredDatabaseProgrammingTechniques() throws IOException {
    String schema = schemaSql();

    assertThat(schema).contains("create or replace function date2enrollyear");
    assertThat(schema).contains("create or replace function date2enrollsemester");
    assertThat(schema).contains("create or replace procedure insertenroll");
    assertThat(schema).contains("sys_refcursor");
    assertThat(schema).contains("create or replace trigger");
  }

  private String schemaSql() throws IOException {
    try (var paths = Files.list(SCHEMA_INIT_DIR)) {
      return paths
          .filter(path -> path.getFileName().toString().endsWith(".sql"))
          .filter(path -> !path.getFileName().toString().equals("99-dummy-data.sql"))
          .sorted(Comparator.comparing(path -> path.getFileName().toString()))
          .map(this::readString)
          .reduce("", (left, right) -> left + "\n" + right)
          .toLowerCase()
          .replaceAll("\\s+", " ");
    }
  }

  private String readString(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read SQL file: " + path, e);
    }
  }
}

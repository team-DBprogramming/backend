package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AssignmentSchemaSqlTest {

  private static final Path ORIGINAL_SCHEMA_PATH = Path.of("src/main/resources/db/schema.sql");
  private static final Path ASSIGNMENT_SCHEMA_PATH =
      Path.of("src/main/resources/db/assignment-schema.sql");

  @Test
  void originalSchemaIsPreservedAndAssignmentSchemaExists() throws IOException {
    assertThat(ORIGINAL_SCHEMA_PATH).exists();
    assertThat(ASSIGNMENT_SCHEMA_PATH).exists();
  }

  @Test
  void assignmentSchemaUsesLectureMaterialTableAndColumnNames() throws IOException {
    String schema = Files.readString(ASSIGNMENT_SCHEMA_PATH).toLowerCase();

    assertThat(schema).contains("create table student");
    assertThat(schema).contains("s_id varchar2(10)");
    assertThat(schema).contains("s_pwd varchar2(30)");
    assertThat(schema).contains("s_major varchar2(30)");

    assertThat(schema).contains("create table lecture");
    assertThat(schema).contains("id number(2)");
    assertThat(schema).contains("no number(4)");
    assertThat(schema).contains("subject varchar2(20)");
    assertThat(schema).contains("prof varchar2(10)");

    assertThat(schema).contains("create table class");
    assertThat(schema).contains("p_id varchar2(6)");
    assertThat(schema).contains("c_id number");
    assertThat(schema).contains("c_no number");
    assertThat(schema).contains("c_where varchar2(30)");

    assertThat(schema).contains("create table enroll");
    assertThat(schema).contains("e_year number");
    assertThat(schema).contains("e_semester number");

    assertThat(schema).contains("create table registration_period");
    assertThat(schema).contains("period_type varchar2(10)");
    assertThat(schema).doesNotContain("target_year");
  }

  @Test
  void assignmentSchemaContainsRequiredDatabaseProgrammingTechniques() throws IOException {
    String schema = Files.readString(ASSIGNMENT_SCHEMA_PATH).toLowerCase();

    assertThat(schema).contains("create or replace function date2enrollyear");
    assertThat(schema).contains("create or replace function date2enrollsemester");
    assertThat(schema).contains("create or replace procedure insertenroll");
    assertThat(schema).contains("sys_refcursor");
    assertThat(schema).contains("create or replace trigger");
  }
}

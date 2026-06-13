package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SchemaSqlTest {

  private static final Path SCHEMA_PATH = Path.of("src/main/resources/db/schema.sql");
  private static final Path PROFESSOR_DASHBOARD_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorDashboardMapper.xml");

  @Test
  void schemaMatchesCommonAndProfessorScope() throws IOException {
    String schema = Files.readString(SCHEMA_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(schema).doesNotContain("create table waitlist");
    assertThat(schema).doesNotContain("create table grade");
    assertThat(schema).doesNotContain("helpful_count");
    assertThat(schema).doesNotContain("waitlist_count");
    assertThat(schema).doesNotContain("attendance_score");

    assertThat(schema).contains("create table course_request");
    assertThat(schema).contains("reason clob not null");
    assertThat(schema).contains("status varchar2(20) default 'pending' not null");
    assertThat(schema).contains("professor_note clob");
    assertThat(schema).contains("create table user_account");
    assertThat(schema).contains("login_id varchar2(50) not null");
    assertThat(schema).contains("password_hash varchar2(255) not null");
    assertThat(schema).contains("constraint uk_user_account_login_id unique ( login_id )");
    assertThat(schema).contains("create index idx_user_account_role");
    assertThat(schema).contains("c_year number(4) not null");
    assertThat(schema).contains("c_semester number(1) not null");
    assertThat(schema).contains("constraint chk_class_semester check ( c_semester in ( 1, 2 ) )");
    assertThat(schema).contains("recipient_s_id varchar2(10)");
    assertThat(schema).contains("target_c_id varchar2(10)");
    assertThat(schema).contains("create table refresh_token");
    assertThat(schema).contains("login_type varchar2(10) not null");
    assertThat(schema).contains("token_hash varchar2(255) not null");
    assertThat(schema).contains("remember_me number(1) default 0 not null");
    assertThat(schema).contains("revoked_at timestamp");
  }

  @Test
  void professorDashboardMapperUsesCurrentSchemaViewsAndCallableProcedures() throws IOException {
    String schema = Files.readString(SCHEMA_PATH).toLowerCase().replaceAll("\\s+", " ");
    String mapper =
        Files.readString(PROFESSOR_DASHBOARD_MAPPER_PATH)
            .toLowerCase()
            .replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace view v_professor_dashboard_class");
    assertThat(schema).contains("create or replace procedure get_professor_dashboard_summary");
    assertThat(schema).contains("create or replace procedure get_professor_today_schedules");
    assertThat(schema).contains("create or replace procedure get_professor_assigned_courses");
    assertThat(schema).contains("p_professor_user_id in user_account.user_id%type");
    assertThat(schema).contains("p_result out sys_refcursor");
    assertThat(schema).contains("case");
    assertThat(schema).contains("nvl");

    assertThat(mapper).doesNotContain("course_section");
    assertThat(mapper).doesNotContain("section_schedule");
    assertThat(mapper).doesNotContain("classroom");
    assertThat(mapper).doesNotContain(" enrollment ");
    assertThat(mapper).doesNotContain(" semester ");
    assertThat(mapper).doesNotContain("recipient_user_id");
    assertThat(mapper).doesNotContain("target_section_id");

    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("call get_professor_dashboard_summary");
    assertThat(mapper).contains("call get_professor_today_schedules");
    assertThat(mapper).contains("call get_professor_assigned_courses");
  }
}
package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SchemaSqlTest {

  private static final Path SCHEMA_PATH = Path.of("src/main/resources/db/schema.sql");

  @Test
  void schemaMatchesCommonAndProfessorScope() throws IOException {
    String schema = Files.readString(SCHEMA_PATH).toLowerCase();

    assertThat(schema).doesNotContain("create table waitlist");
    assertThat(schema).doesNotContain("create table grade");
    assertThat(schema).doesNotContain("helpful_count");
    assertThat(schema).doesNotContain("waitlist_count");
    assertThat(schema).doesNotContain("attendance_score");

    assertThat(schema).contains("create table course_request");
    assertThat(schema).contains("reason clob not null");
    assertThat(schema).contains("status varchar2(20) default 'pending' not null");
    assertThat(schema).contains("professor_note clob");
    assertThat(schema).contains("recipient_user_id number(19) not null");
    assertThat(schema).contains("sender_user_id number(19)");
    assertThat(schema).contains("target_section_id number(19)");
    assertThat(schema).contains("idx_course_section_professor_semester");
    assertThat(schema).contains("create table refresh_token");
    assertThat(schema).contains("token_hash varchar2(255) not null");
    assertThat(schema).contains("remember_me number(1) default 0 not null");
    assertThat(schema).contains("revoked_at timestamp");
  }
}

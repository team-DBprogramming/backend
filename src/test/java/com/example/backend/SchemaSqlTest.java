package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SchemaSqlTest {

  private static final Path SCHEMA_INIT_DIR = Path.of("src/main/resources/db/init");
  private static final Path NOTIFICATION_MAPPER_PATH =
      Path.of("src/main/resources/mappers/notification/NotificationMapper.xml");
  private static final Path PROFESSOR_COURSE_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorCourseMapper.xml");
  private static final Path PROFESSOR_COURSE_REQUEST_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorCourseRequestMapper.xml");
  private static final Path PROFESSOR_DASHBOARD_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorDashboardMapper.xml");
  private static final Path PROFESSOR_MESSAGE_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorMessageMapper.xml");
  private static final Path PROFESSOR_STUDENT_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorStudentMapper.xml");
  private static final Path PROFESSOR_STUDENT_EXPORT_MAPPER_PATH =
      Path.of("src/main/resources/mappers/professor/ProfessorStudentExportMapper.xml");

  @Test
  void schemaMatchesCommonAndProfessorScope() throws IOException {
    String schema = readSchemaSql();

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
    assertThat(schema).contains("create or replace view v_notification_list");
    assertThat(schema).contains("dbms_lob.substr(n.body, 4000, 1) as body");
    assertThat(schema).contains("|| '遺꾨컲'");
    assertThat(schema).contains("n.created_at as sort_created_at");
  }

  @Test
  void notificationListQueryUsesView() throws IOException {
    String schema = readSchemaSql();
    String mapper = Files.readString(NOTIFICATION_MAPPER_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace view v_notification_list");
    assertThat(schema).contains("|| '遺꾨컲'");
    assertThat(mapper).contains("from v_notification_list");
  }

  @Test
  void notificationDetailUsesViewAndCallableProcedure() throws IOException {
    String schema = readSchemaSql();
    String mapper = Files.readString(NOTIFICATION_MAPPER_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace view v_notification_detail");
    assertThat(schema).contains("create or replace procedure get_notification_detail");
    assertThat(schema).contains("p_student_id in student.s_id%type");
    assertThat(schema).contains("p_notification_id in notification.notification_id%type");
    assertThat(schema).contains("p_result out sys_refcursor");
    assertThat(schema).contains("when no_data_found then");
    assertThat(schema).contains("raise_application_error(-20041, 'notification_not_found')");

    assertThat(mapper).contains("callgetnotificationdetail");
    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("{ call get_notification_detail(");
    assertThat(mapper).contains("jdbctype=cursor");
    assertThat(mapper).doesNotContain("<select id=\"findnotification\"");
  }

  @Test
  void notificationMarkAsReadUsesCallableProcedureWithRowCountGuard() throws IOException {
    String schema = readSchemaSql();
    String mapper = Files.readString(NOTIFICATION_MAPPER_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace procedure mark_notification_as_read");
    assertThat(schema).contains("p_student_id in student.s_id%type");
    assertThat(schema).contains("p_notification_id in notification.notification_id%type");
    assertThat(schema).contains("if sql%rowcount = 0 then");
    assertThat(schema).contains("raise_application_error(-20042, 'notification_not_found')");

    assertThat(mapper).contains("callmarknotificationasread");
    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("{ call mark_notification_as_read(");
    assertThat(mapper).doesNotContain("<update id=\"markasread\"");
  }

  @Test
  void professorCourseMapperUsesCurrentSchemaTables() throws IOException {
    String mapper =
        Files.readString(PROFESSOR_COURSE_MAPPER_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(mapper).doesNotContain("course_section");
    assertThat(mapper).doesNotContain("section_schedule");
    assertThat(mapper).doesNotContain("classroom");
    assertThat(mapper).doesNotContain(" enrollment ");
    assertThat(mapper).doesNotContain(" semester ");

    assertThat(mapper).contains("from user_account ua");
    assertThat(mapper).contains("join professor p on p.p_id = ua.login_id");
    assertThat(mapper).contains("join class cls");
    assertThat(mapper).contains("join lecture l");
    assertThat(mapper).contains("from class_time ct");
    assertThat(mapper).contains("from enroll e");
    assertThat(mapper).contains("join review r on r.e_id = e.e_id");
    assertThat(mapper).contains("cls.c_year");
    assertThat(mapper).contains("cls.c_semester");
  }

  @Test
  void professorCourseRequestMapperUsesCurrentSchemaAndCallableProcedure() throws IOException {
    String schema = readSchemaSql();
    String mapper =
        Files.readString(PROFESSOR_COURSE_REQUEST_MAPPER_PATH)
            .toLowerCase()
            .replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace procedure process_course_request");
    assertThat(schema).contains("p_status in varchar2");
    assertThat(schema).contains("processed_by_p_id");
    assertThat(schema).contains("insert into notification");
    assertThat(schema).contains("recipient_s_id");
    assertThat(schema).contains("sender_p_id");

    assertThat(mapper).doesNotContain("course_section");
    assertThat(mapper).doesNotContain(" section_id");
    assertThat(mapper).doesNotContain(" semester ");
    assertThat(mapper).doesNotContain("course_code");
    assertThat(mapper).doesNotContain("s.student_id");
    assertThat(mapper).doesNotContain("cr.student_id");
    assertThat(mapper).doesNotContain("professor_id");
    assertThat(mapper).doesNotContain("recipient_user_id");
    assertThat(mapper).doesNotContain("sender_user_id");
    assertThat(mapper).doesNotContain("target_section_id");
    assertThat(mapper).doesNotContain("processed_by_professor_id");

    assertThat(mapper).contains("from user_account ua");
    assertThat(mapper).contains("join professor p on p.p_id = ua.login_id");
    assertThat(mapper).contains("join class cls");
    assertThat(mapper).contains("join lecture l");
    assertThat(mapper).contains("from course_request cr");
    assertThat(mapper).contains("join student s on s.s_id = cr.s_id");
    assertThat(mapper).contains("dbms_lob.substr(cr.reason");
    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("call process_course_request");
  }

  @Test
  void professorDashboardMapperUsesCurrentSchemaViewsAndCallableProcedures() throws IOException {
    String schema = readSchemaSql();
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

  @Test
  void professorMessageMapperUsesCurrentSchemaAndCallableProcedure() throws IOException {
    String schema = readSchemaSql();
    String mapper =
        Files.readString(PROFESSOR_MESSAGE_MAPPER_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace procedure send_professor_message");
    assertThat(schema).contains("p_professor_user_id in user_account.user_id%type");
    assertThat(schema).contains("p_student_ids in varchar2");
    assertThat(schema).contains("p_message in notification.body%type");
    assertThat(schema).contains("recipient_s_id");
    assertThat(schema).contains("sender_p_id");
    assertThat(schema).contains("target_c_id");
    assertThat(schema).contains("target_c_no");
    assertThat(schema).contains("cursor recipient_cursor(");
    assertThat(schema).contains("p_target_c_id in class.c_id%type");
    assertThat(schema).contains("p_target_c_no in class.c_no%type");
    assertThat(schema).contains("p_student_ids_csv in varchar2");
    assertThat(schema).contains("for recipient in recipient_cursor(p_c_id, v_c_no, p_student_ids) loop");
    assertThat(schema).contains("exception");

    assertThat(mapper).doesNotContain("course_section");
    assertThat(mapper).doesNotContain("section_id");
    assertThat(mapper).doesNotContain(" semester ");
    assertThat(mapper).doesNotContain(" enrollment ");
    assertThat(mapper).doesNotContain("recipient_user_id");
    assertThat(mapper).doesNotContain("sender_user_id");
    assertThat(mapper).doesNotContain("target_section_id");

    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("call send_professor_message");
  }

  @Test
  void professorStudentMapperUsesCurrentSchemaAndCallableProcedure() throws IOException {
    String schema = readSchemaSql();
    String mapper =
        Files.readString(PROFESSOR_STUDENT_MAPPER_PATH).toLowerCase().replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace view v_professor_student_class");
    assertThat(schema).contains("create or replace procedure get_professor_student_list");
    assertThat(schema).contains("p_professor_user_id in user_account.user_id%type");
    assertThat(schema).contains("p_summary out sys_refcursor");
    assertThat(schema).contains("p_students out sys_refcursor");
    assertThat(schema).contains("student.s_year%type");
    assertThat(schema).contains("enroll.e_status%type");
    assertThat(schema).contains("row_number() over");

    assertThat(mapper).doesNotContain("course_section");
    assertThat(mapper).doesNotContain(" semester ");
    assertThat(mapper).doesNotContain(" department ");
    assertThat(mapper).doesNotContain(" enrollment ");
    assertThat(mapper).doesNotContain("student_id = e.student_id");
    assertThat(mapper).doesNotContain("s.year_level");
    assertThat(mapper).doesNotContain("dept_name");
    assertThat(mapper).doesNotContain("<select id=\"findstudents\"");
    assertThat(mapper).doesNotContain("<select id=\"findstudentsummary\"");

    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("call get_professor_student_list");
    assertThat(mapper).contains("jdbctype=cursor");
  }

  @Test
  void professorStudentExportMapperUsesCurrentSchemaAndCallableProcedures() throws IOException {
    String schema = readSchemaSql();
    String mapper =
        Files.readString(PROFESSOR_STUDENT_EXPORT_MAPPER_PATH)
            .toLowerCase()
            .replaceAll("\\s+", " ");

    assertThat(schema).contains("create or replace view v_professor_student_export");
    assertThat(schema).contains("create or replace procedure get_professor_export_course");
    assertThat(schema).contains("create or replace procedure get_professor_export_students");
    assertThat(schema).contains("p_professor_user_id in user_account.user_id%type");
    assertThat(schema).contains("p_result out varchar2");
    assertThat(schema).contains("p_course out sys_refcursor");
    assertThat(schema).contains("p_rows out sys_refcursor");
    assertThat(schema).contains("student.s_id%type");
    assertThat(schema).contains("class.c_no%type");
    assertThat(schema).contains("dbms_lob.substr(e.professor_note");

    assertThat(mapper).doesNotContain("course_section");
    assertThat(mapper).doesNotContain(" semester ");
    assertThat(mapper).doesNotContain(" department ");
    assertThat(mapper).doesNotContain(" enrollment ");
    assertThat(mapper).doesNotContain("student_id = e.student_id");
    assertThat(mapper).doesNotContain("s.year_level");
    assertThat(mapper).doesNotContain("dept_name");
    assertThat(mapper).doesNotContain("<select id=\"findcourse\"");
    assertThat(mapper).doesNotContain("<select id=\"findstudents\"");

    assertThat(mapper).contains("statementtype=\"callable\"");
    assertThat(mapper).contains("call get_professor_export_course");
    assertThat(mapper).contains("call get_professor_export_students");
    assertThat(mapper).contains("jdbctype=cursor");
  }

  private String readSchemaSql() throws IOException {
    StringBuilder schema = new StringBuilder();
    try (var files = Files.list(SCHEMA_INIT_DIR)) {
      for (Path path :
          files
              .filter(path -> path.getFileName().toString().endsWith(".sql"))
              .filter(path -> !path.getFileName().toString().equals("99-dummy-data.sql"))
              .sorted()
              .toList()) {
        schema.append('\n').append(Files.readString(path));
      }
    }
    return schema.toString().toLowerCase().replaceAll("\\s+", " ");
  }
}


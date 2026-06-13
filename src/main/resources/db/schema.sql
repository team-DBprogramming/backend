WHENEVER SQLERROR EXIT SQL.SQLCODE;

alter session set container = xepdb1;
alter session set current_schema = backend;

begin
   for object_name in (
      select 'TRG_ENROLL_CLASS_COUNT' name,
             'TRIGGER' type
        from dual
      union all
      select 'AUTHENTICATE_LOGIN',
             'PROCEDURE'
        from dual
      union all
      select 'SAVE_LOGIN_SUCCESS',
             'PROCEDURE'
        from dual
      union all
      select 'REVOKE_REFRESH_TOKEN',
             'PROCEDURE'
        from dual
      union all
      select 'VALIDATE_REFRESH_TOKEN_ACTIVE',
             'PROCEDURE'
        from dual
      union all
      select 'V_AUTH_USER',
             'VIEW'
        from dual
      union all
      select 'GET_PROFESSOR_DASHBOARD_SUMMARY',
             'PROCEDURE'
        from dual
      union all
      select 'GET_PROFESSOR_TODAY_SCHEDULES',
             'PROCEDURE'
        from dual
      union all
      select 'GET_PROFESSOR_ASSIGNED_COURSES',
             'PROCEDURE'
        from dual
      union all
      select 'GET_PROFESSOR_REVIEWS',
             'PROCEDURE'
        from dual
      union all
      select 'GET_PROFESSOR_STUDENT_LIST',
             'PROCEDURE'
        from dual
      union all
      select 'GET_PROFESSOR_EXPORT_COURSE',
             'PROCEDURE'
        from dual
      union all
      select 'GET_PROFESSOR_EXPORT_STUDENTS',
             'PROCEDURE'
        from dual
      union all
      select 'V_PROFESSOR_DASHBOARD_CLASS',
             'VIEW'
        from dual
      union all
      select 'V_PROFESSOR_REVIEW_CLASS',
             'VIEW'
        from dual
      union all
      select 'V_PROFESSOR_STUDENT_CLASS',
             'VIEW'
        from dual
      union all
      select 'V_PROFESSOR_STUDENT_EXPORT',
             'VIEW'
        from dual
      union all
      select 'INSERTENROLLCHECKED',
             'PROCEDURE'
        from dual
      union all
      select 'DELETEENROLLCHECKED',
             'PROCEDURE'
        from dual
      union all
      select 'INSERTENROLL',
             'PROCEDURE'
        from dual
      union all
      select 'DELETEENROLL',
             'PROCEDURE'
        from dual
      union all
      select 'OPENAVAILABLECLASSCURSOR',
              'PROCEDURE'
         from dual
      union all
      select 'PROCESS_COURSE_REQUEST',
             'PROCEDURE'
        from dual
      union all
      select 'SEND_PROFESSOR_MESSAGE',
             'PROCEDURE'
        from dual
      union all
      select 'MARK_NOTIFICATION_AS_READ',
             'PROCEDURE'
        from dual
      union all
      select 'DATE2ENROLLSEMESTER',
             'FUNCTION'
        from dual
      union all
      select 'DATE2ENROLLYEAR',
             'FUNCTION'
        from dual
   ) loop
      begin
         execute immediate 'DROP '
                           || object_name.type
                           || ' '
                           || object_name.name;
      exception
         when others then
            if sqlcode not in ( - 4043,
                                - 4080,
                                - 942 ) then
               raise;
            end if;
      end;
   end loop;
end;
/

begin
   for table_name in (
      select 'NOTIFICATION' name
        from dual
      union all
      select 'REFRESH_TOKEN'
        from dual
      union all
      select 'USER_ACCOUNT'
        from dual
      union all
      select 'REVIEW'
        from dual
      union all
      select 'COURSE_REQUEST'
        from dual
      union all
      select 'CART_ITEM'
        from dual
      union all
      select 'REGISTRATION_PERIOD'
        from dual
      union all
      select 'ENROLL'
        from dual
      union all
      select 'CLASS_TIME'
        from dual
      union all
      select 'CLASS'
        from dual
      union all
      select 'LECTURE'
        from dual
      union all
      select 'PROFESSOR'
        from dual
      union all
      select 'STUDENT'
        from dual
   ) loop
      begin
         execute immediate 'DROP TABLE '
                           || table_name.name
                           || ' CASCADE CONSTRAINTS PURGE';
      exception
         when others then
            if sqlcode != -942 then
               raise;
            end if;
      end;
   end loop;
end;
/

create table student (
   s_id       varchar2(10) not null,
   s_pwd      varchar2(30) not null,
   s_name     varchar2(30) not null,
   s_major    varchar2(30) not null,
   s_addr     varchar2(100),
   s_phone    varchar2(20),
   s_email    varchar2(100),
   s_year     number(1) default 1 not null,
   s_status   varchar2(20) default 'ENROLLED' not null,
   created_at timestamp default current_timestamp,
   constraint pk_student primary key ( s_id ),
   constraint chk_student_year check ( s_year between 1 and 6 ),
   constraint chk_student_status
      check ( s_status in ( 'ENROLLED',
                            'LEAVE',
                            'GRADUATED',
                            'WITHDRAWN' ) )
);

create table professor (
   p_id     varchar2(6) not null,
   p_name   varchar2(10) not null,
   p_major  varchar2(30),
   p_office varchar2(30),
   p_phone  varchar2(20),
   p_pwd    varchar2(30) not null,
   p_email  varchar2(100),
   constraint pk_professor primary key ( p_id ),
   constraint chk_professor_id check ( regexp_like ( p_id,
                                                     '^P[0-9]{5}$' ) )
);

create table user_account (
   user_id       number(19) generated by default as identity not null,
   login_id      varchar2(50) not null,
   password_hash varchar2(255) not null,
   role          varchar2(20) not null,
   email         varchar2(100),
   phone         varchar2(20),
   is_active     number(1) default 1,
   last_login_at timestamp,
   created_at    timestamp default current_timestamp,
   constraint pk_user_account primary key ( user_id ),
   constraint uk_user_account_login_id unique ( login_id ),
   constraint chk_user_account_role check ( role in ( 'STUDENT',
                                                      'PROFESSOR' ) ),
   constraint chk_user_account_active check ( is_active in ( 0,
                                                             1 ) )
);

comment on table user_account is '통합 사용자 계정';
comment on column user_account.login_id is '학번/사번';

create index idx_user_account_role on
   user_account (
      role
   );

create table lecture (
   id               number(2) not null,
   no               varchar2(10) not null,
   subject          varchar2(20) not null,
   prof             varchar2(10) not null,
   credit           number(2) default 3 not null,
   p_id             varchar2(6),
   course_type      varchar2(20) not null,
   course_major     varchar2(30) not null,
   target_year      number(1) not null,
   is_english       number(1) default 0 not null,
   textbook         varchar2(200),
   assignment_count number default 0 not null,
   midterm_rate     number default 30 not null,
   final_rate       number default 30 not null,
   assignment_rate  number default 30 not null,
   attendance_rate  number default 10 not null,
   prerequisite     varchar2(100),
   note             varchar2(500),
   constraint pk_lecture primary key ( id ),
   constraint uk_lecture_no unique ( no ),
   constraint fk_lecture_professor foreign key ( p_id )
      references professor ( p_id ),
   constraint chk_lecture_credit check ( credit between 1 and 6 ),
   constraint chk_lecture_course_type
      check ( course_type in ( '전공필수',
                               '전공선택',
                               '교양필수',
                               '교양선택' ) ),
   constraint chk_lecture_target_year check ( target_year between 1 and 6 ),
   constraint chk_lecture_is_english check ( is_english in ( 0,
                                                             1 ) ),
   constraint chk_lecture_grading_rate
      check ( midterm_rate >= 0
         and final_rate >= 0
         and assignment_rate >= 0
         and attendance_rate >= 0
         and midterm_rate + final_rate + assignment_rate + attendance_rate = 100 )
);

create table class (
   p_id     varchar2(6),
   c_id     varchar2(10),
   c_no     number,
   c_where  varchar2(30),
   c_year   number(4) not null,
   c_semester number(1) not null,
   c_max    number default 30 not null,
   c_now    number default 0 not null,
   c_status varchar2(10) default 'OPEN' not null,
   constraint pk_class primary key ( c_id,
                                     c_no ),
   constraint fk_class_professor foreign key ( p_id )
      references professor ( p_id ),
   constraint fk_class_lecture foreign key ( c_id )
      references lecture ( no ),
   constraint chk_class_capacity
      check ( c_max >= 0
         and c_now >= 0
         and c_now <= c_max ),
   constraint chk_class_semester check ( c_semester in ( 1, 2 ) ),
   constraint chk_class_status
      check ( c_status in ( 'OPEN',
                            'CLOSED',
                            'CANCELLED' ) )
);

create table class_time (
   time_id number generated by default as identity not null,
   p_id    varchar2(6),
   c_id    varchar2(10) not null,
   c_no    number not null,
   c_day   varchar2(3) not null,
   c_start varchar2(5) not null,
   c_end   varchar2(5) not null,
   constraint pk_class_time primary key ( time_id ),
   constraint fk_class_time_class
      foreign key ( c_id,
                    c_no )
         references class ( c_id,
                            c_no )
            on delete cascade,
   constraint fk_class_time_professor foreign key ( p_id )
      references professor ( p_id ),
   constraint chk_class_time_day
      check ( c_day in ( 'MON',
                         'TUE',
                         'WED',
                         'THU',
                         'FRI',
                         'SAT' ) ),
   constraint chk_class_time_start check ( regexp_like ( c_start,
                                                         '^([01][0-9]|2[0-3]):[0-5][0-9]$' ) ),
   constraint chk_class_time_end check ( regexp_like ( c_end,
                                                       '^([01][0-9]|2[0-3]):[0-5][0-9]$' ) ),
   constraint chk_class_time_range check ( c_start < c_end )
);

create table enroll (
   e_id           number generated by default as identity not null,
   s_id           varchar2(10) not null,
   c_id           varchar2(10) not null,
   c_no           number not null,
   e_year         number not null,
   e_semester     number not null,
   e_date         timestamp default current_timestamp not null,
   e_status       varchar2(10) default 'ENROLLED' not null,
   e_drop_date    timestamp,
   professor_note clob,
   constraint pk_enroll primary key ( e_id ),
   constraint fk_enroll_student foreign key ( s_id )
      references student ( s_id ),
   constraint fk_enroll_class
      foreign key ( c_id,
                    c_no )
         references class ( c_id,
                            c_no ),
   constraint uk_enroll_active unique ( s_id,
                                        c_id,
                                        e_year,
                                        e_semester ),
   constraint chk_enroll_semester check ( e_semester in ( 1,
                                                          2 ) ),
   constraint chk_enroll_status
      check ( e_status in ( 'ENROLLED',
                            'DROPPED',
                            'COMPLETED' ) )
);

create index idx_enroll_student_status on
   enroll (
      s_id,
      e_status,
      e_year,
      e_semester
   );
create index idx_class_time_lookup on
   class_time (
      c_id,
      c_no,
      c_day,
      c_start,
      c_end
   );

create table registration_period (
   period_id   number generated by default as identity not null,
   e_year      number not null,
   e_semester  number not null,
   period_type varchar2(10) default 'MAIN' not null,
   start_at    timestamp not null,
   end_at      timestamp not null,
   constraint pk_registration_period primary key ( period_id ),
   constraint chk_registration_period_semester check ( e_semester in ( 1,
                                                                       2 ) ),
   constraint chk_registration_period_type
      check ( period_type in ( 'MAIN',
                               'ADD_DROP',
                               'CANCEL' ) ),
   constraint chk_registration_period_range check ( start_at < end_at )
);

create table cart_item (
   cart_id  number generated by default as identity not null,
   s_id     varchar2(10) not null,
   c_id     varchar2(10) not null,
   c_no     number not null,
   added_at timestamp default current_timestamp,
   constraint pk_cart_item primary key ( cart_id ),
   constraint fk_cart_item_student foreign key ( s_id )
      references student ( s_id )
         on delete cascade,
   constraint fk_cart_item_class
      foreign key ( c_id,
                    c_no )
         references class ( c_id,
                            c_no )
            on delete cascade,
   constraint uk_cart_item unique ( s_id,
                                    c_id,
                                    c_no )
);

create table course_request (
   request_id        number generated by default as identity not null,
   s_id              varchar2(10) not null,
   c_id              varchar2(10) not null,
   c_no              number not null,
   reason            clob not null,
   status            varchar2(20) default 'PENDING' not null,
   requested_at      timestamp default current_timestamp,
   processed_at      timestamp,
   processed_by_p_id varchar2(6),
   constraint pk_course_request primary key ( request_id ),
   constraint fk_course_request_student foreign key ( s_id )
      references student ( s_id )
         on delete cascade,
   constraint fk_course_request_class
      foreign key ( c_id,
                    c_no )
         references class ( c_id,
                            c_no )
            on delete cascade,
   constraint fk_course_request_professor foreign key ( processed_by_p_id )
      references professor ( p_id ),
   constraint uk_course_request unique ( s_id,
                                         c_id,
                                         c_no ),
   constraint chk_course_request_status
      check ( status in ( 'PENDING',
                          'APPROVED',
                          'REJECTED' ) )
);

create table review (
   review_id        number generated by default as identity not null,
   e_id             number not null,
   rating_overall   number(3) not null,
   rating_content   number(3),
   rating_workload  number(3),
   rating_professor number(3),
   difficulty       varchar2(10),
   pros             clob,
   cons             clob,
   advice           clob,
   is_anonymous     number(1) default 1,
   created_at       timestamp default current_timestamp,
   constraint pk_review primary key ( review_id ),
   constraint uk_review_enroll unique ( e_id ),
   constraint fk_review_enroll foreign key ( e_id )
      references enroll ( e_id )
         on delete cascade,
   constraint chk_review_rating_overall check ( rating_overall between 1 and 5 ),
   constraint chk_review_rating_content check ( rating_content between 1 and 5 ),
   constraint chk_review_rating_workload check ( rating_workload between 1 and 5 ),
   constraint chk_review_rating_professor check ( rating_professor between 1 and 5 ),
   constraint chk_review_difficulty
      check ( difficulty in ( 'EASY',
                              'MEDIUM',
                              'HARD' ) ),
   constraint chk_review_anonymous check ( is_anonymous in ( 0,
                                                             1 ) )
);

create table notification (
   notification_id   number generated by default as identity not null,
   recipient_s_id    varchar2(10),
   recipient_p_id    varchar2(6),
   sender_s_id       varchar2(10),
   sender_p_id       varchar2(6),
   target_c_id       varchar2(10),
   target_c_no       number,
   target_request_id number,
   title             varchar2(200) not null,
   body              clob,
   type              varchar2(30) not null,
   is_read           number(1) default 0,
   created_at        timestamp default current_timestamp,
   constraint pk_notification primary key ( notification_id ),
   constraint fk_notification_recipient_student foreign key ( recipient_s_id )
      references student ( s_id )
         on delete cascade,
   constraint fk_notification_recipient_professor foreign key ( recipient_p_id )
      references professor ( p_id )
         on delete cascade,
   constraint fk_notification_sender_student foreign key ( sender_s_id )
      references student ( s_id )
         on delete set null,
   constraint fk_notification_sender_professor foreign key ( sender_p_id )
      references professor ( p_id )
         on delete set null,
   constraint fk_notification_class
      foreign key ( target_c_id,
                    target_c_no )
         references class ( c_id,
                            c_no )
            on delete set null,
   constraint fk_notification_request foreign key ( target_request_id )
      references course_request ( request_id )
         on delete set null,
   constraint chk_notification_recipient
      check ( ( recipient_s_id is not null
         and recipient_p_id is null )
          or ( recipient_s_id is null
         and recipient_p_id is not null ) ),
   constraint chk_notification_type
      check ( type in ( 'COURSE_REQUEST',
                        'COURSE_REQUEST_RESULT',
                        'COURSE_REVIEW',
                        'PROFESSOR_MESSAGE',
                        'SYSTEM' ) ),
   constraint chk_notification_read check ( is_read in ( 0,
                                                         1 ) )
);

create index idx_notification_student_unread on
   notification (
      recipient_s_id,
      is_read,
      created_at
   desc );
create index idx_notification_professor_unread on
   notification (
      recipient_p_id,
      is_read,
      created_at
   desc );

create table refresh_token (
   token_id    number generated by default as identity not null,
   login_id    varchar2(50) not null,
   login_type  varchar2(10) not null,
   token_hash  varchar2(255) not null,
   remember_me number(1) default 0 not null,
   expires_at  timestamp not null,
   revoked_at  timestamp,
   created_at  timestamp default current_timestamp,
   constraint pk_refresh_token primary key ( token_id ),
   constraint uk_refresh_token_hash unique ( token_hash ),
   constraint fk_refresh_token_user_account foreign key ( login_id )
      references user_account ( login_id )
         on delete cascade,
   constraint chk_refresh_login_type check ( login_type in ( 'STUDENT',
                                                             'PROFESSOR' ) ),
   constraint chk_refresh_remember check ( remember_me in ( 0,
                                                            1 ) )
);

create index idx_refresh_token_login_active on
   refresh_token (
      login_id,
      login_type,
      revoked_at,
      expires_at
   );

CREATE OR REPLACE VIEW V_AUTH_USER AS
SELECT
   ua.user_id,
   ua.login_id,
   ua.role,
   LOWER(ua.role) AS role_display,
   ua.phone,
   ua.password_hash,
   ua.is_active,
   COALESCE(s.s_name, p.p_name) AS user_name,
   COALESCE(s.s_major, p.p_major) AS department,
   'u_' || TO_CHAR(ua.user_id) AS public_user_id
FROM user_account ua
LEFT JOIN student s
   ON s.s_id = ua.login_id
  AND ua.role = 'STUDENT'
LEFT JOIN professor p
   ON p.p_id = ua.login_id
  AND ua.role = 'PROFESSOR';
/

CREATE OR REPLACE VIEW V_PROFESSOR_DASHBOARD_CLASS AS
SELECT
   ua.user_id AS professor_user_id,
   p.p_id AS professor_id,
   cls.c_id AS course_id,
   l.subject AS course_name,
   cls.c_no AS division_no,
   LPAD(TO_CHAR(cls.c_no), 2, '0') || '분반' AS division,
   cls.c_year,
   cls.c_semester,
   cls.c_status,
   NVL(cls.c_now, 0) AS student_count,
   NVL(cls.c_max, 0) AS max_students,
   cls.c_where AS room
FROM user_account ua
JOIN professor p
   ON p.p_id = ua.login_id
JOIN class cls
   ON cls.p_id = p.p_id
JOIN lecture l
   ON l.no = cls.c_id
WHERE ua.role = 'PROFESSOR';
/

CREATE OR REPLACE VIEW V_PROFESSOR_REVIEW_CLASS AS
SELECT
   professor_user_id,
   professor_id,
   course_id,
   course_name,
   division_no,
   division,
   c_year,
   c_semester,
   c_status,
   student_count
FROM V_PROFESSOR_DASHBOARD_CLASS;
/

CREATE OR REPLACE VIEW V_PROFESSOR_STUDENT_CLASS AS
SELECT
   professor_user_id,
   professor_id,
   course_id,
   course_name,
   division_no,
   division,
   c_year,
   c_semester,
   c_status,
   student_count
FROM V_PROFESSOR_DASHBOARD_CLASS;
/

CREATE OR REPLACE VIEW V_PROFESSOR_STUDENT_EXPORT AS
SELECT
   dc.professor_user_id,
   dc.professor_id,
   dc.course_id,
   dc.course_name,
   dc.division_no,
   dc.division,
   dc.c_year,
   dc.c_semester,
   dc.c_status,
   s.s_id AS student_id,
   s.s_name AS name,
   s.s_year AS grade,
   s.s_major AS major,
   e.e_status AS status,
   TO_CHAR(e.e_date, 'YYYY-MM-DD HH24:MI') AS enrolled_at,
   DBMS_LOB.SUBSTR(e.professor_note, 4000, 1) AS note
FROM V_PROFESSOR_DASHBOARD_CLASS dc
JOIN enroll e
  ON e.c_id = dc.course_id
 AND e.c_no = dc.division_no
 AND e.e_year = dc.c_year
 AND e.e_semester = dc.c_semester
JOIN student s
  ON s.s_id = e.s_id;
/

CREATE OR REPLACE VIEW V_NOTIFICATION_LIST AS
SELECT
   TO_CHAR(n.notification_id) AS notification_id,
   n.recipient_s_id,
   n.title,
   DBMS_LOB.SUBSTR(n.body, 4000, 1) AS body,
   n.type,
   n.is_read,
   TO_CHAR(n.created_at, 'YYYY-MM-DD HH24:MI') AS created_at,
   n.target_c_id AS target_course_id,
   CASE
      WHEN n.target_c_no IS NULL THEN NULL
      ELSE TO_CHAR(n.target_c_no) || '분반'
   END AS target_division,
   TO_CHAR(n.target_request_id) AS target_request_id,
   n.created_at AS sort_created_at,
   n.notification_id AS sort_notification_id
FROM notification n;
/

CREATE OR REPLACE VIEW V_NOTIFICATION_DETAIL AS
SELECT
   n.notification_id,
   TO_CHAR(n.notification_id) AS notification_id_text,
   n.recipient_s_id,
   n.title,
   DBMS_LOB.SUBSTR(n.body, 4000, 1) AS body,
   n.type,
   n.is_read,
   TO_CHAR(n.created_at, 'YYYY-MM-DD HH24:MI') AS created_at,
   COALESCE(sender_professor.p_name, sender_student.s_name) AS sender_name,
   l.no AS course_id,
   l.subject AS course_name,
   CASE
      WHEN c.c_no IS NULL THEN NULL
      ELSE TO_CHAR(c.c_no) || '분반'
   END AS division,
   n.target_c_id AS target_course_id,
   CASE
      WHEN n.target_c_no IS NULL THEN NULL
      ELSE TO_CHAR(n.target_c_no) || '분반'
   END AS target_division,
   TO_CHAR(n.target_request_id) AS target_request_id,
   DBMS_LOB.SUBSTR(cr.reason, 4000, 1) AS request_reason
FROM notification n
LEFT JOIN student sender_student
   ON sender_student.s_id = n.sender_s_id
LEFT JOIN professor sender_professor
   ON sender_professor.p_id = n.sender_p_id
LEFT JOIN course_request cr
   ON cr.request_id = n.target_request_id
LEFT JOIN class c
   ON c.c_id = COALESCE(n.target_c_id, cr.c_id)
  AND c.c_no = COALESCE(n.target_c_no, cr.c_no)
LEFT JOIN lecture l
   ON l.no = c.c_id;
/

CREATE OR REPLACE PROCEDURE GET_NOTIFICATION_DETAIL(
   p_student_id      IN student.s_id%TYPE,
   p_notification_id IN notification.notification_id%TYPE,
   p_result          OUT SYS_REFCURSOR
)
IS
   v_notification_id notification.notification_id%TYPE;
BEGIN
   SELECT notification_id
     INTO v_notification_id
     FROM V_NOTIFICATION_DETAIL
    WHERE recipient_s_id = p_student_id
      AND notification_id = p_notification_id;

   OPEN p_result FOR
      SELECT
         notification_id_text AS notification_id,
         title,
         body,
         type,
         is_read,
         created_at,
         sender_name,
         course_id,
         course_name,
         division,
         target_course_id,
         target_division,
         target_request_id,
         request_reason
      FROM V_NOTIFICATION_DETAIL
      WHERE recipient_s_id = p_student_id
        AND notification_id = p_notification_id;

EXCEPTION
   WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20041, 'NOTIFICATION_NOT_FOUND');
END;
/

CREATE OR REPLACE PROCEDURE MARK_NOTIFICATION_AS_READ(
   p_student_id      IN student.s_id%TYPE,
   p_notification_id IN notification.notification_id%TYPE
)
IS
BEGIN
   UPDATE notification n
      SET n.is_read = 1
    WHERE n.recipient_s_id = p_student_id
      AND n.notification_id = p_notification_id;

   IF SQL%ROWCOUNT = 0 THEN
      RAISE_APPLICATION_ERROR(-20042, 'NOTIFICATION_NOT_FOUND');
   END IF;
END;
/

CREATE OR REPLACE PROCEDURE AUTHENTICATE_LOGIN(
   p_login_id         IN user_account.login_id%TYPE,
   p_password         IN user_account.password_hash%TYPE,
   p_result           OUT varchar2,
   p_user_id          OUT user_account.user_id%TYPE,
   p_account_login_id OUT user_account.login_id%TYPE,
   p_role             OUT user_account.role%TYPE,
   p_role_display     OUT varchar2,
   p_public_user_id   OUT varchar2,
   p_user_name        OUT varchar2,
   p_department       OUT varchar2
)
IS
   v_user     V_AUTH_USER%ROWTYPE;
   v_password user_account.password_hash%TYPE;
   v_digits   varchar2(30);
BEGIN
   p_result := 'INVALID_CREDENTIALS';
   p_user_id := NULL;
   p_account_login_id := NULL;
   p_role := NULL;
   p_role_display := NULL;
   p_public_user_id := NULL;
   p_user_name := NULL;
   p_department := NULL;

   IF p_login_id IS NULL
      OR TRIM(p_login_id) IS NULL
      OR p_password IS NULL
      OR TRIM(p_password) IS NULL THEN
      p_result := 'MISSING_CREDENTIALS';
      RETURN;
   END IF;

   SELECT *
     INTO v_user
     FROM V_AUTH_USER
    WHERE login_id = TRIM(p_login_id);

   IF NVL(v_user.is_active, 0) != 1 THEN
      p_result := 'INVALID_CREDENTIALS';
      RETURN;
   END IF;

   IF v_user.role NOT IN ( 'STUDENT', 'PROFESSOR' ) THEN
      p_result := 'MISSING_ROLE';
      RETURN;
   END IF;

   v_password := TRIM(p_password);
   v_digits := REGEXP_REPLACE(NVL(v_user.phone, ''), '[^0-9]', '');

   IF NVL(TRIM(v_user.password_hash), '') = v_password
      OR ( LENGTH(v_digits) >= 4
           AND SUBSTR(v_digits, -4) = v_password ) THEN
      p_result := 'LOGIN_SUCCESS';
      p_user_id := v_user.user_id;
      p_account_login_id := v_user.login_id;
      p_role := v_user.role;
      p_role_display := v_user.role_display;
      p_public_user_id := v_user.public_user_id;
      p_user_name := v_user.user_name;
      p_department := v_user.department;
      RETURN;
   END IF;

   p_result := 'INVALID_CREDENTIALS';
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      p_result := 'INVALID_CREDENTIALS';
   WHEN TOO_MANY_ROWS THEN
      p_result := 'INVALID_CREDENTIALS';
   WHEN OTHERS THEN
      p_result := 'AUTH_ERROR';
END;
/

CREATE OR REPLACE PROCEDURE SAVE_LOGIN_SUCCESS(
   p_user_id     IN user_account.user_id%TYPE,
   p_token_hash  IN refresh_token.token_hash%TYPE,
   p_remember_me IN refresh_token.remember_me%TYPE,
   p_expires_at  IN refresh_token.expires_at%TYPE,
   p_result      OUT varchar2
)
IS
   v_user user_account%ROWTYPE;
BEGIN
   p_result := 'SAVE_FAILED';

   SELECT *
     INTO v_user
     FROM user_account
    WHERE user_id = p_user_id
      AND role IN ( 'STUDENT', 'PROFESSOR' )
      AND is_active = 1;

   INSERT INTO refresh_token (
      login_id,
      login_type,
      token_hash,
      remember_me,
      expires_at
   ) VALUES (
      v_user.login_id,
      v_user.role,
      p_token_hash,
      NVL(p_remember_me, 0),
      p_expires_at
   );

   UPDATE user_account
      SET last_login_at = CAST(SYSTIMESTAMP AS TIMESTAMP)
    WHERE user_id = v_user.user_id;

   p_result := 'SAVE_SUCCESS';
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      p_result := 'USER_NOT_FOUND';
   WHEN DUP_VAL_ON_INDEX THEN
      p_result := 'DUPLICATE_TOKEN';
   WHEN OTHERS THEN
      p_result := 'SAVE_FAILED';
END;
/

CREATE OR REPLACE PROCEDURE REVOKE_REFRESH_TOKEN(
   p_token_hash IN refresh_token.token_hash%TYPE,
   p_login_id   IN refresh_token.login_id%TYPE,
   p_login_type IN refresh_token.login_type%TYPE,
   p_revoked_at IN refresh_token.revoked_at%TYPE,
   p_result     OUT varchar2
)
IS
   v_token refresh_token%ROWTYPE;
BEGIN
   p_result := 'INVALID_TOKEN';

   SELECT *
     INTO v_token
     FROM refresh_token
    WHERE token_hash = p_token_hash
      AND login_id = p_login_id
      AND login_type = p_login_type
      AND revoked_at IS NULL
      AND expires_at > p_revoked_at;

   UPDATE refresh_token
      SET revoked_at = p_revoked_at
    WHERE token_id = v_token.token_id
      AND revoked_at IS NULL;

   IF SQL%ROWCOUNT = 1 THEN
      p_result := 'REVOKE_SUCCESS';
   ELSE
      p_result := 'INVALID_TOKEN';
   END IF;
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      p_result := 'INVALID_TOKEN';
   WHEN TOO_MANY_ROWS THEN
      p_result := 'INVALID_TOKEN';
   WHEN OTHERS THEN
      p_result := 'REVOKE_FAILED';
END;
/

CREATE OR REPLACE PROCEDURE VALIDATE_REFRESH_TOKEN_ACTIVE(
   p_token_hash IN refresh_token.token_hash%TYPE,
   p_login_id   IN refresh_token.login_id%TYPE,
   p_login_type IN refresh_token.login_type%TYPE,
   p_checked_at IN refresh_token.expires_at%TYPE,
   p_result     OUT varchar2
)
IS
   v_token refresh_token%ROWTYPE;
BEGIN
   p_result := 'INVALID_TOKEN';

   SELECT *
     INTO v_token
     FROM refresh_token
    WHERE token_hash = p_token_hash
      AND login_id = p_login_id
      AND login_type = p_login_type
      AND revoked_at IS NULL
      AND expires_at > p_checked_at;

   IF v_token.token_id IS NOT NULL THEN
      p_result := 'TOKEN_ACTIVE';
   ELSE
      p_result := 'INVALID_TOKEN';
   END IF;
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      p_result := 'INVALID_TOKEN';
   WHEN TOO_MANY_ROWS THEN
      p_result := 'INVALID_TOKEN';
   WHEN OTHERS THEN
      p_result := 'VALIDATE_FAILED';
END;
/

SET SERVEROUTPUT ON;

CREATE OR REPLACE FUNCTION Date2EnrollYear(p_date IN DATE)
RETURN NUMBER
IS
  v_month NUMBER;
BEGIN
  v_month := TO_NUMBER(TO_CHAR(p_date, 'MM'));

  IF v_month IN (11, 12) THEN
    RETURN TO_NUMBER(TO_CHAR(ADD_MONTHS(p_date, 2), 'YYYY'));
  END IF;

  RETURN TO_NUMBER(TO_CHAR(p_date, 'YYYY'));
END;
/

CREATE OR REPLACE FUNCTION Date2EnrollSemester(p_date IN DATE)
RETURN NUMBER
IS
  v_month NUMBER;
BEGIN
  v_month := TO_NUMBER(TO_CHAR(p_date, 'MM'));

  IF v_month BETWEEN 1 AND 4 THEN
    RETURN 1;
  ELSIF v_month BETWEEN 5 AND 10 THEN
    RETURN 2;
  ELSE
    RETURN 1;
  END IF;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_DASHBOARD_SUMMARY(
   p_professor_user_id IN user_account.user_id%TYPE,
   p_semester          IN VARCHAR2,
   p_course_count      OUT NUMBER,
   p_total_students    OUT NUMBER,
   p_total_capacity    OUT NUMBER,
   p_avg_satisfaction  OUT NUMBER,
   p_new_review_count  OUT NUMBER
)
IS
   v_year         class.c_year%TYPE;
   v_semester     class.c_semester%TYPE;
   v_professor_id professor.p_id%TYPE;
BEGIN
   IF p_semester IS NULL OR TRIM(p_semester) IS NULL THEN
      v_year := Date2EnrollYear(SYSDATE);
      v_semester := Date2EnrollSemester(SYSDATE);
   ELSE
      v_year := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '^[0-9]{4}'));
      v_semester := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '-([12])', 1, 1, NULL, 1));
   END IF;

   SELECT login_id
     INTO v_professor_id
     FROM user_account
    WHERE user_id = p_professor_user_id
      AND role = 'PROFESSOR';

   SELECT
      (
         SELECT COUNT(*)
           FROM V_PROFESSOR_DASHBOARD_CLASS dc
          WHERE dc.professor_user_id = p_professor_user_id
            AND dc.c_status != 'CANCELLED'
            AND dc.c_year = v_year
            AND dc.c_semester = v_semester
      ),
      (
         SELECT NVL(SUM(dc.student_count), 0)
           FROM V_PROFESSOR_DASHBOARD_CLASS dc
          WHERE dc.professor_user_id = p_professor_user_id
            AND dc.c_status != 'CANCELLED'
            AND dc.c_year = v_year
            AND dc.c_semester = v_semester
      ),
      (
         SELECT NVL(SUM(dc.max_students), 0)
           FROM V_PROFESSOR_DASHBOARD_CLASS dc
          WHERE dc.professor_user_id = p_professor_user_id
            AND dc.c_status != 'CANCELLED'
            AND dc.c_year = v_year
            AND dc.c_semester = v_semester
      ),
      (
         SELECT ROUND(AVG(r.rating_overall), 1)
           FROM V_PROFESSOR_DASHBOARD_CLASS dc
           JOIN enroll e
             ON e.c_id = dc.course_id
            AND e.c_no = dc.division_no
            AND e.e_status = 'ENROLLED'
           JOIN review r
             ON r.e_id = e.e_id
          WHERE dc.professor_user_id = p_professor_user_id
            AND dc.c_status != 'CANCELLED'
            AND dc.c_year = v_year
            AND dc.c_semester = v_semester
      ),
      (
         SELECT COUNT(*)
           FROM notification n
           LEFT JOIN class cls
             ON cls.c_id = n.target_c_id
            AND cls.c_no = n.target_c_no
          WHERE n.recipient_p_id = v_professor_id
            AND n.type = 'COURSE_REVIEW'
            AND n.is_read = 0
            AND (
               n.target_c_id IS NULL
               OR (
                  cls.c_year = v_year
                  AND cls.c_semester = v_semester
               )
            )
      )
   INTO
      p_course_count,
      p_total_students,
      p_total_capacity,
      p_avg_satisfaction,
      p_new_review_count
   FROM dual;

EXCEPTION
   WHEN NO_DATA_FOUND THEN
      p_course_count := 0;
      p_total_students := 0;
      p_total_capacity := 0;
      p_avg_satisfaction := NULL;
      p_new_review_count := 0;
   WHEN VALUE_ERROR THEN
      p_course_count := 0;
      p_total_students := 0;
      p_total_capacity := 0;
      p_avg_satisfaction := NULL;
      p_new_review_count := 0;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_TODAY_SCHEDULES(
   p_professor_user_id IN user_account.user_id%TYPE,
   p_semester          IN VARCHAR2,
   p_now               IN TIMESTAMP,
   p_result            OUT SYS_REFCURSOR
)
IS
   v_year      class.c_year%TYPE;
   v_semester  class.c_semester%TYPE;
   v_now       TIMESTAMP;
   v_day       class_time.c_day%TYPE;
   v_time      VARCHAR2(5);
BEGIN
   v_now := COALESCE(p_now, CAST(SYSTIMESTAMP AS TIMESTAMP));

   IF p_semester IS NULL OR TRIM(p_semester) IS NULL THEN
      v_year := Date2EnrollYear(CAST(v_now AS DATE));
      v_semester := Date2EnrollSemester(CAST(v_now AS DATE));
   ELSE
      v_year := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '^[0-9]{4}'));
      v_semester := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '-([12])', 1, 1, NULL, 1));
   END IF;

   v_day := UPPER(TRIM(TO_CHAR(CAST(v_now AS DATE), 'DY', 'NLS_DATE_LANGUAGE=ENGLISH')));
   v_time := TO_CHAR(CAST(v_now AS DATE), 'HH24:MI');

   OPEN p_result FOR
      SELECT
         dc.course_id,
         dc.course_name,
         dc.division,
         dc.student_count,
         ct.c_start AS start_time,
         ct.c_end AS end_time,
         dc.room,
         CASE
            WHEN ct.c_start IS NULL OR ct.c_end IS NULL THEN 'SCHEDULED'
            WHEN v_time >= ct.c_start AND v_time < ct.c_end THEN 'IN_PROGRESS'
            WHEN v_time >= ct.c_end THEN 'COMPLETED'
            ELSE 'SCHEDULED'
         END AS schedule_status
      FROM V_PROFESSOR_DASHBOARD_CLASS dc
      JOIN class_time ct
        ON ct.c_id = dc.course_id
       AND ct.c_no = dc.division_no
      WHERE dc.professor_user_id = p_professor_user_id
        AND dc.c_status != 'CANCELLED'
        AND dc.c_year = v_year
        AND dc.c_semester = v_semester
        AND ct.c_day = v_day
      ORDER BY ct.c_start, dc.course_name, dc.division_no;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_ASSIGNED_COURSES(
   p_professor_user_id IN user_account.user_id%TYPE,
   p_semester          IN VARCHAR2,
   p_result            OUT SYS_REFCURSOR
)
IS
   v_year     class.c_year%TYPE;
   v_semester class.c_semester%TYPE;
BEGIN
   IF p_semester IS NULL OR TRIM(p_semester) IS NULL THEN
      v_year := Date2EnrollYear(SYSDATE);
      v_semester := Date2EnrollSemester(SYSDATE);
   ELSE
      v_year := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '^[0-9]{4}'));
      v_semester := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '-([12])', 1, 1, NULL, 1));
   END IF;

   OPEN p_result FOR
      SELECT
         dc.course_id,
         dc.course_name,
         dc.division,
         dc.student_count,
         dc.max_students,
         ROUND(AVG(r.rating_overall), 1) AS satisfaction
      FROM V_PROFESSOR_DASHBOARD_CLASS dc
      LEFT JOIN enroll e
        ON e.c_id = dc.course_id
       AND e.c_no = dc.division_no
       AND e.e_status = 'ENROLLED'
      LEFT JOIN review r
        ON r.e_id = e.e_id
      WHERE dc.professor_user_id = p_professor_user_id
        AND dc.c_status != 'CANCELLED'
        AND dc.c_year = v_year
        AND dc.c_semester = v_semester
      GROUP BY
         dc.course_id,
         dc.course_name,
         dc.division,
         dc.division_no,
         dc.student_count,
         dc.max_students
      ORDER BY dc.course_name, dc.division_no;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_REVIEWS(
   p_professor_user_id  IN user_account.user_id%TYPE,
   p_course_id          IN class.c_id%TYPE,
   p_division           IN VARCHAR2,
   p_semester           IN VARCHAR2,
   p_sort               IN VARCHAR2,
   p_result             OUT VARCHAR2,
   p_avg_rating         OUT NUMBER,
   p_participation_rate OUT NUMBER,
   p_participant_count  OUT NUMBER,
   p_item_overall       OUT NUMBER,
   p_item_content       OUT NUMBER,
   p_item_workload      OUT NUMBER,
   p_item_kindness      OUT NUMBER,
   p_reviews            OUT SYS_REFCURSOR
)
IS
   v_year              class.c_year%TYPE;
   v_semester          class.c_semester%TYPE;
   v_sort              VARCHAR2(20);
   v_target_count      NUMBER;
   v_total_students    NUMBER;

   PROCEDURE open_empty_reviews IS
   BEGIN
      OPEN p_reviews FOR
         SELECT
            CAST(NULL AS VARCHAR2(40)) AS review_id,
            CAST(NULL AS NUMBER) AS rating,
            CAST(NULL AS VARCHAR2(20)) AS created_at,
            CAST(NULL AS VARCHAR2(4000)) AS pros,
            CAST(NULL AS VARCHAR2(4000)) AS cons,
            CAST(NULL AS VARCHAR2(4000)) AS tip,
            CAST(NULL AS VARCHAR2(40)) AS writer
         FROM dual
         WHERE 1 = 0;
   END;
BEGIN
   p_result := 'SUCCESS';
   p_avg_rating := NULL;
   p_participation_rate := NULL;
   p_participant_count := 0;
   p_item_overall := NULL;
   p_item_content := NULL;
   p_item_workload := NULL;
   p_item_kindness := NULL;

   IF p_division IS NULL OR TRIM(p_division) IS NULL THEN
      p_result := 'DIVISION_REQUIRED';
      open_empty_reviews;
      RETURN;
   END IF;

   IF p_semester IS NULL OR TRIM(p_semester) IS NULL THEN
      v_year := Date2EnrollYear(SYSDATE);
      v_semester := Date2EnrollSemester(SYSDATE);
   ELSE
      v_year := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '^[0-9]{4}'));
      v_semester := TO_NUMBER(REGEXP_SUBSTR(TRIM(p_semester), '-([12])', 1, 1, NULL, 1));
   END IF;

   v_sort := UPPER(TRIM(NVL(p_sort, 'LATEST')));
   IF v_sort NOT IN ( 'LATEST', 'RATING_DESC', 'RATING_ASC' ) THEN
      v_sort := 'LATEST';
   END IF;

   SELECT COUNT(*), NVL(SUM(student_count), 0)
     INTO v_target_count, v_total_students
     FROM V_PROFESSOR_REVIEW_CLASS dc
    WHERE dc.professor_user_id = p_professor_user_id
      AND dc.course_id = p_course_id
      AND dc.c_status != 'CANCELLED'
      AND dc.c_year = v_year
      AND dc.c_semester = v_semester
      AND (
         LPAD(TO_CHAR(dc.division_no), 2, '0') = TRIM(p_division)
         OR TO_CHAR(dc.division_no) = TRIM(p_division)
         OR dc.division = TRIM(p_division)
      );

   IF v_target_count = 0 THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_reviews;
      RETURN;
   END IF;

   SELECT
      ROUND(AVG(r.rating_overall), 1),
      COUNT(r.review_id),
      ROUND(AVG(r.rating_overall), 1),
      ROUND(AVG(r.rating_content), 1),
      ROUND(AVG(r.rating_workload), 1),
      ROUND(AVG(r.rating_professor), 1)
   INTO
      p_avg_rating,
      p_participant_count,
      p_item_overall,
      p_item_content,
      p_item_workload,
      p_item_kindness
   FROM V_PROFESSOR_REVIEW_CLASS dc
   JOIN enroll e
     ON e.c_id = dc.course_id
    AND e.c_no = dc.division_no
    AND e.e_status = 'ENROLLED'
   LEFT JOIN review r
     ON r.e_id = e.e_id
   WHERE dc.professor_user_id = p_professor_user_id
     AND dc.course_id = p_course_id
     AND dc.c_status != 'CANCELLED'
     AND dc.c_year = v_year
     AND dc.c_semester = v_semester
     AND (
        LPAD(TO_CHAR(dc.division_no), 2, '0') = TRIM(p_division)
        OR TO_CHAR(dc.division_no) = TRIM(p_division)
        OR dc.division = TRIM(p_division)
     );

   IF v_total_students > 0 THEN
      p_participation_rate := ROUND(p_participant_count * 100 / v_total_students);
   ELSE
      p_participation_rate := NULL;
   END IF;

   OPEN p_reviews FOR
      SELECT
         TO_CHAR(r.review_id) AS review_id,
         r.rating_overall AS rating,
         TO_CHAR(r.created_at, 'YYYY.MM.DD') AS created_at,
         DBMS_LOB.SUBSTR(r.pros, 4000, 1) AS pros,
         DBMS_LOB.SUBSTR(r.cons, 4000, 1) AS cons,
         DBMS_LOB.SUBSTR(r.advice, 4000, 1) AS tip,
         '익명' AS writer
      FROM V_PROFESSOR_REVIEW_CLASS dc
      JOIN enroll e
        ON e.c_id = dc.course_id
       AND e.c_no = dc.division_no
       AND e.e_status = 'ENROLLED'
      JOIN review r
        ON r.e_id = e.e_id
      WHERE dc.professor_user_id = p_professor_user_id
        AND dc.course_id = p_course_id
        AND dc.c_status != 'CANCELLED'
        AND dc.c_year = v_year
        AND dc.c_semester = v_semester
        AND (
           LPAD(TO_CHAR(dc.division_no), 2, '0') = TRIM(p_division)
           OR TO_CHAR(dc.division_no) = TRIM(p_division)
           OR dc.division = TRIM(p_division)
        )
      ORDER BY
         CASE WHEN v_sort = 'RATING_DESC' THEN r.rating_overall END DESC,
         CASE WHEN v_sort = 'RATING_ASC' THEN r.rating_overall END ASC,
         r.created_at DESC,
         r.review_id DESC;
EXCEPTION
   WHEN VALUE_ERROR THEN
      p_result := 'COURSE_NOT_FOUND';
      p_avg_rating := NULL;
      p_participation_rate := NULL;
      p_participant_count := 0;
      p_item_overall := NULL;
      p_item_content := NULL;
      p_item_workload := NULL;
      p_item_kindness := NULL;
      open_empty_reviews;
   WHEN OTHERS THEN
      p_result := 'COURSE_NOT_FOUND';
      p_avg_rating := NULL;
      p_participation_rate := NULL;
      p_participant_count := 0;
      p_item_overall := NULL;
      p_item_content := NULL;
      p_item_workload := NULL;
      p_item_kindness := NULL;
      open_empty_reviews;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_STUDENT_LIST(
   p_professor_user_id IN user_account.user_id%TYPE,
   p_course_id         IN class.c_id%TYPE,
   p_division          IN VARCHAR2,
   p_keyword           IN VARCHAR2,
   p_grade             IN student.s_year%TYPE,
   p_major             IN student.s_major%TYPE,
   p_page              IN NUMBER,
   p_size              IN NUMBER,
   p_result            OUT VARCHAR2,
   p_summary           OUT SYS_REFCURSOR,
   p_students          OUT SYS_REFCURSOR
)
IS
   v_year         class.c_year%TYPE;
   v_semester     class.c_semester%TYPE;
   v_page         NUMBER;
   v_size         NUMBER;
   v_offset       NUMBER;
   v_target_count NUMBER;
   v_enrolled_status enroll.e_status%TYPE := 'ENROLLED';

   PROCEDURE open_empty_cursors IS
   BEGIN
      OPEN p_summary FOR
         SELECT
            CAST(NULL AS VARCHAR2(100)) AS course_name,
            CAST(NULL AS VARCHAR2(20)) AS course_id,
            CAST(NULL AS VARCHAR2(20)) AS division,
            CAST(NULL AS VARCHAR2(20)) AS semester,
            CAST(NULL AS NUMBER) AS total_students,
            CAST(NULL AS NUMBER) AS request_count
         FROM dual
         WHERE 1 = 0;

      OPEN p_students FOR
         SELECT
            CAST(NULL AS VARCHAR2(20)) AS student_id,
            CAST(NULL AS VARCHAR2(100)) AS name,
            CAST(NULL AS NUMBER) AS grade,
            CAST(NULL AS VARCHAR2(100)) AS major,
            CAST(NULL AS NUMBER) AS is_retake
         FROM dual
         WHERE 1 = 0;
   END;
BEGIN
   p_result := 'SUCCESS';
   v_year := Date2EnrollYear(SYSDATE);
   v_semester := Date2EnrollSemester(SYSDATE);
   v_page := CASE WHEN p_page IS NULL OR p_page <= 0 THEN 1 ELSE p_page END;
   v_size := CASE WHEN p_size IS NULL OR p_size <= 0 THEN 20 ELSE p_size END;
   v_offset := (v_page - 1) * v_size;

   IF p_division IS NULL OR TRIM(p_division) IS NULL THEN
      p_result := 'DIVISION_REQUIRED';
      open_empty_cursors;
      RETURN;
   END IF;

   SELECT COUNT(*)
     INTO v_target_count
     FROM V_PROFESSOR_STUDENT_CLASS dc
    WHERE dc.professor_user_id = p_professor_user_id
      AND dc.course_id = p_course_id
      AND dc.c_status != 'CANCELLED'
      AND (
         LPAD(TO_CHAR(dc.division_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
         OR TO_CHAR(dc.division_no) = TRIM(p_division)
         OR dc.division = TRIM(p_division)
         OR TO_CHAR(dc.division_no) || '분반' = TRIM(p_division)
         OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '분반' = TRIM(p_division)
      );

   IF v_target_count = 0 THEN
      p_result := 'NOT_FOUND';
      open_empty_cursors;
      RETURN;
   END IF;

   OPEN p_summary FOR
      SELECT
         dc.course_name,
         dc.course_id,
         dc.division,
         TO_CHAR(dc.c_year) || '-' || TO_CHAR(dc.c_semester) || '학기' AS semester,
         dc.student_count AS total_students,
         (
            SELECT COUNT(*)
            FROM course_request cr
            WHERE cr.c_id = dc.course_id
              AND cr.c_no = dc.division_no
              AND cr.status = 'PENDING'
         ) AS request_count
      FROM V_PROFESSOR_STUDENT_CLASS dc
      WHERE dc.professor_user_id = p_professor_user_id
        AND dc.course_id = p_course_id
        AND dc.c_status != 'CANCELLED'
        AND (
           LPAD(TO_CHAR(dc.division_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
           OR TO_CHAR(dc.division_no) = TRIM(p_division)
           OR dc.division = TRIM(p_division)
           OR TO_CHAR(dc.division_no) || '분반' = TRIM(p_division)
           OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '분반' = TRIM(p_division)
        );

   OPEN p_students FOR
      SELECT
         student_id,
         name,
         grade,
         major,
         is_retake
      FROM (
         SELECT
            s.s_id AS student_id,
            s.s_name AS name,
            s.s_year AS grade,
            s.s_major AS major,
            CASE
               WHEN EXISTS (
                  SELECT 1
                  FROM enroll prev_e
                  WHERE prev_e.s_id = s.s_id
                    AND prev_e.c_id = dc.course_id
                    AND prev_e.e_status = 'COMPLETED'
                    AND (
                       prev_e.e_year != v_year
                       OR prev_e.e_semester != v_semester
                    )
               ) THEN 1
               ELSE 0
            END AS is_retake,
            ROW_NUMBER() OVER (ORDER BY s.s_id) AS row_num
         FROM V_PROFESSOR_STUDENT_CLASS dc
         JOIN enroll e
           ON e.c_id = dc.course_id
          AND e.c_no = dc.division_no
          AND e.e_year = v_year
          AND e.e_semester = v_semester
          AND e.e_status = v_enrolled_status
         JOIN student s
           ON s.s_id = e.s_id
         WHERE dc.professor_user_id = p_professor_user_id
           AND dc.course_id = p_course_id
           AND dc.c_status != 'CANCELLED'
           AND (
              LPAD(TO_CHAR(dc.division_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
              OR TO_CHAR(dc.division_no) = TRIM(p_division)
              OR dc.division = TRIM(p_division)
              OR TO_CHAR(dc.division_no) || '분반' = TRIM(p_division)
              OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '분반' = TRIM(p_division)
           )
           AND (
              p_keyword IS NULL
              OR TRIM(p_keyword) IS NULL
              OR LOWER(s.s_id) LIKE '%' || LOWER(TRIM(p_keyword)) || '%'
              OR LOWER(s.s_name) LIKE '%' || LOWER(TRIM(p_keyword)) || '%'
           )
           AND (p_grade IS NULL OR s.s_year = p_grade)
           AND (
              p_major IS NULL
              OR TRIM(p_major) IS NULL
              OR LOWER(s.s_major) LIKE '%' || LOWER(TRIM(p_major)) || '%'
           )
      )
      WHERE row_num > v_offset
        AND row_num <= v_offset + v_size
      ORDER BY row_num;

EXCEPTION
   WHEN VALUE_ERROR THEN
      p_result := 'NOT_FOUND';
      open_empty_cursors;
   WHEN OTHERS THEN
      p_result := 'NOT_FOUND';
      open_empty_cursors;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_EXPORT_COURSE(
   p_professor_user_id IN user_account.user_id%TYPE,
   p_course_id         IN class.c_id%TYPE,
   p_division          IN VARCHAR2,
   p_result            OUT VARCHAR2,
   p_course            OUT SYS_REFCURSOR
)
IS
   v_year        class.c_year%TYPE;
   v_semester    class.c_semester%TYPE;
   v_division_no class.c_no%TYPE;
   v_target_count NUMBER;

   PROCEDURE open_empty_course IS
   BEGIN
      OPEN p_course FOR
         SELECT
            CAST(NULL AS VARCHAR2(100)) AS course_name,
            CAST(NULL AS VARCHAR2(20)) AS course_id,
            CAST(NULL AS VARCHAR2(20)) AS division,
            CAST(NULL AS VARCHAR2(20)) AS semester
         FROM dual
         WHERE 1 = 0;
   END;
BEGIN
   p_result := 'SUCCESS';
   v_year := Date2EnrollYear(SYSDATE);
   v_semester := Date2EnrollSemester(SYSDATE);

   IF p_division IS NULL OR TRIM(p_division) IS NULL THEN
      p_result := 'DIVISION_REQUIRED';
      open_empty_course;
      RETURN;
   END IF;

   SELECT COUNT(*), MIN(dc.division_no)
     INTO v_target_count, v_division_no
     FROM V_PROFESSOR_DASHBOARD_CLASS dc
    WHERE dc.professor_user_id = p_professor_user_id
      AND dc.course_id = p_course_id
      AND dc.c_status != 'CANCELLED'
      AND dc.c_year = v_year
      AND dc.c_semester = v_semester
      AND (
         LPAD(TO_CHAR(dc.division_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
         OR TO_CHAR(dc.division_no) = TRIM(p_division)
         OR dc.division = TRIM(p_division)
         OR TO_CHAR(dc.division_no) || '분반' = TRIM(p_division)
         OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '분반' = TRIM(p_division)
         OR TO_CHAR(dc.division_no) || '遺꾨컲' = TRIM(p_division)
         OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '遺꾨컲' = TRIM(p_division)
      );

   IF v_target_count = 0 THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_course;
      RETURN;
   END IF;

   OPEN p_course FOR
      SELECT
         dc.course_name,
         dc.course_id,
         dc.division,
         TO_CHAR(dc.c_year) || '-' || TO_CHAR(dc.c_semester) || '학기' AS semester
      FROM V_PROFESSOR_DASHBOARD_CLASS dc
      WHERE dc.professor_user_id = p_professor_user_id
        AND dc.course_id = p_course_id
        AND dc.division_no = v_division_no
        AND dc.c_status != 'CANCELLED'
        AND dc.c_year = v_year
        AND dc.c_semester = v_semester;
EXCEPTION
   WHEN VALUE_ERROR THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_course;
   WHEN OTHERS THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_course;
END;
/

CREATE OR REPLACE PROCEDURE GET_PROFESSOR_EXPORT_STUDENTS(
   p_professor_user_id IN user_account.user_id%TYPE,
   p_course_id         IN class.c_id%TYPE,
   p_division          IN VARCHAR2,
   p_keyword           IN VARCHAR2,
   p_grade             IN student.s_year%TYPE,
   p_major             IN student.s_major%TYPE,
   p_result            OUT VARCHAR2,
   p_rows              OUT SYS_REFCURSOR
)
IS
   v_year         class.c_year%TYPE;
   v_semester     class.c_semester%TYPE;
   v_division_no  class.c_no%TYPE;
   v_student_id   student.s_id%TYPE;
   v_target_count NUMBER;

   PROCEDURE open_empty_rows IS
   BEGIN
      OPEN p_rows FOR
         SELECT
            CAST(NULL AS VARCHAR2(20)) AS student_id,
            CAST(NULL AS VARCHAR2(100)) AS name,
            CAST(NULL AS NUMBER) AS grade,
            CAST(NULL AS VARCHAR2(100)) AS major,
            CAST(NULL AS VARCHAR2(20)) AS status,
            CAST(NULL AS VARCHAR2(20)) AS enrolled_at,
            CAST(NULL AS VARCHAR2(4000)) AS note
         FROM dual
         WHERE 1 = 0;
   END;
BEGIN
   p_result := 'SUCCESS';
   v_year := Date2EnrollYear(SYSDATE);
   v_semester := Date2EnrollSemester(SYSDATE);
   v_student_id := NULL;

   IF p_division IS NULL OR TRIM(p_division) IS NULL THEN
      p_result := 'DIVISION_REQUIRED';
      open_empty_rows;
      RETURN;
   END IF;

   SELECT COUNT(*), MIN(dc.division_no)
     INTO v_target_count, v_division_no
     FROM V_PROFESSOR_DASHBOARD_CLASS dc
    WHERE dc.professor_user_id = p_professor_user_id
      AND dc.course_id = p_course_id
      AND dc.c_status != 'CANCELLED'
      AND dc.c_year = v_year
      AND dc.c_semester = v_semester
      AND (
         LPAD(TO_CHAR(dc.division_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
         OR TO_CHAR(dc.division_no) = TRIM(p_division)
         OR dc.division = TRIM(p_division)
         OR TO_CHAR(dc.division_no) || '분반' = TRIM(p_division)
         OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '분반' = TRIM(p_division)
         OR TO_CHAR(dc.division_no) || '遺꾨컲' = TRIM(p_division)
         OR LPAD(TO_CHAR(dc.division_no), 2, '0') || '遺꾨컲' = TRIM(p_division)
      );

   IF v_target_count = 0 THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_rows;
      RETURN;
   END IF;

   OPEN p_rows FOR
      SELECT
         se.student_id,
         se.name,
         se.grade,
         se.major,
         se.status,
         se.enrolled_at,
         se.note
      FROM V_PROFESSOR_STUDENT_EXPORT se
      WHERE se.professor_user_id = p_professor_user_id
        AND se.course_id = p_course_id
        AND se.division_no = v_division_no
        AND se.c_status != 'CANCELLED'
        AND se.c_year = v_year
        AND se.c_semester = v_semester
        AND se.status = 'ENROLLED'
        AND (
           p_keyword IS NULL
           OR TRIM(p_keyword) IS NULL
           OR LOWER(se.student_id) LIKE '%' || LOWER(TRIM(p_keyword)) || '%'
           OR LOWER(se.name) LIKE '%' || LOWER(TRIM(p_keyword)) || '%'
        )
        AND (p_grade IS NULL OR se.grade = p_grade)
        AND (
           p_major IS NULL
           OR TRIM(p_major) IS NULL
           OR LOWER(se.major) LIKE '%' || LOWER(TRIM(p_major)) || '%'
        )
      ORDER BY se.student_id;
EXCEPTION
   WHEN VALUE_ERROR THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_rows;
   WHEN OTHERS THEN
      p_result := 'COURSE_NOT_FOUND';
      open_empty_rows;
END;
/

CREATE OR REPLACE PROCEDURE InsertEnroll(
    sStudentId   IN student.s_id%TYPE,
    sCourseId    IN class.c_id%TYPE,
    nCourseIdNo  IN class.c_no%TYPE,
    result       OUT VARCHAR2
)
IS
    too_many_sum_courseunit EXCEPTION;
    duplicate_course        EXCEPTION;
    too_many_students       EXCEPTION;
    duplicate_time          EXCEPTION;

    nYear           NUMBER;
    nSemester       NUMBER;
    nSumCourseUnit  NUMBER;
    nCourseUnit     NUMBER;
    nCnt            NUMBER;
    nTeachMax       NUMBER;
BEGIN
    result := '';

    DBMS_OUTPUT.PUT_LINE('#');
    DBMS_OUTPUT.PUT_LINE(
        sStudentId || '님이 과목번호 '
        || sCourseId || ', 분반 '
        || nCourseIdNo || ' 수강신청 요청'
    );

    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    --------------------------------------------------
    -- 1. 신청하려는 분반 존재 + OPEN 여부 + 신청 과목 학점
    --------------------------------------------------
    SELECT l.credit, c.c_max
    INTO nCourseUnit, nTeachMax
    FROM lecture l
    JOIN class c
      ON c.c_id = l.no
    WHERE c.c_id = sCourseId
      AND c.c_no = nCourseIdNo
      AND c.c_status = 'OPEN';

    --------------------------------------------------
    -- 2. 현재 신청 학점
    --------------------------------------------------
    SELECT NVL(SUM(l.credit), 0)
    INTO nSumCourseUnit
    FROM enroll e
    JOIN lecture l
      ON e.c_id = l.no
    WHERE e.s_id = sStudentId
      AND e.e_year = nYear
      AND e.e_semester = nSemester
      AND e.e_status = 'ENROLLED';

    IF nSumCourseUnit + nCourseUnit > 18 THEN
        RAISE too_many_sum_courseunit;
    END IF;

    --------------------------------------------------
    -- 3. 이미 신청한 과목인지 확인
    --------------------------------------------------
    SELECT COUNT(*)
    INTO nCnt
    FROM enroll
    WHERE s_id = sStudentId
      AND c_id = sCourseId
      AND e_year = nYear
      AND e_semester = nSemester
      AND e_status = 'ENROLLED';

    IF nCnt > 0 THEN
        RAISE duplicate_course;
    END IF;

    --------------------------------------------------
    -- 4. 정원 초과 확인
    -- 트리거가 class.c_now를 관리하므로 class.c_now 기준으로 확인
    --------------------------------------------------
    SELECT COUNT(*)
    INTO nCnt
    FROM class
    WHERE c_id = sCourseId
      AND c_no = nCourseIdNo
      AND c_now >= c_max;

    IF nCnt > 0 THEN
        RAISE too_many_students;
    END IF;

    --------------------------------------------------
    -- 5. 시간 충돌 확인
    --------------------------------------------------
    SELECT COUNT(*)
    INTO nCnt
    FROM class_time ct1
    JOIN class_time ct2
      ON ct1.c_day = ct2.c_day
    JOIN enroll e
      ON e.c_id = ct2.c_id
     AND e.c_no = ct2.c_no
    WHERE e.s_id = sStudentId
      AND e.e_year = nYear
      AND e.e_semester = nSemester
      AND e.e_status = 'ENROLLED'
      AND ct1.c_id = sCourseId
      AND ct1.c_no = nCourseIdNo
      AND ct1.c_start < ct2.c_end
      AND ct1.c_end > ct2.c_start;

    IF nCnt > 0 THEN
        RAISE duplicate_time;
    END IF;

    --------------------------------------------------
    -- 6. 수강신청
    --------------------------------------------------
    INSERT INTO enroll (
        s_id,
        c_id,
        c_no,
        e_year,
        e_semester,
        e_date,
        e_status
    )
    VALUES (
        sStudentId,
        sCourseId,
        nCourseIdNo,
        nYear,
        nSemester,
        SYSTIMESTAMP,
        'ENROLLED'
    );

    result := 'ENROLL_SUCCESS';

EXCEPTION
    WHEN too_many_sum_courseunit THEN
        result := 'MAX_CREDIT_EXCEEDED';

    WHEN duplicate_course THEN
        result := 'DUPLICATE_COURSE';

    WHEN too_many_students THEN
        result := 'CAPACITY_FULL';

    WHEN duplicate_time THEN
        result := 'TIME_CONFLICT';

    WHEN NO_DATA_FOUND THEN
        result := 'COURSE_NOT_FOUND';

    WHEN DUP_VAL_ON_INDEX THEN
        result := 'DUPLICATE_COURSE';

    WHEN OTHERS THEN
        result := 'SQLCODE=' || SQLCODE;
END;
/

CREATE OR REPLACE PROCEDURE CancelEnroll(
    sStudentId   IN student.s_id%TYPE,
    sCourseId    IN class.c_id%TYPE,
    nCourseIdNo  IN class.c_no%TYPE,
    result       OUT VARCHAR2
)
IS
    nYear      NUMBER;
    nSemester  NUMBER;
    nCnt       NUMBER;
BEGIN
    result := '';

    DBMS_OUTPUT.PUT_LINE('#');
    DBMS_OUTPUT.PUT_LINE(
        sStudentId || '님이 과목번호 '
        || sCourseId || ', 분반 '
        || NVL(TO_CHAR(nCourseIdNo), '전체') || '의 수강 해제를 요청하였습니다.'
    );

    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    --------------------------------------------------
    -- 수강 중인지 확인
    --------------------------------------------------
    SELECT COUNT(*)
    INTO nCnt
    FROM enroll
    WHERE s_id = sStudentId
      AND c_id = sCourseId
      AND (nCourseIdNo IS NULL OR c_no = nCourseIdNo)
      AND e_year = nYear
      AND e_semester = nSemester
      AND e_status = 'ENROLLED';

    IF nCnt = 0 THEN
        result := 'ENROLL_NOT_FOUND';
        RETURN;
    END IF;

    --------------------------------------------------
    -- 수강 취소
    --------------------------------------------------
    UPDATE enroll
    SET e_status = 'DROPPED',
        e_drop_date = SYSTIMESTAMP
    WHERE s_id = sStudentId
      AND c_id = sCourseId
      AND (nCourseIdNo IS NULL OR c_no = nCourseIdNo)
      AND e_year = nYear
      AND e_semester = nSemester
      AND e_status = 'ENROLLED';

    result := 'DELETE_SUCCESS';

EXCEPTION
    WHEN OTHERS THEN
        result := 'SQLCODE=' || SQLCODE;
END;
/

CREATE OR REPLACE TRIGGER TRG_ENROLL_CLASS_COUNT
AFTER INSERT OR UPDATE OR DELETE
ON enroll
FOR EACH ROW
BEGIN
  IF INSERTING THEN
    IF :NEW.e_status = 'ENROLLED' THEN
      UPDATE class
      SET c_now = c_now + 1
      WHERE c_id = :NEW.c_id
        AND c_no = :NEW.c_no;
    END IF;

  ELSIF UPDATING THEN
    IF :OLD.e_status = 'ENROLLED'
       AND :NEW.e_status <> 'ENROLLED' THEN
      UPDATE class
      SET c_now = GREATEST(0, c_now - 1)
      WHERE c_id = :OLD.c_id
        AND c_no = :OLD.c_no;

    ELSIF :OLD.e_status <> 'ENROLLED'
          AND :NEW.e_status = 'ENROLLED' THEN
      UPDATE class
      SET c_now = c_now + 1
      WHERE c_id = :NEW.c_id
        AND c_no = :NEW.c_no;
    END IF;

  ELSIF DELETING THEN
    IF :OLD.e_status = 'ENROLLED' THEN
      UPDATE class
      SET c_now = GREATEST(0, c_now - 1)
      WHERE c_id = :OLD.c_id
        AND c_no = :OLD.c_no;
    END IF;
  END IF;
END;
/

CREATE OR REPLACE PROCEDURE GET_ENROLL_STATUS(
    p_status OUT VARCHAR2,
    p_start_at OUT TIMESTAMP,
    p_end_at OUT TIMESTAMP,
    p_days_left OUT NUMBER,
    p_remaining_seconds OUT NUMBER,
    p_deadline OUT TIMESTAMP
)
IS
    nYear      NUMBER;
    nSemester  NUMBER;
BEGIN
    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    BEGIN
        SELECT
            CASE
                WHEN SYSTIMESTAMP BETWEEN start_at AND end_at THEN 'IN_PROGRESS'
                WHEN SYSTIMESTAMP < start_at THEN 'NOT_STARTED'
                ELSE 'CLOSED'
            END AS status,
            start_at,
            end_at
        INTO
            p_status,
            p_start_at,
            p_end_at
        FROM registration_period
        WHERE e_year = nYear
          AND e_semester = nSemester
          AND period_type IN ('MAIN', 'ADD_DROP')
        ORDER BY
            CASE
                WHEN SYSTIMESTAMP BETWEEN start_at AND end_at THEN 1
                WHEN SYSTIMESTAMP < start_at THEN 2
                ELSE 3
            END,
            start_at
        FETCH FIRST 1 ROW ONLY;

        IF p_status = 'IN_PROGRESS' THEN
            p_days_left :=
                GREATEST(
                    0,
                    TRUNC(CAST(p_end_at AS DATE) - CAST(SYSTIMESTAMP AS DATE))
                );

            p_remaining_seconds :=
                GREATEST(
                    0,
                    ROUND((CAST(p_end_at AS DATE) - CAST(SYSTIMESTAMP AS DATE)) * 24 * 60 * 60)
                );

            p_deadline := p_end_at;
        ELSE
            p_days_left := 0;
            p_remaining_seconds := 0;
            p_deadline := p_end_at;
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_status := 'CLOSED';
            p_start_at := NULL;
            p_end_at := NULL;
            p_days_left := 0;
            p_remaining_seconds := 0;
            p_deadline := NULL;
    END;
END;
/

CREATE OR REPLACE PROCEDURE GET_CREDIT_SUMMARY(
    sStudentId      IN student.s_id%TYPE,
    nAppliedCredit  OUT NUMBER,
    nMax            OUT NUMBER,
    nCourseCnt      OUT NUMBER,
    nCartCount      OUT NUMBER
)
IS
    nYear      NUMBER;
    nSemester  NUMBER;
BEGIN
    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);
    nMax := 18;

    SELECT NVL(SUM(l.credit), 0), COUNT(*)
    INTO nAppliedCredit, nCourseCnt
    FROM enroll e
    JOIN lecture l
      ON e.c_id = l.no
    WHERE e.s_id = sStudentId
      AND e.e_year = nYear
      AND e.e_semester = nSemester
      AND e.e_status = 'ENROLLED';

    SELECT COUNT(*)
    INTO nCartCount
    FROM cart_item
    WHERE s_id = sStudentId;
END;
/

CREATE OR REPLACE PROCEDURE GET_ENROLLED_COURSES(
    sStudentId IN student.s_id%TYPE,
    p_result   OUT SYS_REFCURSOR
)
IS
    nYear      NUMBER;
    nSemester  NUMBER;
BEGIN
    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    OPEN p_result FOR
        SELECT
            e.c_id AS course_id,
            l.subject AS course_name,
            l.credit AS credit,
            LISTAGG(
                ct.c_day || ' ' || ct.c_start || '-' || ct.c_end,
                ', '
            ) WITHIN GROUP (
                ORDER BY
                    CASE ct.c_day
                        WHEN 'MON' THEN 1
                        WHEN 'TUE' THEN 2
                        WHEN 'WED' THEN 3
                        WHEN 'THU' THEN 4
                        WHEN 'FRI' THEN 5
                        WHEN 'SAT' THEN 6
                        ELSE 7
                    END,
                    ct.c_start
            ) AS schedule,
            TO_CHAR(e.e_date, 'YYYY-MM-DD HH24:MI:SS') AS registered_at,
            l.prof AS professor,
            c.c_where AS room
        FROM enroll e
        JOIN lecture l
          ON l.no = e.c_id
        JOIN class c
          ON c.c_id = e.c_id
         AND c.c_no = e.c_no
        JOIN class_time ct
          ON ct.c_id = c.c_id
         AND ct.c_no = c.c_no
        WHERE e.s_id = sStudentId
          AND e.e_year = nYear
          AND e.e_semester = nSemester
          AND e.e_status = 'ENROLLED'
        GROUP BY
            e.c_id,
            l.subject,
            l.credit,
            e.e_date,
            l.prof,
            c.c_where
        ORDER BY e.e_date;
END;
/

CREATE OR REPLACE PROCEDURE GET_ENROLLMENT_TIMETABLE(
    sStudentId IN student.s_id%TYPE,
    p_result   OUT SYS_REFCURSOR
)
IS
    nYear      NUMBER;
    nSemester  NUMBER;
BEGIN
    nYear := Date2EnrollYear(SYSDATE);
    nSemester := Date2EnrollSemester(SYSDATE);

    OPEN p_result FOR
        SELECT
            e.c_id AS course_id,
            l.subject AS course_name,
            l.course_type AS course_type,
            l.credit AS credit,
            l.prof AS professor,
            TO_CHAR(c.c_no) || '분반' AS division,
            ct.c_day AS day_of_week,
            ct.c_start AS start_time,
            ct.c_end AS end_time,
            c.c_where AS room
        FROM enroll e
        JOIN lecture l
          ON l.no = e.c_id
        JOIN class c
          ON c.c_id = e.c_id
         AND c.c_no = e.c_no
        JOIN class_time ct
          ON ct.c_id = c.c_id
         AND ct.c_no = c.c_no
        WHERE e.s_id = sStudentId
          AND e.e_year = nYear
          AND e.e_semester = nSemester
          AND e.e_status = 'ENROLLED'
        ORDER BY
            CASE ct.c_day
                WHEN 'MON' THEN 1
                WHEN 'TUE' THEN 2
                WHEN 'WED' THEN 3
                WHEN 'THU' THEN 4
                WHEN 'FRI' THEN 5
                WHEN 'SAT' THEN 6
                ELSE 7
            END,
            ct.c_start,
            e.c_id;
END;
/


------------------------------------------------------------
-- 이하 추가 구현
-- GET_COURSE_LIST / GET_COURSE_DETAIL / GET_CART_LIST /
-- GET_REVIEW_LIST / GET_DASHBOARD 는 Spring Boot + MyBatis에서 구현
------------------------------------------------------------

------------------------------------------------------------
-- 일괄 수강신청 결과 임시 저장 테이블
-- 최초 1회만 생성되며, 같은 세션에서 프로시저 호출마다 비워서 사용
------------------------------------------------------------
BEGIN
    EXECUTE IMMEDIATE '
        CREATE GLOBAL TEMPORARY TABLE bulk_enroll_result_temp (
            cart_item_id NUMBER NOT NULL,
            success      NUMBER(1) NOT NULL,
            message      VARCHAR2(200),
            code         VARCHAR2(100)
        )
        ON COMMIT DELETE ROWS
    ';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN
            RAISE;
        END IF;
END;
/

------------------------------------------------------------
-- 장바구니 담기
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE INSERT_CART(
    p_s_id   IN cart_item.s_id%TYPE,
    p_c_id   IN cart_item.c_id%TYPE,
    p_c_no   IN cart_item.c_no%TYPE,
    p_result OUT VARCHAR2
)
IS
BEGIN
    INSERT INTO cart_item (
        s_id,
        c_id,
        c_no,
        added_at
    )
    VALUES (
        p_s_id,
        p_c_id,
        p_c_no,
        SYSTIMESTAMP
    );

    p_result := 'SUCCESS';

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        p_result := 'ALREADY_EXISTS';

    WHEN OTHERS THEN
        IF SQLCODE = -2291 THEN
            p_result := 'CLASS_NOT_FOUND';
        ELSE
            p_result := 'SQLCODE=' || SQLCODE;
        END IF;
END;
/

------------------------------------------------------------
-- 장바구니 ID 기준 단건 삭제
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE DELETE_CART(
    p_cart_id IN cart_item.cart_id%TYPE,
    p_s_id    IN cart_item.s_id%TYPE,
    p_result  OUT VARCHAR2
)
IS
BEGIN
    DELETE FROM cart_item
    WHERE cart_id = p_cart_id
      AND s_id = p_s_id;

    IF SQL%ROWCOUNT = 0 THEN
        p_result := 'NOT_FOUND';
    ELSE
        p_result := 'SUCCESS';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        p_result := 'SQLCODE=' || SQLCODE;
END;
/

------------------------------------------------------------
-- 교수 수강 요청 승인/거절 처리
-- 상태 정규화, 예외 분기, 요청 갱신, 결과 알림 생성을 PL/SQL에서 처리
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE PROCESS_COURSE_REQUEST(
    p_professor_user_id IN user_account.user_id%TYPE,
    p_c_id              IN class.c_id%TYPE,
    p_division          IN VARCHAR2,
    p_request_id        IN VARCHAR2,
    p_status            IN VARCHAR2,
    p_result            OUT VARCHAR2,
    p_out_request_id    OUT VARCHAR2,
    p_out_status        OUT course_request.status%TYPE,
    p_out_updated_at    OUT TIMESTAMP
)
IS
    v_professor_id professor.p_id%TYPE;
    v_request_pk   course_request.request_id%TYPE;
    v_student_id   student.s_id%TYPE;
    v_c_no         class.c_no%TYPE;
    v_course_name  lecture.subject%TYPE;
    v_old_status   course_request.status%TYPE;
    v_new_status   course_request.status%TYPE;
    v_updated_at   TIMESTAMP;
BEGIN
    p_result := NULL;
    p_out_request_id := NULL;
    p_out_status := NULL;
    p_out_updated_at := NULL;

    IF p_division IS NULL OR TRIM(p_division) IS NULL THEN
        p_result := 'DIVISION_REQUIRED';
        RETURN;
    END IF;

    IF p_status IS NULL OR TRIM(p_status) IS NULL THEN
        p_result := 'INVALID_STATUS';
        RETURN;
    END IF;

    v_new_status := UPPER(TRIM(p_status));

    IF v_new_status NOT IN ('APPROVED', 'REJECTED') THEN
        p_result := 'INVALID_STATUS';
        RETURN;
    END IF;

    SELECT p.p_id
    INTO v_professor_id
    FROM user_account ua
    JOIN professor p
      ON p.p_id = ua.login_id
    WHERE ua.user_id = p_professor_user_id
      AND ua.role = 'PROFESSOR';

    SELECT
        cr.request_id,
        cr.s_id,
        cr.status,
        cls.c_no,
        l.subject
    INTO
        v_request_pk,
        v_student_id,
        v_old_status,
        v_c_no,
        v_course_name
    FROM course_request cr
    JOIN class cls
      ON cls.c_id = cr.c_id
     AND cls.c_no = cr.c_no
    JOIN lecture l
      ON l.no = cls.c_id
    WHERE TO_CHAR(cr.request_id) = TRIM(p_request_id)
      AND cr.c_id = p_c_id
      AND cls.p_id = v_professor_id
      AND cls.c_status != 'CANCELLED'
      AND cls.c_year = Date2EnrollYear(SYSDATE)
      AND cls.c_semester = Date2EnrollSemester(SYSDATE)
      AND (
          LPAD(TO_CHAR(cls.c_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
          OR TO_CHAR(cls.c_no) || '분반' = TRIM(p_division)
          OR LPAD(TO_CHAR(cls.c_no), 2, '0') || '분반' = TRIM(p_division)
      )
    FOR UPDATE OF cr.status;

    IF v_old_status != 'PENDING' THEN
        p_result := 'ALREADY_PROCESSED';
        RETURN;
    END IF;

    v_updated_at := CAST(SYSTIMESTAMP AS TIMESTAMP);

    UPDATE course_request
    SET status = v_new_status,
        processed_at = v_updated_at,
        processed_by_p_id = v_professor_id
    WHERE request_id = v_request_pk
      AND status = 'PENDING';

    IF SQL%ROWCOUNT = 0 THEN
        p_result := 'ALREADY_PROCESSED';
        RETURN;
    END IF;

    INSERT INTO notification (
        recipient_s_id,
        sender_p_id,
        target_c_id,
        target_c_no,
        target_request_id,
        title,
        body,
        type,
        is_read,
        created_at
    )
    VALUES (
        v_student_id,
        v_professor_id,
        p_c_id,
        v_c_no,
        v_request_pk,
        v_course_name || ' 수강 요청 결과',
        v_course_name || ' 수강 요청이 '
            || CASE v_new_status
                WHEN 'APPROVED' THEN '승인'
                ELSE '거절'
               END
            || '되었습니다.',
        'COURSE_REQUEST_RESULT',
        0,
        v_updated_at
    );

    p_result := 'SUCCESS';
    p_out_request_id := TO_CHAR(v_request_pk);
    p_out_status := v_new_status;
    p_out_updated_at := v_updated_at;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_result := 'NOT_FOUND';

    WHEN OTHERS THEN
        p_result := 'SQLCODE=' || SQLCODE;
END;
/

------------------------------------------------------------
-- 학생 + 과목 + 분반 기준 장바구니 삭제
------------------------------------------------------------
------------------------------------------------------------
-- 교수 메시지 전송
-- 수신자 검증, 중복 제거, 반복 알림 생성을 PL/SQL에서 처리
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SEND_PROFESSOR_MESSAGE(
    p_professor_user_id IN user_account.user_id%TYPE,
    p_c_id              IN class.c_id%TYPE,
    p_division          IN VARCHAR2,
    p_student_ids       IN VARCHAR2,
    p_message           IN notification.body%TYPE,
    p_result            OUT VARCHAR2,
    p_sent_count        OUT NUMBER
)
IS
    v_professor_id    professor.p_id%TYPE;
    v_c_no            class.c_no%TYPE;
    v_requested_count NUMBER := 0;
    v_valid_count     NUMBER := 0;
    v_created_at      TIMESTAMP := CAST(SYSTIMESTAMP AS TIMESTAMP);

    CURSOR recipient_cursor IS
        WITH requested_students AS (
            SELECT DISTINCT TRIM(REGEXP_SUBSTR(p_student_ids, '[^,]+', 1, LEVEL)) AS s_id
            FROM dual
            CONNECT BY REGEXP_SUBSTR(p_student_ids, '[^,]+', 1, LEVEL) IS NOT NULL
        )
        SELECT s.s_id
        FROM requested_students rs
        JOIN student s
          ON s.s_id = rs.s_id
        JOIN enroll e
          ON e.s_id = s.s_id
         AND e.c_id = p_c_id
         AND e.c_no = v_c_no
         AND e.e_status = 'ENROLLED'
         AND e.e_year = Date2EnrollYear(SYSDATE)
         AND e.e_semester = Date2EnrollSemester(SYSDATE)
        WHERE rs.s_id IS NOT NULL;
BEGIN
    p_result := NULL;
    p_sent_count := 0;

    IF p_c_id IS NULL
        OR p_division IS NULL
        OR TRIM(p_division) IS NULL
        OR p_student_ids IS NULL
        OR TRIM(p_student_ids) IS NULL
        OR p_message IS NULL THEN
        p_result := 'INVALID_REQUEST';
        RETURN;
    END IF;

    SELECT p.p_id, cls.c_no
    INTO v_professor_id, v_c_no
    FROM user_account ua
    JOIN professor p
      ON p.p_id = ua.login_id
    JOIN class cls
      ON cls.p_id = p.p_id
    WHERE ua.user_id = p_professor_user_id
      AND ua.role = 'PROFESSOR'
      AND cls.c_id = p_c_id
      AND cls.c_status != 'CANCELLED'
      AND cls.c_year = Date2EnrollYear(SYSDATE)
      AND cls.c_semester = Date2EnrollSemester(SYSDATE)
      AND (
          LPAD(TO_CHAR(cls.c_no), 2, '0') = LPAD(REGEXP_SUBSTR(TRIM(p_division), '^[0-9]+'), 2, '0')
          OR TO_CHAR(cls.c_no) || '분반' = TRIM(p_division)
          OR LPAD(TO_CHAR(cls.c_no), 2, '0') || '분반' = TRIM(p_division)
          OR TO_CHAR(cls.c_no) || '遺꾨컲' = TRIM(p_division)
          OR LPAD(TO_CHAR(cls.c_no), 2, '0') || '遺꾨컲' = TRIM(p_division)
      );

    WITH requested_students AS (
        SELECT DISTINCT TRIM(REGEXP_SUBSTR(p_student_ids, '[^,]+', 1, LEVEL)) AS s_id
        FROM dual
        CONNECT BY REGEXP_SUBSTR(p_student_ids, '[^,]+', 1, LEVEL) IS NOT NULL
    )
    SELECT COUNT(*)
    INTO v_requested_count
    FROM requested_students
    WHERE s_id IS NOT NULL;

    IF v_requested_count = 0 THEN
        p_result := 'INVALID_REQUEST';
        RETURN;
    END IF;

    WITH requested_students AS (
        SELECT DISTINCT TRIM(REGEXP_SUBSTR(p_student_ids, '[^,]+', 1, LEVEL)) AS s_id
        FROM dual
        CONNECT BY REGEXP_SUBSTR(p_student_ids, '[^,]+', 1, LEVEL) IS NOT NULL
    )
    SELECT COUNT(*)
    INTO v_valid_count
    FROM requested_students rs
    JOIN enroll e
      ON e.s_id = rs.s_id
     AND e.c_id = p_c_id
     AND e.c_no = v_c_no
     AND e.e_status = 'ENROLLED'
     AND e.e_year = Date2EnrollYear(SYSDATE)
     AND e.e_semester = Date2EnrollSemester(SYSDATE)
    WHERE rs.s_id IS NOT NULL;

    IF v_valid_count != v_requested_count THEN
        p_result := 'INVALID_RECIPIENT';
        RETURN;
    END IF;

    FOR recipient IN recipient_cursor LOOP
        INSERT INTO notification (
            recipient_s_id,
            sender_p_id,
            target_c_id,
            target_c_no,
            title,
            body,
            type,
            is_read,
            created_at
        )
        VALUES (
            recipient.s_id,
            v_professor_id,
            p_c_id,
            v_c_no,
            '교수님으로부터 메시지가 도착했습니다.',
            p_message,
            'PROFESSOR_MESSAGE',
            0,
            v_created_at
        );

        p_sent_count := p_sent_count + 1;
    END LOOP;

    p_result := 'SUCCESS';

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_result := 'COURSE_NOT_FOUND';
        p_sent_count := 0;

    WHEN OTHERS THEN
        p_result := 'SQLCODE=' || SQLCODE;
        p_sent_count := 0;
END;
/

CREATE OR REPLACE PROCEDURE DELETE_CART_BY_SECTION(
    p_s_id   IN cart_item.s_id%TYPE,
    p_c_id   IN cart_item.c_id%TYPE,
    p_c_no   IN cart_item.c_no%TYPE,
    p_result OUT VARCHAR2
)
IS
BEGIN
    DELETE FROM cart_item
    WHERE s_id = p_s_id
      AND c_id = p_c_id
      AND c_no = p_c_no;

    IF SQL%ROWCOUNT = 0 THEN
        p_result := 'NOT_FOUND';
    ELSE
        p_result := 'SUCCESS';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        p_result := 'SQLCODE=' || SQLCODE;
END;
/

------------------------------------------------------------
-- 장바구니 개수
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE COUNT_CART(
    p_s_id  IN cart_item.s_id%TYPE,
    p_count OUT NUMBER
)
IS
BEGIN
    SELECT COUNT(*)
    INTO p_count
    FROM cart_item
    WHERE s_id = p_s_id;
END;
/

------------------------------------------------------------
-- 장바구니 선택 항목 일괄 수강신청
-- p_cart_ids_csv 예: '1,3,5'
-- 성공 항목은 장바구니에서 삭제, 실패 항목은 유지
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE ENROLL_FROM_CART(
    p_s_id         IN cart_item.s_id%TYPE,
    p_cart_ids_csv IN VARCHAR2,
    p_results      OUT SYS_REFCURSOR,
    p_summary      OUT SYS_REFCURSOR
)
IS
    v_enroll_result VARCHAR2(100);
    v_message       VARCHAR2(200);
    v_success       NUMBER(1);
BEGIN
    DELETE FROM bulk_enroll_result_temp;

    FOR cart_rec IN (
        SELECT
            ci.cart_id,
            ci.c_id,
            ci.c_no
        FROM cart_item ci
        WHERE ci.s_id = p_s_id
          AND ci.cart_id IN (
              SELECT TO_NUMBER(TRIM(REGEXP_SUBSTR(
                         p_cart_ids_csv,
                         '[^,]+',
                         1,
                         LEVEL
                     )))
              FROM dual
              CONNECT BY REGEXP_SUBSTR(
                             p_cart_ids_csv,
                             '[^,]+',
                             1,
                             LEVEL
                         ) IS NOT NULL
          )
        ORDER BY ci.cart_id
    )
    LOOP
        InsertEnroll(
            p_s_id,
            cart_rec.c_id,
            cart_rec.c_no,
            v_enroll_result
        );

        IF v_enroll_result = 'ENROLL_SUCCESS' THEN
            v_success := 1;
            v_message := '수강신청에 성공했습니다.';

            DELETE FROM cart_item
            WHERE cart_id = cart_rec.cart_id
              AND s_id = p_s_id;
        ELSE
            v_success := 0;
            v_message :=
                CASE v_enroll_result
                    WHEN 'MAX_CREDIT_EXCEEDED'
                        THEN '최대 신청 학점을 초과합니다.'
                    WHEN 'DUPLICATE_COURSE'
                        THEN '이미 신청한 과목입니다.'
                    WHEN 'CAPACITY_FULL'
                        THEN '강의 정원이 가득 찼습니다.'
                    WHEN 'TIME_CONFLICT'
                        THEN '기존 수업과 시간이 겹칩니다.'
                    WHEN 'COURSE_NOT_FOUND'
                        THEN '강의 또는 분반을 찾을 수 없습니다.'
                    WHEN 'CLASS_NOT_FOUND'
                        THEN '강의 또는 분반을 찾을 수 없습니다.'
                    WHEN 'REGISTRATION_CLOSED'
                        THEN '현재 수강신청 기간이 아닙니다.'
                    ELSE '수강신청 처리 중 오류가 발생했습니다.'
                END;
        END IF;

        INSERT INTO bulk_enroll_result_temp (
            cart_item_id,
            success,
            message,
            code
        )
        VALUES (
            cart_rec.cart_id,
            v_success,
            v_message,
            v_enroll_result
        );
    END LOOP;

    -- 요청 ID가 존재하지 않거나 다른 학생 소유인 경우
    FOR requested_id IN (
        SELECT TO_NUMBER(TRIM(REGEXP_SUBSTR(
                   p_cart_ids_csv,
                   '[^,]+',
                   1,
                   LEVEL
               ))) AS cart_id
        FROM dual
        CONNECT BY REGEXP_SUBSTR(
                       p_cart_ids_csv,
                       '[^,]+',
                       1,
                       LEVEL
                   ) IS NOT NULL
    )
    LOOP
        INSERT INTO bulk_enroll_result_temp (
            cart_item_id,
            success,
            message,
            code
        )
        SELECT
            requested_id.cart_id,
            0,
            '장바구니 항목을 찾을 수 없습니다.',
            'CART_ITEM_NOT_FOUND'
        FROM dual
        WHERE NOT EXISTS (
            SELECT 1
            FROM bulk_enroll_result_temp br
            WHERE br.cart_item_id = requested_id.cart_id
        );
    END LOOP;

    OPEN p_results FOR
        SELECT
            TO_CHAR(cart_item_id) AS cart_item_id,
            success,
            message,
            code
        FROM bulk_enroll_result_temp
        ORDER BY cart_item_id;

    OPEN p_summary FOR
        SELECT
            COUNT(*) AS total,
            NVL(SUM(CASE WHEN success = 1 THEN 1 ELSE 0 END), 0) AS success,
            NVL(SUM(CASE WHEN success = 0 THEN 1 ELSE 0 END), 0) AS failed
        FROM bulk_enroll_result_temp;

EXCEPTION
    WHEN OTHERS THEN
        DELETE FROM bulk_enroll_result_temp;

        INSERT INTO bulk_enroll_result_temp (
            cart_item_id,
            success,
            message,
            code
        )
        VALUES (
            -1,
            0,
            '일괄 수강신청 처리 중 오류가 발생했습니다.',
            'SQLCODE=' || SQLCODE
        );

        OPEN p_results FOR
            SELECT
                TO_CHAR(cart_item_id) AS cart_item_id,
                success,
                message,
                code
            FROM bulk_enroll_result_temp;

        OPEN p_summary FOR
            SELECT
                1 AS total,
                0 AS success,
                1 AS failed
            FROM dual;
END;
/

------------------------------------------------------------
-- 강의평 등록
-- StudentReviewRequest:
-- rating, difficulty, comment
-- difficulty: 1~2 EASY / 3 MEDIUM / 4~5 HARD
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE INSERT_REVIEW(
    p_s_id          IN student.s_id%TYPE,
    p_c_id          IN class.c_id%TYPE,
    p_rating        IN NUMBER,
    p_difficulty    IN NUMBER,
    p_comment       IN VARCHAR2,
    p_review_id     OUT VARCHAR2,
    p_submitted_at  OUT VARCHAR2,
    p_result        OUT VARCHAR2
)
IS
    v_e_id        enroll.e_id%TYPE;
    v_difficulty  review.difficulty%TYPE;
    v_created_at  review.created_at%TYPE;
    v_year        NUMBER;
    v_semester    NUMBER;
BEGIN
    p_review_id := NULL;
    p_submitted_at := NULL;

    IF p_rating IS NULL OR p_rating < 1 OR p_rating > 5 THEN
        p_result := 'INVALID_RATING';
        RETURN;
    END IF;

    IF p_difficulty IS NULL THEN
        v_difficulty := 'MEDIUM';
    ELSIF p_difficulty <= 2 THEN
        v_difficulty := 'EASY';
    ELSIF p_difficulty >= 4 THEN
        v_difficulty := 'HARD';
    ELSE
        v_difficulty := 'MEDIUM';
    END IF;

    v_year := Date2EnrollYear(SYSDATE);
    v_semester := Date2EnrollSemester(SYSDATE);

    SELECT e.e_id
    INTO v_e_id
    FROM enroll e
    WHERE e.s_id = p_s_id
      AND e.c_id = p_c_id
      AND e.e_year = v_year
      AND e.e_semester = v_semester
      AND e.e_status IN ('ENROLLED', 'COMPLETED')
    ORDER BY e.e_date DESC
    FETCH FIRST 1 ROW ONLY;

    INSERT INTO review (
        e_id,
        rating_overall,
        rating_content,
        rating_workload,
        rating_professor,
        difficulty,
        pros,
        cons,
        advice,
        is_anonymous,
        created_at
    )
    VALUES (
        v_e_id,
        p_rating,
        p_rating,
        p_rating,
        p_rating,
        v_difficulty,
        p_comment,
        NULL,
        NULL,
        1,
        SYSTIMESTAMP
    )
    RETURNING review_id, created_at
    INTO p_review_id, v_created_at;

    p_submitted_at := TO_CHAR(v_created_at, 'YYYY-MM-DD HH24:MI:SS');
    p_result := 'SUCCESS';

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_result := 'ENROLLMENT_NOT_FOUND';

    WHEN DUP_VAL_ON_INDEX THEN
        p_result := 'ALREADY_REVIEWED';

    WHEN OTHERS THEN
        p_result := 'SQLCODE=' || SQLCODE;
END;
/

------------------------------------------------------------
-- 수강 요청 등록/재요청
-- StudentBorrowRequest:
-- division, reason
-- division 문자열 파싱은 Service에서 하고 p_c_no NUMBER로 전달
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE INSERT_BORROW_REQUEST(
    p_s_id        IN student.s_id%TYPE,
    p_c_id        IN class.c_id%TYPE,
    p_c_no        IN class.c_no%TYPE,
    p_reason      IN VARCHAR2,
    p_request_id  OUT VARCHAR2,
    p_result      OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_request_id := NULL;

    IF p_reason IS NULL OR TRIM(p_reason) IS NULL THEN
        p_result := 'REASON_REQUIRED';
        RETURN;
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM class
    WHERE c_id = p_c_id
      AND c_no = p_c_no;

    IF v_count = 0 THEN
        p_result := 'CLASS_NOT_FOUND';
        RETURN;
    END IF;

    MERGE INTO course_request cr
    USING (
        SELECT
            p_s_id AS s_id,
            p_c_id AS c_id,
            p_c_no AS c_no
        FROM dual
    ) src
    ON (
        cr.s_id = src.s_id
        AND cr.c_id = src.c_id
        AND cr.c_no = src.c_no
    )
    WHEN MATCHED THEN
        UPDATE SET
            cr.reason = p_reason,
            cr.status = 'PENDING',
            cr.requested_at = SYSTIMESTAMP,
            cr.processed_at = NULL,
            cr.processed_by_p_id = NULL
    WHEN NOT MATCHED THEN
        INSERT (
            s_id,
            c_id,
            c_no,
            reason,
            status,
            requested_at,
            processed_at,
            processed_by_p_id
        )
        VALUES (
            p_s_id,
            p_c_id,
            p_c_no,
            p_reason,
            'PENDING',
            SYSTIMESTAMP,
            NULL,
            NULL
        );

    SELECT TO_CHAR(request_id)
    INTO p_request_id
    FROM course_request
    WHERE s_id = p_s_id
      AND c_id = p_c_id
      AND c_no = p_c_no;

    p_result := 'SUCCESS';

EXCEPTION
    WHEN OTHERS THEN
        p_result := 'SQLCODE=' || SQLCODE;
END;
/

------------------------------------------------------------
-- 리뷰 입력 전 검증 트리거
-- 중복은 UK_REVIEW_ENROLL이 최종 차단
------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_REVIEW_BEFORE_INSERT
BEFORE INSERT ON review
FOR EACH ROW
DECLARE
    v_status enroll.e_status%TYPE;
BEGIN
    SELECT e_status
    INTO v_status
    FROM enroll
    WHERE e_id = :NEW.e_id;

    IF v_status NOT IN ('ENROLLED', 'COMPLETED') THEN
        RAISE_APPLICATION_ERROR(
            -20021,
            '리뷰를 작성할 수 없는 수강 상태입니다.'
        );
    END IF;

    IF :NEW.created_at IS NULL THEN
        :NEW.created_at := SYSTIMESTAMP;
    END IF;

    IF :NEW.is_anonymous IS NULL THEN
        :NEW.is_anonymous := 1;
    END IF;
END;
/

------------------------------------------------------------
-- 수강 요청 입력 전 기본값 보정 트리거
-- 중복/재요청 처리는 INSERT_BORROW_REQUEST의 MERGE가 담당
------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_BORROW_REQUEST_BEFORE
BEFORE INSERT OR UPDATE ON course_request
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.status IS NULL THEN
            :NEW.status := 'PENDING';
        END IF;

        IF :NEW.requested_at IS NULL THEN
            :NEW.requested_at := SYSTIMESTAMP;
        END IF;
    END IF;

    IF UPDATING AND :NEW.status = 'PENDING' THEN
        :NEW.processed_at := NULL;
        :NEW.processed_by_p_id := NULL;
    END IF;
END;
/

------------------------------------------------------------
-- 테스트 예시
------------------------------------------------------------
-- 장바구니 추가
-- VARIABLE v_result VARCHAR2(100);
-- EXEC INSERT_CART('20230001', 'CSE4077', 1, :v_result);
-- PRINT v_result;

-- 장바구니 일괄 수강신청
-- VARIABLE results_rc REFCURSOR;
-- VARIABLE summary_rc REFCURSOR;
-- EXEC ENROLL_FROM_CART('20230001', '1,3', :results_rc, :summary_rc);
-- PRINT results_rc;
-- PRINT summary_rc;

-- 리뷰 등록
-- VARIABLE review_id VARCHAR2(100);
-- VARIABLE submitted_at VARCHAR2(100);
-- VARIABLE review_result VARCHAR2(100);
-- EXEC INSERT_REVIEW(
--   '20230001',
--   'CSE3033',
--   5,
--   4,
--   'SQL과 PL/SQL 실습이 유용했습니다.',
--   :review_id,
--   :submitted_at,
--   :review_result
-- );
-- PRINT review_id;
-- PRINT submitted_at;
-- PRINT review_result;

-- 수강 요청
-- VARIABLE request_id VARCHAR2(100);
-- VARIABLE request_result VARCHAR2(100);
-- EXEC INSERT_BORROW_REQUEST(
--   '20230001',
--   'CSE4077',
--   1,
--   '졸업을 위해 수강이 필요합니다.',
--   :request_id,
--   :request_result
-- );
-- PRINT request_id;
-- PRINT request_result;

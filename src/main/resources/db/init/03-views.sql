WHENEVER SQLERROR EXIT SQL.SQLCODE;

alter session set container = xepdb1;
alter session set current_schema = backend;

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
   LPAD(TO_CHAR(cls.c_no), 2, '0') || '遺꾨컲' AS division,
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
      ELSE TO_CHAR(n.target_c_no) || '遺꾨컲'
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
      ELSE TO_CHAR(c.c_no) || '遺꾨컲'
   END AS division,
   n.target_c_id AS target_course_id,
   CASE
      WHEN n.target_c_no IS NULL THEN NULL
      ELSE TO_CHAR(n.target_c_no) || '遺꾨컲'
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


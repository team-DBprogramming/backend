-- Dummy seed data for src/main/resources/db/assignment-schema.sql.
-- Run this after assignment-schema.sql has been executed.
-- Data is mapped from dummy-data.sql into the assignment-style schema:
-- user_account/student -> student, course -> lecture, course_section -> class,
-- section_schedule -> class_time, enrollment -> enroll.

WHENEVER SQLERROR EXIT SQL.SQLCODE;

ALTER SESSION SET CONTAINER = XEPDB1;
ALTER SESSION SET CURRENT_SCHEMA = backend;

INSERT INTO student (s_id, s_pwd, s_name, s_major, s_addr, s_phone, s_email, s_year, s_status)
VALUES ('20230001', '0001', '김민지', '컴퓨터과학과', '서울시 용산구', '010-1111-0001', 'minji.kim@example.edu', 4, 'ENROLLED');
INSERT INTO student (s_id, s_pwd, s_name, s_major, s_addr, s_phone, s_email, s_year, s_status)
VALUES ('20230002', '0002', '이준호', '컴퓨터과학과', '서울시 마포구', '010-1111-0002', 'junho.lee@example.edu', 4, 'ENROLLED');
INSERT INTO student (s_id, s_pwd, s_name, s_major, s_addr, s_phone, s_email, s_year, s_status)
VALUES ('20240001', '0003', '박서연', '컴퓨터과학과', '서울시 서대문구', '010-1111-0003', 'seoyeon.park@example.edu', 3, 'ENROLLED');
INSERT INTO student (s_id, s_pwd, s_name, s_major, s_addr, s_phone, s_email, s_year, s_status)
VALUES ('20220001', '0004', '최현우', '경영학과', '서울시 종로구', '010-1111-0004', 'hyunwoo.choi@example.edu', 5, 'LEAVE');
INSERT INTO student (s_id, s_pwd, s_name, s_major, s_addr, s_phone, s_email, s_year, s_status)
VALUES ('20210001', '0005', '정유나', '수학과', '서울시 중구', '010-1111-0005', 'yuna.jung@example.edu', 6, 'ENROLLED');

INSERT INTO professor (p_id, p_name, p_major, p_office, p_phone, p_email)
VALUES ('P10001', '한지훈', '컴퓨터과학과', '소프트웨어관501', '02-2222-1001', 'jihoon.han@example.edu');
INSERT INTO professor (p_id, p_name, p_major, p_office, p_phone, p_email)
VALUES ('P10002', '강소라', '컴퓨터과학과', '소프트웨어관407', '02-2222-1002', 'sora.kang@example.edu');
INSERT INTO professor (p_id, p_name, p_major, p_office, p_phone, p_email)
VALUES ('P10003', '오태민', '경영학과', '경영관302', '02-2222-1003', 'taemin.oh@example.edu');
INSERT INTO professor (p_id, p_name, p_major, p_office, p_phone, p_email)
VALUES ('P10004', '신은하', '수학과', '과학관215', '02-2222-1004', 'eunha.shin@example.edu');

INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (1, 1010, '프로그래밍', '강소라', 3, 'P10002');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (2, 2010, '자료구조', '강소라', 3, 'P10002');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (3, 3033, '데이터베이스', '한지훈', 3, 'P10001');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (4, 4050, '소공', '한지훈', 3, 'P10001');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (5, 4077, '인공지능', '강소라', 3, 'P10002');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (6, 2100, '마케팅', '오태민', 3, 'P10003');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (7, 2200, '선형대수', '신은하', 3, 'P10004');
INSERT INTO lecture (id, no, subject, prof, credit, p_id)
VALUES (8, 1500, '영어', '신은하', 2, 'P10004');

INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10001', 3033, 1, '명신관101', 35, 0, 'OPEN');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10001', 4050, 1, '명신관203', 30, 0, 'OPEN');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10002', 4077, 1, '명신관305', 40, 0, 'OPEN');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10002', 2010, 2, '명신관101', 40, 0, 'OPEN');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10003', 2100, 1, '경영관201', 45, 0, 'OPEN');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10004', 2200, 1, '과학관110', 50, 0, 'OPEN');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10004', 1500, 1, '명신관203', 25, 0, 'CLOSED');
INSERT INTO class (p_id, c_id, c_no, c_where, c_max, c_now, c_status)
VALUES ('P10002', 1010, 1, '명신관305', 45, 0, 'OPEN');

INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (1, 'P10001', 3033, 1, 'MON', '09:00', '10:15');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (2, 'P10001', 3033, 1, 'WED', '09:00', '10:15');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (3, 'P10001', 4050, 1, 'TUE', '10:30', '11:45');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (4, 'P10001', 4050, 1, 'THU', '10:30', '11:45');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (5, 'P10002', 4077, 1, 'MON', '13:00', '14:15');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (6, 'P10002', 4077, 1, 'WED', '13:00', '14:15');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (7, 'P10002', 2010, 2, 'FRI', '09:00', '11:50');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (8, 'P10003', 2100, 1, 'TUE', '15:00', '17:45');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (9, 'P10004', 2200, 1, 'MON', '10:30', '11:45');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (10, 'P10004', 2200, 1, 'WED', '10:30', '11:45');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (11, 'P10004', 1500, 1, 'SAT', '09:00', '11:30');
INSERT INTO class_time (time_id, p_id, c_id, c_no, c_day, c_start, c_end)
VALUES (12, 'P10002', 1010, 1, 'MON', '15:00', '16:15');

INSERT INTO registration_period (period_id, e_year, e_semester, period_type, start_at, end_at)
VALUES (1, 2026, 2, 'MAIN', TO_TIMESTAMP('2026-06-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-06-30 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO registration_period (period_id, e_year, e_semester, period_type, start_at, end_at)
VALUES (2, 2026, 2, 'ADD_DROP', TO_TIMESTAMP('2026-07-07 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-07-20 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));

-- Trigger trg_enroll_class_count updates class.c_now for ENROLLED rows.
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (1, '20230001', 3033, 1, 2026, 2, TO_TIMESTAMP('2026-06-01 09:12:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, NULL);
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (2, '20230002', 3033, 1, 2026, 2, TO_TIMESTAMP('2026-06-01 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, NULL);
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (3, '20230001', 4050, 1, 2026, 2, TO_TIMESTAMP('2026-06-01 09:40:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, '프로젝트 참여도가 높음');
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (4, '20230002', 4077, 1, 2026, 2, TO_TIMESTAMP('2026-06-02 16:35:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, '수강 예외 승인');
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (5, '20240001', 4077, 1, 2026, 2, TO_TIMESTAMP('2026-06-02 10:05:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, NULL);
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (6, '20240001', 2010, 2, 2026, 2, TO_TIMESTAMP('2026-06-02 10:10:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, NULL);
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (7, '20230001', 2100, 1, 2026, 2, TO_TIMESTAMP('2026-06-03 15:05:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, '교양 선택 과목');
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (8, '20210001', 2200, 1, 2026, 2, TO_TIMESTAMP('2026-06-03 14:10:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, NULL);
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (9, '20210001', 1500, 1, 2026, 2, TO_TIMESTAMP('2026-06-03 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'ENROLLED', NULL, NULL);
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (10, '20220001', 3033, 1, 2025, 2, TO_TIMESTAMP('2025-08-18 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'COMPLETED', NULL, '휴학 전 이수 완료');
INSERT INTO enroll (e_id, s_id, c_id, c_no, e_year, e_semester, e_date, e_status, e_drop_date, professor_note)
VALUES (11, '20240001', 3033, 1, 2026, 2, TO_TIMESTAMP('2026-06-04 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'DROPPED', TO_TIMESTAMP('2026-06-05 17:00:00', 'YYYY-MM-DD HH24:MI:SS'), '정정 기간 취소');

INSERT INTO cart_item (cart_id, s_id, c_id, c_no, added_at)
VALUES (1, '20230001', 4077, 1, TO_TIMESTAMP('2026-05-28 10:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO cart_item (cart_id, s_id, c_id, c_no, added_at)
VALUES (2, '20230002', 4050, 1, TO_TIMESTAMP('2026-06-05 10:12:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO cart_item (cart_id, s_id, c_id, c_no, added_at)
VALUES (3, '20240001', 3033, 1, TO_TIMESTAMP('2026-06-04 16:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO cart_item (cart_id, s_id, c_id, c_no, added_at)
VALUES (4, '20210001', 1010, 1, TO_TIMESTAMP('2026-06-05 11:20:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO course_request (request_id, s_id, c_id, c_no, reason, status, requested_at, processed_at, processed_by_p_id)
VALUES (1, '20240001', 4050, 1, '소프트웨어공학 프로젝트 분반에 참여하고 싶습니다.', 'PENDING', TO_TIMESTAMP('2026-06-04 14:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO course_request (request_id, s_id, c_id, c_no, reason, status, requested_at, processed_at, processed_by_p_id)
VALUES (2, '20230002', 4077, 1, '졸업 계획을 위해 인공지능 과목 수강이 필요합니다.', 'APPROVED', TO_TIMESTAMP('2026-06-03 11:20:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-06-03 16:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'P10002');
INSERT INTO course_request (request_id, s_id, c_id, c_no, reason, status, requested_at, processed_at, processed_by_p_id)
VALUES (3, '20220001', 3033, 1, '복학 후 이번 학기에 데이터베이스 수강이 필요합니다.', 'REJECTED', TO_TIMESTAMP('2026-06-04 09:40:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-06-04 12:05:00', 'YYYY-MM-DD HH24:MI:SS'), 'P10001');
INSERT INTO course_request (request_id, s_id, c_id, c_no, reason, status, requested_at, processed_at, processed_by_p_id)
VALUES (4, '20210001', 2100, 1, '연계 전공 선택 과목으로 마케팅을 수강하고 싶습니다.', 'PENDING', TO_TIMESTAMP('2026-06-05 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);

INSERT INTO review (review_id, e_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (1, 1, 5, 5, 4, 5, 'MEDIUM', 'SQL과 PL/SQL 실습이 유용합니다.', '매주 과제가 있어 꾸준한 노력이 필요합니다.', 'SQL 프로젝트를 일찍 시작하세요.', 1, TO_TIMESTAMP('2026-03-12 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, e_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (2, 2, 4, 4, 3, 5, 'MEDIUM', '트랜잭션 설명이 명확합니다.', '실습 시간이 짧게 느껴집니다.', '시험 전 정규화를 복습하세요.', 0, TO_TIMESTAMP('2026-04-03 09:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, e_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (3, 3, 5, 5, 4, 5, 'HARD', '프로젝트가 실제 개발과 가깝습니다.', '마감 일정이 빠듯할 수 있습니다.', '첫 주부터 회의록을 남기세요.', 1, TO_TIMESTAMP('2026-03-28 16:10:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO notification (notification_id, recipient_p_id, sender_s_id, target_c_id, target_c_no, target_request_id, title, body, type, is_read, created_at)
VALUES (1, 'P10001', '20240001', 4050, 1, 1, '새 수강 요청', '학생이 소프트웨어공학 분반 수강을 요청했습니다.', 'COURSE_REQUEST', 0, TO_TIMESTAMP('2026-06-04 14:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_s_id, sender_p_id, target_c_id, target_c_no, target_request_id, title, body, type, is_read, created_at)
VALUES (2, '20230002', 'P10002', 4077, 1, 2, '수강 요청 승인', '인공지능 수강 요청이 승인되었습니다.', 'COURSE_REQUEST_RESULT', 1, TO_TIMESTAMP('2026-06-03 16:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_s_id, sender_p_id, target_c_id, target_c_no, target_request_id, title, body, type, is_read, created_at)
VALUES (3, '20220001', 'P10001', 3033, 1, 3, '수강 요청 반려', '데이터베이스 수강 요청이 반려되었습니다.', 'COURSE_REQUEST_RESULT', 0, TO_TIMESTAMP('2026-06-04 12:05:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_p_id, sender_s_id, target_c_id, target_c_no, title, body, type, is_read, created_at)
VALUES (4, 'P10001', '20230001', 3033, 1, '새 강의 평가', '학생이 데이터베이스 강의 평가를 등록했습니다.', 'COURSE_REVIEW', 0, TO_TIMESTAMP('2026-03-12 12:01:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_s_id, title, body, type, is_read, created_at)
VALUES (5, '20210001', '시스템 공지', '점검 시간에는 수강신청을 사용할 수 없습니다.', 'SYSTEM', 0, TO_TIMESTAMP('2026-06-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO refresh_token (token_id, login_id, login_type, token_hash, remember_me, expires_at, revoked_at, created_at)
VALUES (1, '20230001', 'STUDENT', 'seed-token-hash-student-20230001', 0, TO_TIMESTAMP('2026-07-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, TO_TIMESTAMP('2026-06-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO refresh_token (token_id, login_id, login_type, token_hash, remember_me, expires_at, revoked_at, created_at)
VALUES (2, 'P10001', 'PROFESSOR', 'seed-token-hash-professor-p10001', 1, TO_TIMESTAMP('2026-07-30 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, TO_TIMESTAMP('2026-06-01 10:00:00', 'YYYY-MM-DD HH24:MI:SS'));

COMMIT;

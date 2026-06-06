-- Dummy seed data for src/main/resources/db/schema.sql.
-- Run this after schema.sql has been executed.

WHENEVER SQLERROR EXIT SQL.SQLCODE;

ALTER SESSION SET CONTAINER = XEPDB1;
ALTER SESSION SET CURRENT_SCHEMA = backend;

INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (1, 'CSE', '컴퓨터공학과', '소프트웨어대학');
INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (2, 'BUS', '경영학과', '경영대학');
INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (3, 'MATH', '수학과', '자연과학대학');
INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (4, 'ENG', '영어영문학과', '인문대학');

INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (1, '20230001', 'dummy-password-hash', 'STUDENT', 'minji.kim@example.edu', '010-1111-0001', 1, TO_TIMESTAMP('2026-05-28 09:10:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-03-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (2, '20230002', 'dummy-password-hash', 'STUDENT', 'junho.lee@example.edu', '010-1111-0002', 1, TO_TIMESTAMP('2026-05-29 13:20:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-03-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (3, '20240001', 'dummy-password-hash', 'STUDENT', 'seoyeon.park@example.edu', '010-1111-0003', 1, TO_TIMESTAMP('2026-05-27 18:05:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2024-03-04 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (4, '20220001', 'dummy-password-hash', 'STUDENT', 'hyunwoo.choi@example.edu', '010-1111-0004', 1, NULL, TO_TIMESTAMP('2022-03-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (5, '20210001', 'dummy-password-hash', 'STUDENT', 'yuna.jung@example.edu', '010-1111-0005', 1, TO_TIMESTAMP('2026-05-20 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2021-03-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (6, 'P1001', 'dummy-password-hash', 'PROFESSOR', 'jihoon.han@example.edu', '02-2222-1001', 1, TO_TIMESTAMP('2026-05-30 08:40:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (10, '2024123456', 'dummy-password-hash', 'STUDENT', 'gildong.hong@example.edu', '010-1111-1234', 1, TO_TIMESTAMP('2026-06-01 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2024-03-04 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (7, 'P1002', 'dummy-password-hash', 'PROFESSOR', 'sora.kang@example.edu', '02-2222-1002', 1, TO_TIMESTAMP('2026-05-29 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (8, 'P1003', 'dummy-password-hash', 'PROFESSOR', 'taemin.oh@example.edu', '02-2222-1003', 1, NULL, TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (9, 'P1004', 'dummy-password-hash', 'PROFESSOR', 'eunha.shin@example.edu', '02-2222-1004', 1, TO_TIMESTAMP('2026-05-28 11:15:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (1, 1, 1, '김민지', 4, 'ENROLLED', 3.82, 96, 2023);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (2, 2, 1, '이준호', 4, 'ENROLLED', 3.55, 91, 2023);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (3, 3, 1, '박서연', 3, 'ENROLLED', 3.74, 65, 2024);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (4, 4, 2, '최현우', 5, 'LEAVE', 3.21, 108, 2022);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (5, 5, 3, '정유나', 6, 'ENROLLED', 3.95, 124, 2021);

INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (1, 6, 1, '한지훈', '교수', '소프트웨어관 501호');
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (6, 10, 1, '홍길동', 3, 'ENROLLED', 3.68, 54, 2024);
INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (2, 7, 1, '강소라', '부교수', '소프트웨어관 407호');
INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (3, 8, 2, '오태민', '조교수', '경영관 302호');
INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (4, 9, 3, '신은하', '강사', '과학관 215호');

INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (1, 'CSE1010', '프로그래밍 입문', 3, 'MAJOR_REQUIRED', 1, 1, '문제 해결 실습을 통해 프로그래밍 기초를 학습합니다.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (2, 'CSE2010', '자료구조', 3, 'MAJOR_REQUIRED', 1, 2, '리스트, 트리, 그래프, 해싱과 알고리즘적 사고를 다룹니다.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (3, 'CSE3033', '데이터베이스 시스템', 3, 'MAJOR_REQUIRED', 1, 3, '관계형 모델링, SQL, 트랜잭션, 인덱싱을 학습합니다.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (4, 'CSE4050', '소프트웨어공학', 3, 'MAJOR_ELECTIVE', 1, 4, '요구사항, 아키텍처, 테스트, 팀 개발 방법을 다룹니다.', 1);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (5, 'CSE4077', '인공지능', 3, 'MAJOR_ELECTIVE', 1, 4, '탐색, 계획, 머신러닝 기초와 인공지능 응용을 학습합니다.', 1);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (6, 'BUS2100', '마케팅 원론', 3, 'MAJOR_REQUIRED', 2, 2, '마케팅 핵심 개념과 시장 분석 방법을 학습합니다.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (7, 'MATH2200', '선형대수학', 3, 'MAJOR_REQUIRED', 3, 2, '벡터공간, 행렬, 선형사상, 고윳값을 다룹니다.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (8, 'ENG1500', '대학영어', 2, 'LIBERAL_REQUIRED', 4, 1, '학술 독해, 작문, 발표 역량을 기릅니다.', 1);

INSERT INTO prerequisite (prereq_id, course_id, required_course_id) VALUES (1, 2, 1);
INSERT INTO prerequisite (prereq_id, course_id, required_course_id) VALUES (2, 3, 2);
INSERT INTO prerequisite (prereq_id, course_id, required_course_id) VALUES (3, 4, 3);
INSERT INTO prerequisite (prereq_id, course_id, required_course_id) VALUES (4, 5, 2);

INSERT INTO semester (semester_id, year, term, start_date, end_date, is_current)
VALUES (1, 2025, 'FALL', TO_DATE('2025-09-01', 'YYYY-MM-DD'), TO_DATE('2025-12-19', 'YYYY-MM-DD'), 0);
INSERT INTO semester (semester_id, year, term, start_date, end_date, is_current)
VALUES (2, 2026, 'SPRING', TO_DATE('2026-03-02', 'YYYY-MM-DD'), TO_DATE('2026-06-19', 'YYYY-MM-DD'), 1);
INSERT INTO semester (semester_id, year, term, start_date, end_date, is_current)
VALUES (3, 2026, 'FALL', TO_DATE('2026-09-01', 'YYYY-MM-DD'), TO_DATE('2026-12-18', 'YYYY-MM-DD'), 0);

INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (1, '소프트웨어관', '101', 40, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (2, '소프트웨어관', '203', 35, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (3, '소프트웨어관', '305', 60, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (4, '경영관', '201', 45, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (5, '과학관', '110', 50, 0);

INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (1, 3, 2, 1, '01', 35, 3, 'OPEN', 'https://example.edu/syllabus/CSE3033-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (2, 4, 2, 1, '01', 30, 2, 'OPEN', 'https://example.edu/syllabus/CSE4050-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (3, 5, 2, 2, '01', 40, 2, 'OPEN', 'https://example.edu/syllabus/CSE4077-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (4, 2, 2, 2, '02', 40, 1, 'OPEN', 'https://example.edu/syllabus/CSE2010-2026S-02');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (5, 6, 2, 3, '01', 45, 1, 'OPEN', 'https://example.edu/syllabus/BUS2100-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (6, 7, 2, 4, '01', 50, 1, 'OPEN', 'https://example.edu/syllabus/MATH2200-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (7, 8, 2, 4, '01', 25, 1, 'CLOSED', 'https://example.edu/syllabus/ENG1500-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (8, 3, 1, 1, '01', 35, 2, 'OPEN', 'https://example.edu/syllabus/CSE3033-2025F-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (9, 1, 3, 2, '01', 45, 0, 'OPEN', 'https://example.edu/syllabus/CSE1010-2026F-01');

INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (1, 1, 1, 'MON', '09:00', '10:15');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (2, 1, 1, 'WED', '09:00', '10:15');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (3, 2, 2, 'TUE', '10:30', '11:45');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (4, 2, 2, 'THU', '10:30', '11:45');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (5, 3, 3, 'MON', '13:00', '14:15');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (6, 3, 3, 'WED', '13:00', '14:15');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (7, 4, 1, 'FRI', '09:00', '11:50');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (8, 5, 4, 'TUE', '15:00', '17:45');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (9, 6, 5, 'MON', '10:30', '11:45');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (10, 6, 5, 'WED', '10:30', '11:45');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (11, 7, 2, 'SAT', '09:00', '11:30');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (12, 8, 1, 'TUE', '09:00', '10:15');
INSERT INTO section_schedule (schedule_id, section_id, classroom_id, day_of_week, start_time, end_time) VALUES (13, 9, 3, 'MON', '15:00', '16:15');

INSERT INTO registration_period (period_id, semester_id, period_type, target_year, start_at, end_at)
VALUES (1, 2, 'CART', NULL, TO_TIMESTAMP('2026-02-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-02-06 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO registration_period (period_id, semester_id, period_type, target_year, start_at, end_at)
VALUES (2, 2, 'MAIN', 4, TO_TIMESTAMP('2026-02-09 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-02-09 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO registration_period (period_id, semester_id, period_type, target_year, start_at, end_at)
VALUES (3, 2, 'MAIN', 3, TO_TIMESTAMP('2026-02-10 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-02-10 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO registration_period (period_id, semester_id, period_type, target_year, start_at, end_at)
VALUES (4, 2, 'ADD_DROP', NULL, TO_TIMESTAMP('2026-03-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-06 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO registration_period (period_id, semester_id, period_type, target_year, start_at, end_at)
VALUES (5, 3, 'CART', NULL, TO_TIMESTAMP('2026-08-03 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-08-07 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO cart_item (cart_id, student_id, section_id, added_at)
VALUES (1, 1, 3, TO_TIMESTAMP('2026-02-03 10:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO cart_item (cart_id, student_id, section_id, added_at)
VALUES (2, 2, 2, TO_TIMESTAMP('2026-02-03 10:12:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO cart_item (cart_id, student_id, section_id, added_at)
VALUES (3, 3, 1, TO_TIMESTAMP('2026-02-04 16:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO cart_item (cart_id, student_id, section_id, added_at)
VALUES (4, 5, 9, TO_TIMESTAMP('2026-08-04 11:20:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (1, 3, 2, '동등한 팀 프로젝트 과목을 이수했으며 이 분반에 참여하고 싶습니다.', 'PENDING', TO_TIMESTAMP('2026-03-03 14:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (2, 2, 3, '졸업 트랙 이수를 위해 이 과목이 필요하며 선수과목 내용을 따라갈 수 있습니다.', 'APPROVED', TO_TIMESTAMP('2026-03-01 11:20:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-01 16:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (3, 4, 1, '휴학 후 복학하여 이번 학기에 데이터베이스 시스템을 수강해야 합니다.', 'REJECTED', TO_TIMESTAMP('2026-03-02 09:40:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-02 12:05:00', 'YYYY-MM-DD HH24:MI:SS'), 1);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (4, 5, 5, '융합 전공 선택 과목으로 마케팅을 수강하고 싶습니다.', 'PENDING', TO_TIMESTAMP('2026-03-04 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (5, 1, 5, '프로덕트 매니지먼트 인턴십을 준비하고 있습니다.', 'APPROVED', TO_TIMESTAMP('2026-03-01 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-01 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3);

INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (1, 1, 1, 'ENROLLED', TO_TIMESTAMP('2026-02-09 09:12:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (2, 2, 1, 'ENROLLED', TO_TIMESTAMP('2026-02-09 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (3, 1, 2, 'ENROLLED', TO_TIMESTAMP('2026-02-09 09:40:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, '프로젝트 참여도가 높음.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (4, 2, 3, 'ENROLLED', TO_TIMESTAMP('2026-03-01 16:35:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, '수강 예외 승인.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (5, 3, 3, 'ENROLLED', TO_TIMESTAMP('2026-02-10 10:05:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (6, 3, 4, 'ENROLLED', TO_TIMESTAMP('2026-02-10 10:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (7, 1, 5, 'ENROLLED', TO_TIMESTAMP('2026-03-01 15:05:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, '융합 선택 과목.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (8, 5, 6, 'ENROLLED', TO_TIMESTAMP('2026-02-10 14:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (9, 5, 7, 'ENROLLED', TO_TIMESTAMP('2026-02-10 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (10, 4, 8, 'COMPLETED', TO_TIMESTAMP('2025-08-18 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, '휴학 전 이수 완료.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (11, 3, 1, 'DROPPED', TO_TIMESTAMP('2026-02-09 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-04 17:00:00', 'YYYY-MM-DD HH24:MI:SS'), '수강 정정 기간에 취소.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (12, 6, 1, 'ENROLLED', TO_TIMESTAMP('2026-03-04 09:20:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (13, 6, 2, 'ENROLLED', TO_TIMESTAMP('2026-03-04 09:25:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);

INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (1, 1, 5, 5, 4, 5, 'MEDIUM', '예제가 실용적이고 피드백이 빠릅니다.', '매주 과제가 있어 꾸준한 노력이 필요합니다.', 'SQL 프로젝트를 일찍 시작하세요.', 1, TO_TIMESTAMP('2026-05-15 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (2, 2, 4, 4, 3, 5, 'MEDIUM', '트랜잭션 설명이 명확합니다.', '일부 실습 시간이 깁니다.', '중간고사 전에 정규화를 복습하세요.', 0, TO_TIMESTAMP('2026-05-18 09:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (3, 3, 5, 5, 4, 5, 'HARD', '팀 프로젝트가 실제 개발과 비슷하게 느껴집니다.', '마일스톤 일정이 촘촘합니다.', '첫 주부터 회의록을 잘 남기세요.', 1, TO_TIMESTAMP('2026-05-20 16:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (4, 4, 4, 5, 5, 4, 'HARD', '인공지능 과제가 흥미롭습니다.', '수학 기초가 있으면 큰 도움이 됩니다.', '확률 기초를 다시 확인하세요.', 1, TO_TIMESTAMP('2026-05-22 10:20:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (5, 10, 4, 4, 3, 4, 'MEDIUM', '데이터베이스 프로젝트가 유용했습니다.', '시험 범위가 넓었습니다.', '쿼리 튜닝을 연습하세요.', 1, TO_TIMESTAMP('2025-12-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (6, 12, 5, 5, 4, 5, 'MEDIUM', '데이터 모델링 실습이 많아 좋았습니다.', '복습할 내용이 많습니다.', 'ERD와 SQL을 같이 정리하면 도움이 됩니다.', 1, TO_TIMESTAMP('2026-06-02 14:30:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (1, 6, 3, 2, 1, '새 수강 요청', '박서연 학생이 소프트웨어공학 수강을 요청했습니다.', 'COURSE_REQUEST', 0, TO_TIMESTAMP('2026-03-03 14:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (2, 2, 7, 3, 2, '수강 요청 승인', '인공지능 수강 요청이 승인되었습니다.', 'COURSE_REQUEST_RESULT', 1, TO_TIMESTAMP('2026-03-01 16:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (3, 4, 6, 1, 3, '수강 요청 반려', '데이터베이스 시스템 수강 요청이 반려되었습니다.', 'COURSE_REQUEST_RESULT', 0, TO_TIMESTAMP('2026-03-02 12:05:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (4, 6, 1, 1, NULL, '새 강의 평가', '학생이 데이터베이스 시스템 강의 평가를 등록했습니다.', 'COURSE_REVIEW', 0, TO_TIMESTAMP('2026-05-15 12:01:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (5, 6, 1, 2, NULL, '새 강의 평가', '학생이 소프트웨어공학 강의 평가를 등록했습니다.', 'COURSE_REVIEW', 0, TO_TIMESTAMP('2026-05-20 16:11:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (6, 1, 6, 1, NULL, '프로젝트 안내', '금요일까지 데이터베이스 프로젝트 제안서를 제출해 주세요.', 'PROFESSOR_MESSAGE', 0, TO_TIMESTAMP('2026-04-10 08:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (7, 5, NULL, NULL, NULL, '시스템 점검', '일요일 02:00-04:00에는 수강신청 시스템을 사용할 수 없습니다.', 'SYSTEM', 0, TO_TIMESTAMP('2026-05-25 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));

COMMIT;

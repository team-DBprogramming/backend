-- Dummy seed data for src/main/resources/db/schema.sql.
-- Run this after schema.sql has been executed.

WHENEVER SQLERROR EXIT SQL.SQLCODE;

ALTER SESSION SET CONTAINER = XEPDB1;
ALTER SESSION SET CURRENT_SCHEMA = backend;

INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (1, 'CSE', 'Computer Science and Engineering', 'College of Software');
INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (2, 'BUS', 'Business Administration', 'College of Business');
INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (3, 'MATH', 'Mathematics', 'College of Natural Sciences');
INSERT INTO department (dept_id, dept_code, dept_name, college) VALUES (4, 'ENG', 'English Language and Literature', 'College of Humanities');

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
VALUES (7, 'P1002', 'dummy-password-hash', 'PROFESSOR', 'sora.kang@example.edu', '02-2222-1002', 1, TO_TIMESTAMP('2026-05-29 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (8, 'P1003', 'dummy-password-hash', 'PROFESSOR', 'taemin.oh@example.edu', '02-2222-1003', 1, NULL, TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO user_account (user_id, login_id, password_hash, role, email, phone, is_active, last_login_at, created_at)
VALUES (9, 'P1004', 'dummy-password-hash', 'PROFESSOR', 'eunha.shin@example.edu', '02-2222-1004', 1, TO_TIMESTAMP('2026-05-28 11:15:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2020-02-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (1, 1, 1, 'Minji Kim', 4, 'ENROLLED', 3.82, 96, 2023);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (2, 2, 1, 'Junho Lee', 4, 'ENROLLED', 3.55, 91, 2023);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (3, 3, 1, 'Seoyeon Park', 3, 'ENROLLED', 3.74, 65, 2024);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (4, 4, 2, 'Hyunwoo Choi', 5, 'LEAVE', 3.21, 108, 2022);
INSERT INTO student (student_id, user_id, dept_id, name, year_level, enrollment_status, cum_gpa, total_credits, admission_year)
VALUES (5, 5, 3, 'Yuna Jung', 6, 'ENROLLED', 3.95, 124, 2021);

INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (1, 6, 1, 'Jihoon Han', 'Professor', 'Software Hall 501');
INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (2, 7, 1, 'Sora Kang', 'Associate Professor', 'Software Hall 407');
INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (3, 8, 2, 'Taemin Oh', 'Assistant Professor', 'Business Hall 302');
INSERT INTO professor (professor_id, user_id, dept_id, name, title, office) VALUES (4, 9, 3, 'Eunha Shin', 'Lecturer', 'Science Hall 215');

INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (1, 'CSE1010', 'Introduction to Programming', 3, 'MAJOR_REQUIRED', 1, 1, 'Programming fundamentals with problem solving practice.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (2, 'CSE2010', 'Data Structures', 3, 'MAJOR_REQUIRED', 1, 2, 'Lists, trees, graphs, hashing, and algorithmic thinking.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (3, 'CSE3033', 'Database Systems', 3, 'MAJOR_REQUIRED', 1, 3, 'Relational modeling, SQL, transactions, and indexing.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (4, 'CSE4050', 'Software Engineering', 3, 'MAJOR_ELECTIVE', 1, 4, 'Requirements, architecture, testing, and team development.', 1);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (5, 'CSE4077', 'Artificial Intelligence', 3, 'MAJOR_ELECTIVE', 1, 4, 'Search, planning, machine learning basics, and AI applications.', 1);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (6, 'BUS2100', 'Principles of Marketing', 3, 'MAJOR_REQUIRED', 2, 2, 'Core marketing concepts and market analysis.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (7, 'MATH2200', 'Linear Algebra', 3, 'MAJOR_REQUIRED', 3, 2, 'Vector spaces, matrices, linear maps, and eigenvalues.', 0);
INSERT INTO course (course_id, course_code, course_name, credits, course_type, dept_id, target_year, description, is_english)
VALUES (8, 'ENG1500', 'Academic English', 2, 'LIBERAL_REQUIRED', 4, 1, 'Academic reading, writing, and presentation skills.', 1);

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

INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (1, 'Software Hall', '101', 40, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (2, 'Software Hall', '203', 35, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (3, 'Software Hall', '305', 60, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (4, 'Business Hall', '201', 45, 1);
INSERT INTO classroom (classroom_id, building, room_no, capacity, has_projector) VALUES (5, 'Science Hall', '110', 50, 0);

INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (1, 3, 2, 1, '01', 35, 2, 'OPEN', 'https://example.edu/syllabus/CSE3033-2026S-01');
INSERT INTO course_section (section_id, course_id, semester_id, professor_id, section_no, capacity, enrolled_count, status, syllabus_url)
VALUES (2, 4, 2, 1, '01', 30, 1, 'OPEN', 'https://example.edu/syllabus/CSE4050-2026S-01');
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
VALUES (1, 3, 2, 'I completed an equivalent team project course and would like to join this section.', 'PENDING', TO_TIMESTAMP('2026-03-03 14:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (2, 2, 3, 'I need this course for my graduation track and can handle the prerequisite.', 'APPROVED', TO_TIMESTAMP('2026-03-01 11:20:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-01 16:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (3, 4, 1, 'I am returning from leave and need database systems this semester.', 'REJECTED', TO_TIMESTAMP('2026-03-02 09:40:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-02 12:05:00', 'YYYY-MM-DD HH24:MI:SS'), 1);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (4, 5, 5, 'I want to take marketing as an interdisciplinary elective.', 'PENDING', TO_TIMESTAMP('2026-03-04 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO course_request (request_id, student_id, section_id, reason, status, requested_at, processed_at, processed_by_professor_id)
VALUES (5, 1, 5, 'I am preparing for a product management internship.', 'APPROVED', TO_TIMESTAMP('2026-03-01 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-01 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3);

INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (1, 1, 1, 'ENROLLED', TO_TIMESTAMP('2026-02-09 09:12:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (2, 2, 1, 'ENROLLED', TO_TIMESTAMP('2026-02-09 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (3, 1, 2, 'ENROLLED', TO_TIMESTAMP('2026-02-09 09:40:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'Strong project participation.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (4, 2, 3, 'ENROLLED', TO_TIMESTAMP('2026-03-01 16:35:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'Override approved.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (5, 3, 3, 'ENROLLED', TO_TIMESTAMP('2026-02-10 10:05:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (6, 3, 4, 'ENROLLED', TO_TIMESTAMP('2026-02-10 10:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (7, 1, 5, 'ENROLLED', TO_TIMESTAMP('2026-03-01 15:05:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'Interdisciplinary elective.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (8, 5, 6, 'ENROLLED', TO_TIMESTAMP('2026-02-10 14:10:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (9, 5, 7, 'ENROLLED', TO_TIMESTAMP('2026-02-10 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL);
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (10, 4, 8, 'COMPLETED', TO_TIMESTAMP('2025-08-18 09:30:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'Completed before leave.');
INSERT INTO enrollment (enrollment_id, student_id, section_id, status, enrolled_at, dropped_at, professor_note)
VALUES (11, 3, 1, 'DROPPED', TO_TIMESTAMP('2026-02-09 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2026-03-04 17:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Dropped during add/drop period.');

INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (1, 1, 5, 5, 4, 5, 'MEDIUM', 'Examples are practical and feedback is fast.', 'Weekly assignments require steady effort.', 'Start the SQL project early.', 1, TO_TIMESTAMP('2026-05-15 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (2, 2, 4, 4, 3, 5, 'MEDIUM', 'Clear explanations of transactions.', 'Some labs are long.', 'Review normalization before midterm.', 0, TO_TIMESTAMP('2026-05-18 09:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (3, 3, 5, 5, 4, 5, 'HARD', 'Team project feels close to real development.', 'Milestones are dense.', 'Keep meeting notes from week one.', 1, TO_TIMESTAMP('2026-05-20 16:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (4, 4, 4, 5, 5, 4, 'HARD', 'AI assignments are interesting.', 'Math background helps a lot.', 'Refresh probability basics.', 1, TO_TIMESTAMP('2026-05-22 10:20:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO review (review_id, enrollment_id, rating_overall, rating_content, rating_workload, rating_professor, difficulty, pros, cons, advice, is_anonymous, created_at)
VALUES (5, 10, 4, 4, 3, 4, 'MEDIUM', 'Database project was useful.', 'Exam coverage was broad.', 'Practice query tuning.', 1, TO_TIMESTAMP('2025-12-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (1, 6, 3, 2, 1, 'New course request', 'Seoyeon Park requested access to Software Engineering.', 'COURSE_REQUEST', 0, TO_TIMESTAMP('2026-03-03 14:10:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (2, 2, 7, 3, 2, 'Course request approved', 'Your Artificial Intelligence request was approved.', 'COURSE_REQUEST_RESULT', 1, TO_TIMESTAMP('2026-03-01 16:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (3, 4, 6, 1, 3, 'Course request rejected', 'Your Database Systems request was rejected.', 'COURSE_REQUEST_RESULT', 0, TO_TIMESTAMP('2026-03-02 12:05:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (4, 6, 1, 1, NULL, 'New course review', 'A student submitted a Database Systems review.', 'COURSE_REVIEW', 0, TO_TIMESTAMP('2026-05-15 12:01:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (5, 6, 1, 2, NULL, 'New course review', 'A student submitted a Software Engineering review.', 'COURSE_REVIEW', 0, TO_TIMESTAMP('2026-05-20 16:11:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (6, 1, 6, 1, NULL, 'Project reminder', 'Please submit the database project proposal by Friday.', 'PROFESSOR_MESSAGE', 0, TO_TIMESTAMP('2026-04-10 08:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO notification (notification_id, recipient_user_id, sender_user_id, target_section_id, target_request_id, title, body, type, is_read, created_at)
VALUES (7, 5, NULL, NULL, NULL, 'System maintenance', 'The registration system will be unavailable on Sunday 02:00-04:00.', 'SYSTEM', 0, TO_TIMESTAMP('2026-05-25 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));

COMMIT;

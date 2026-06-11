select count(*), p_id from class_time group by p_id;
select * from class_time ORDER by p_id;
DESCRIBE class_time;

SELECT * from registration_period;
\
select l.credit
from class c join lecture l on c.c_id = l.no;

select * from lecture;
select * from class;
select * from enroll;
select * from cart_item;

SELECT c_id, c_no, c_now, c_max
FROM class
ORDER BY c_id, c_no;

WHERE s_id = sStudentId
      AND c_id = sCourseId
      AND c_no = nCourseIdNo
      AND e_year = nYear
      AND e_semester = nSemester
      AND e_status = 'ENROLLED';




-- 현재 연도/학기 확인
SELECT Date2EnrollYear(SYSDATE), Date2EnrollSemester(SYSDATE) FROM dual;


-- registration_period 확인
SELECT * FROM registration_period;

-- 현재 시간이 기간 안에 있는지 확인
SELECT period_id, e_year, e_semester, period_type, start_at, end_at,
       CASE WHEN SYSTIMESTAMP BETWEEN start_at AND end_at THEN 'IN_PROGRESS' ELSE 'CLOSED' END AS status
FROM registration_period;

SELECT object_name, object_type, status 
FROM user_objects 
WHERE object_type IN ('PROCEDURE', 'FUNCTION', 'TRIGGER')
ORDER BY object_type, object_name;


select c_id, c_no 
FROM class
;


SELECT cart_id, s_id, c_id, c_no
FROM cart_item
WHERE s_id = '현재 로그인 학생 학번'
  AND c_id = 'BUS2100'
  AND c_no = 1;
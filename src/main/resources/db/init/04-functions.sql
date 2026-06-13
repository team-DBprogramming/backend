WHENEVER SQLERROR EXIT SQL.SQLCODE;

alter session set container = xepdb1;
alter session set current_schema = backend;

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


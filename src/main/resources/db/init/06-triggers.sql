WHENEVER SQLERROR EXIT SQL.SQLCODE;

alter session set container = xepdb1;
alter session set current_schema = backend;

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
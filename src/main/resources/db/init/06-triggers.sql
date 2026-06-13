WHENEVER SQLERROR EXIT SQL.SQLCODE;

alter session set container = xepdb1;
alter session set current_schema = backend;

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
            '강의평을 작성할 수 없는 수강 상태입니다.'
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

-- 강의평 등록
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

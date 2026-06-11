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
        || nCourseIdNo || '의 수강 해제를 요청하였습니다.'
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
      AND c_no = nCourseIdNo
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
      AND c_no = nCourseIdNo
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
-- 학생 + 과목 + 분반 기준 장바구니 삭제
------------------------------------------------------------
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

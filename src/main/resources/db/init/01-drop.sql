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


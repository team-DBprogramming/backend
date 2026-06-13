-- Compatibility wrapper. Docker Compose runs src/main/resources/db/init/99-dummy-data.sql.
WHENEVER SQLERROR EXIT SQL.SQLCODE;
@@init/99-dummy-data.sql

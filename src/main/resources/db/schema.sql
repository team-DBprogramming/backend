-- Compatibility wrapper. Docker Compose mounts src/main/resources/db/init instead.
WHENEVER SQLERROR EXIT SQL.SQLCODE;
@@init/01-drop.sql
@@init/02-tables.sql
@@init/03-views.sql
@@init/04-functions.sql
@@init/05-procedures.sql
@@init/06-triggers.sql

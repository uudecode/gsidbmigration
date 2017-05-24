1. Подсоединяемся через удалённый десктоп к серверу MyPG.
2. Запускаем программу "pgAdmin III", подключаемся к серверу администратором -
  пользователь "postgres" пароль "a12345".
3. Создаём базу скриптом "database.sql"
4. Перенос схемы PODS:
   4.1 Выполням скрипт "cr_tables_pods.sql" - будут созданы схемы "pods" и "podstemp" с таблицами
         без foreign констрейнтов.
   4.2 Запускаем программу "Navicat Premium", выбираем пункт меню "Tools -> Data Transfer",
    вкладка "General - Source - Connection" выбираем, например,
    "ETALON" (при необходимости создаём новое соединение).
     "Schema" выбираем "PODS", все таблицы выбраны по умолчанию.
    вкладка "General - Target - Connection" выбираем "Postgres_LOCAL",
    Database - ETALONPG,
    Schema - podstemp.
    Вкладка "Advanced"  - должны быть выбраны:
     "insert records", "use hexadecimal format for BLOB" и "Continue on error"
    Жмём "Start"
    4.3 После успешного окончания переноса из Oracle в Postgres запускаем скрипт "ins_data_pods.sql" - данные будут
    перенесены из схемы "podstemp" в схему "pods"
    4.4 Создаём foreign констрейнты - скрипт "cr_const_pods.sql"
    4.5 Удаляем временную схему "podstemp" - скрипт "drop_temp_pods.sql

5. Перенос схемы WEB50:
   5.1 Выполням скрипт "cr_tables_web50.sql" - будут созданы схемы "web50" и "web50temp" с таблицами
         без foreign констрейнтов.
   5.2 Запускаем программу "Navicat Premium", выбираем пункт меню "Tools -> Data Transfer",
    вкладка "General - Source - Connection" выбираем, например,
    "ETALON" (при необходимости создаём новое соединение).
     "Schema" выбираем "WEB50", все таблицы выбраны по умолчанию.
    вкладка "General - Target - Connection" выбираем "Postgres_LOCAL",
    Database - ETALONPG,
    Schema - web50temp.
    Вкладка "Advanced"  - должны быть выбраны:
     "insert records", "use hexadecimal format for BLOB" и "Continue on error"
    Жмём "Start"
    5.3 После успешного окончания переноса из Oracle в Postgres запускаем скрипт "ins_data_web50.sql" - данные будут
    перенесены из схемы "web50temp" в схему "web50"
    5.4 Создаём foreign констрейнты - скрипт "cr_const_web50.sql"
    5.5 Удаляем временную схему "podstemp" - скрипт "drop_temp_web50.sql


Все скрипты находятся в каталогах "D:\Work\PostgreSQL\PODS" и "D:\Work\PostgreSQL\WEB50" соответственно.


1. �������������� ����� �������� ������� � ������� MyPG.
2. ��������� ��������� "pgAdmin III", ������������ � ������� ��������������� -
  ������������ "postgres" ������ "a12345".
3. ������ ���� �������� "database.sql"
4. ������� ����� PODS:
   4.1 �������� ������ "cr_tables_pods.sql" - ����� ������� ����� "pods" � "podstemp" � ���������
         ��� foreign ������������.
   4.2 ��������� ��������� "Navicat Premium", �������� ����� ���� "Tools -> Data Transfer",
    ������� "General - Source - Connection" ��������, ��������,
    "ETALON" (��� ������������� ������ ����� ����������).
     "Schema" �������� "PODS", ��� ������� ������� �� ���������.
    ������� "General - Target - Connection" �������� "Postgres_LOCAL",
    Database - ETALONPG,
    Schema - podstemp.
    ������� "Advanced"  - ������ ���� �������:
     "insert records", "use hexadecimal format for BLOB" � "Continue on error"
    ��� "Start"
    4.3 ����� ��������� ��������� �������� �� Oracle � Postgres ��������� ������ "ins_data_pods.sql" - ������ �����
    ���������� �� ����� "podstemp" � ����� "pods"
    4.4 ������ foreign ����������� - ������ "cr_const_pods.sql"
    4.5 ������� ��������� ����� "podstemp" - ������ "drop_temp_pods.sql

5. ������� ����� WEB50:
   5.1 �������� ������ "cr_tables_web50.sql" - ����� ������� ����� "web50" � "web50temp" � ���������
         ��� foreign ������������.
   5.2 ��������� ��������� "Navicat Premium", �������� ����� ���� "Tools -> Data Transfer",
    ������� "General - Source - Connection" ��������, ��������,
    "ETALON" (��� ������������� ������ ����� ����������).
     "Schema" �������� "WEB50", ��� ������� ������� �� ���������.
    ������� "General - Target - Connection" �������� "Postgres_LOCAL",
    Database - ETALONPG,
    Schema - web50temp.
    ������� "Advanced"  - ������ ���� �������:
     "insert records", "use hexadecimal format for BLOB" � "Continue on error"
    ��� "Start"
    5.3 ����� ��������� ��������� �������� �� Oracle � Postgres ��������� ������ "ins_data_web50.sql" - ������ �����
    ���������� �� ����� "web50temp" � ����� "web50"
    5.4 ������ foreign ����������� - ������ "cr_const_web50.sql"
    5.5 ������� ��������� ����� "podstemp" - ������ "drop_temp_web50.sql


��� ������� ��������� � ��������� "D:\Work\PostgreSQL\PODS" � "D:\Work\PostgreSQL\WEB50" ��������������.


--INSERT INTO level01 (col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid) VALUES ('RandomString1', 1234567890, 12345, 1234, 12, 123.456, 123.45, TRUE, 'A', '2023-01-01', '08:00:00', '2023-01-01 08:00:00', '2023-01-01', '2023-01-01 08:00:00', 123456.789, 1234567890123456789, '550e8400-e29b-41d4-a716-446655440000');

INSERT INTO some_table ( id, name, value) VALUES ( 1, 'Name A', 10);
INSERT INTO some_table ( id, name, value) VALUES ( 2, 'Name B', 50);
INSERT INTO some_table ( id, name, value) VALUES ( 3, 'Name C', 30);
INSERT INTO some_table ( id, name, value) VALUES ( 4, 'Name D', 20);

INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 1, 'RandomString1', 81234567890, 102345, 12324, 19, 923.456, 23.45, FALSE, 'D', '2023-06-01', '09:00:00', '2023-08-20 08:00:00', '2023-08-01', '2023-08-01 04:00:00', 23456.789, 2234567890123456789, '950e8400-e29b-41d4-a716-446655440001', 'ON');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 2, 'ABC',           9234567890,  122345,  1214, 10, 523.456, 523.45, TRUE, 'E', '2023-01-02', '08:00:00', '2023-01-10 09:00:00', '2023-02-01', '2023-01-01 05:00:00', 123456.789, 3234567890123456789, '550e8400-e29b-41d4-a716-446655440002', 'OFF');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 4, 'abc',           1232567890,   52345,  1244, 19, 423.456, 623.45, TRUE, 'F', '2023-01-07', '07:00:00', '2023-01-05 10:00:00', '2023-09-01', '2023-01-01 09:00:00', 12356.789, 1234567890123456789, '550e8400-e29b-41d4-a716-446655440004', 'OFF');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 3, '123',           1234567890,   92345,  1134, 13, 323.456, 723.45, TRUE, '!', '2023-01-12', '06:00:00', '2023-01-01 11:00:00', '2023-12-01', '2023-01-01 08:00:00', 123456.789, 234567890123456789, '550e8400-e29b-41d4-a716-446655440000', 'HIGH');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

INSERT INTO child_table ( name, number, parent_id ) VALUES ( '1A Name', 20, 1);
INSERT INTO child_table ( name, number, parent_id ) VALUES ( '1B Name', 30, 1);
INSERT INTO child_table ( name, number, parent_id ) VALUES ( '1C Name', 30, 1);
INSERT INTO child_table ( name, number, parent_id ) VALUES ( '1D Name', 50, 1);

INSERT INTO child_table ( name, number, parent_id ) VALUES ( '2A Name', 20, 2);
INSERT INTO child_table ( name, number, parent_id ) VALUES ( '2B Name', 31, 2);

INSERT INTO child_table ( name, number, parent_id ) VALUES ( '3A Name', 20, 3);
INSERT INTO child_table ( name, number, parent_id ) VALUES ( '3B Name', 20, 3);
INSERT INTO child_table ( name, number, parent_id ) VALUES ( '3C Name', 31, 3);

INSERT INTO main_table ( description, child_table_id ) VALUES ('Some description', '3C Name');
INSERT INTO main_table ( description, child_table_id ) VALUES ('Some other description', '1A Name');
INSERT INTO main_table ( description, child_table_id ) VALUES ('other description with null child', null);

INSERT INTO gran_child_table ( gran_name, something, child_table_name ) VALUES ( '1A GRAN NAME', 'Nothing1', '1A Name');
INSERT INTO gran_child_table ( gran_name, something, child_table_name ) VALUES ( '1A2 GRAN NAME', 'Nothing2', '1A Name');
INSERT INTO gran_child_table ( gran_name, something, child_table_name ) VALUES ( '1A3 GRAN NAME', 'Nothing3', '1A Name');
INSERT INTO gran_child_table ( gran_name, something, child_table_name ) VALUES ( '1B GRAN NAME', 'Nothing4', '1B Name');

INSERT INTO author (name) VALUES ('J.K. Rowling');

INSERT INTO book (title, author_id, price) VALUES ('Harry Potter and the Sorcerers Stone', 1, 1.2);
INSERT INTO book (title, author_id, price) VALUES ('Harry Potter and the Chamber of Secrets', 1, 2);

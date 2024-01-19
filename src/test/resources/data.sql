
INSERT INTO some_table ( id, name, value) VALUES ( 1, 'Name A', 10);
INSERT INTO some_table ( id, name, value) VALUES ( 2, 'Name B', 50);
INSERT INTO some_table ( id, name, value) VALUES ( 3, 'Name C', 30);
INSERT INTO some_table ( id, name, value) VALUES ( 4, 'Name D', 20);

INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 1, 'RandomString1', 81234567890, 102345, 12324, 19, 923.456, 23.45, FALSE, 'D', '2023-06-01', '09:00:00', '2023-08-20 08:00:00', '2023-08-20 08:00:00', '2023-08-01', '2023-08-01 04:00:00', 23456.789, 2234567890123456789, '950e8400-e29b-41d4-a716-446655440001', 'ON');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 2, 'ABC',           9234567890,  122345,  1214, 10, 523.456, 523.45, TRUE, 'E', '2023-01-02', '08:00:00', '2023-01-10 09:00:00', '2023-01-10 09:00:00', '2023-02-01', '2023-01-01 05:00:00', 123456.789, 3234567890123456789, '550e8400-e29b-41d4-a716-446655440002', 'OFF');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 4, 'abc',           1232567890,   52345,  1244, 19, 423.456, 623.45, TRUE, 'F', '2023-01-07', '07:00:00', '2023-01-05 10:00:00', '2023-01-05 10:00:00', '2023-09-01', '2023-01-01 09:00:00', 12356.789, 1234567890123456789, '550e8400-e29b-41d4-a716-446655440004', 'OFF');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( 3, '123',           1234567890,   92345,  1134, 13, 323.456, 723.45, TRUE, '!', '2023-01-12', '06:00:00', '2023-01-01 11:00:00', '2023-01-01 11:00:00', '2023-12-01', '2023-01-01 08:00:00', 123456.789, 234567890123456789, '550e8400-e29b-41d4-a716-446655440000', 'HIGH');
INSERT INTO parent_table ( some_table_id, col_string, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ( null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

INSERT INTO parent_table (col_string, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, some_table_id, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ('ISO_DATE: 2024-01-19', '2024-01-19', '00:00:00', '2024-01-19 00:00:00', '2024-01-19 00:00:00', '2024-01-19', '2024-01-19 00:00:00', null,1111,111,111,11,111,1111,true,'!',111,111,'110e8400-e29b-41d4-a716-446655440000','HIGH');
INSERT INTO parent_table (col_string, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, some_table_id, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ('ISO_DATETIME: 2024-01-18T15:00:00', '2024-01-18', '15:00:00', '2024-01-18 15:00:00', '2024-01-18 15:00:00', '2024-01-18', '2024-01-18 15:00:00', null,1111,111,111,11,111,1111,true,'!',111,111,'110e8400-e29b-41d4-a716-446655440000','HIGH');
INSERT INTO parent_table (col_string, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, some_table_id, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ('ISO_DATETIME_TZ: 2024-01-17T15:00:00+01:00', '2024-01-17', '17:00:00', '2024-01-17 15:00:00', '2024-01-17 15:00:00', '2024-01-17', '2024-01-17 15:00:00', null,1111,111,111,11,111,1111,true,'!',111,111,'110e8400-e29b-41d4-a716-446655440000','HIGH');
INSERT INTO parent_table (col_string, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, some_table_id, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ('ISO_DATETIME_UTC: 2024-01-16T14:00:00Z', '2024-01-16', '16:00:00', '2024-01-16 14:00:00', '2024-01-16 14:00:00', '2024-01-16', '2024-01-16 14:00:00', null,1111,111,111,11,111,1111,true,'!',111,111,'110e8400-e29b-41d4-a716-446655440000','HIGH');
INSERT INTO parent_table (col_string, col_date, col_time, col_timestamp, col_instant, col_local_date, col_local_date_time, some_table_id, col_long, col_integer, col_short, col_byte, col_double, col_float, col_boolean, col_char, col_big_decimal, col_big_integer, col_uuid, col_status_enum) VALUES ('RFC_1123: Thu, 15 Jan 2024 15:00:00 GMT', '2024-01-15', '15:00:00', '2024-01-15 15:00:00', '2024-01-15 15:00:00', '2024-01-15', '2024-01-15 15:00:00', null,1111,111,111,11,111,1111,true,'!',111,111,'110e8400-e29b-41d4-a716-446655440000','HIGH');

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

INSERT INTO author (id, name, birthday) VALUES (1, 'J.K. Rowling', '1965-07-31');
INSERT INTO author (id, name, birthday) VALUES (2, 'George Orwell', '1903-06-25');
INSERT INTO author (id, name, birthday) VALUES (3, 'Jane Austen', '1775-12-16');
INSERT INTO author (id, name, birthday) VALUES (4, 'Mark Twain', '1835-11-30');
INSERT INTO author (id, name, birthday) VALUES (5, 'Leo Tolstoy', '1828-09-09');

INSERT INTO book (title, author_id, price, published) VALUES ('Harry Potter and the Sorcerer''s Stone', 1, 1.2, '1998-09-01 08:00:00');
INSERT INTO book (title, author_id, price, published) VALUES ('Harry Potter and the Chamber of Secrets', 1, 2, '1998-07-02 18:00:00');

INSERT INTO book (title, author_id, price, published) VALUES ('1984 Redux', 2, 9.89, '1950-06-30 18:27:24');
INSERT INTO book (title, author_id, price, published) VALUES ('Animal Farm Revisited', 2, 9.0, '1942-06-24 02:45:45');
INSERT INTO book (title, author_id, price, published) VALUES ('The Last Days of Oceania', 2, 14.24, '1946-08-15 12:37:47');
INSERT INTO book (title, author_id, price, published) VALUES ('Big Brother''s Return', 2, 13.48, '1954-05-20 19:54:05');
INSERT INTO book (title, author_id, price, published) VALUES ('Tales of Airstrip One', 2, 12.89, '1954-12-22 16:55:37');

INSERT INTO book (title, author_id, price, published) VALUES ('Pride and Perseverance', 3, 5.7, '1817-09-10 00:56:49');
INSERT INTO book (title, author_id, price, published) VALUES ('Elegance and Eloquence', 3, 3.64, '1802-06-27 17:43:42');
INSERT INTO book (title, author_id, price, published) VALUES ('Gardens of Pemberley', 3, 3.86, '1816-07-20 02:53:00');
INSERT INTO book (title, author_id, price, published) VALUES ('Letters from Bath', 3, 5.38, '1802-09-06 10:48:25');
INSERT INTO book (title, author_id, price, published) VALUES ('Whispers of Willowshire', 3, 5.68, '1814-11-28 01:55:24');
INSERT INTO book (title, author_id, price, published) VALUES ('Dancing at Netherfield', 3, 4.7, '1813-04-03 11:55:09');
INSERT INTO book (title, author_id, price, published) VALUES ('Lady''s Choice', 3, 6.42, '1820-04-17 19:41:52');
INSERT INTO book (title, author_id, price, published) VALUES ('Secrets of Sanditon', 3, 6.34, '1816-01-01 19:11:40');

INSERT INTO book (title, author_id, price, published) VALUES ('Adventures on the Mississippi', 4, 2.97, '1881-05-05 01:19:09');
INSERT INTO book (title, author_id, price, published) VALUES ('Tales of Tom Sawyer', 4, 5.13, '1890-02-11 03:46:14');
INSERT INTO book (title, author_id, price, published) VALUES ('The Gold Rush Chronicles', 4, 5.71, '1878-12-21 14:08:41');
INSERT INTO book (title, author_id, price, published) VALUES ('Life on the Missouri', 4, 2.87, '1910-05-04 16:12:08');
INSERT INTO book (title, author_id, price, published) VALUES ('Huckleberry Finn Returns', 4, 7.55, '1905-05-11 13:27:20');
INSERT INTO book (title, author_id, price, published) VALUES ('Steamboat Captain', 4, 3.49, '1904-12-04 20:08:12');
INSERT INTO book (title, author_id, price, published) VALUES ('The Western Pioneer', 4, 7.4, '1909-12-05 11:45:35');
INSERT INTO book (title, author_id, price, published) VALUES ('The River Journey', 4, 6.14, '1893-12-21 06:41:20');
INSERT INTO book (title, author_id, price, published) VALUES ('Letters from Nevada', 4, null, '1894-06-22 11:23:56');
INSERT INTO book (title, author_id, price, published) VALUES ('Anecdotes of the Frontier', 4, 7.13, '1901-11-11 15:13:17');

INSERT INTO book (title, author_id, price, published) VALUES ('War and Peace: The Sequel', 5, 9.28, '1900-03-16 06:17:30');
INSERT INTO book (title, author_id, price, published) VALUES ('Anna Karenina''s Legacy', 5, 11.35, '1879-06-09 14:04:54');
INSERT INTO book (title, author_id, price, published) VALUES ('Tales from Yasnaya Polyana', 5, 10.76, '1910-11-01 18:57:30');
INSERT INTO book (title, author_id, price, published) VALUES ('The Last Czar', 5, 5.96, '1885-10-22 03:59:00');
INSERT INTO book (title, author_id, price, published) VALUES ('Journeys of Ivan Ilyich', 5, 6.68, '1873-09-07 16:20:11');
INSERT INTO book (title, author_id, price, published) VALUES ('The Russian Odyssey', 5, 5.11, '1888-10-26 06:37:50');
INSERT INTO book (title, author_id, price, published) VALUES ('Letters to a Young Poet', 5, 10.5, '1867-07-20 11:45:21');
INSERT INTO book (title, author_id, price, published) VALUES ('The Siege of Sevastopol Revisited', 5, 8.63, '1870-12-17 00:32:52');

delete from number_info;

insert into number_info (digits, value) values ('1', '{"name":"a"}');
insert into number_info (digits, value) values ('12', '{"name":"ab"}');
insert into number_info (digits, value) values ('122', '{"name":"abb"}');
insert into number_info (digits, value) values ('1222', '{"name":"abbb"}');
insert into number_info (digits, value) values ('12222', '{"name":"abbbb"}');
insert into number_info (digits, value) values ('123', '{"name":"abc"}');
insert into number_info (digits, value) values ('1234', '{"name":"abcd"}');
insert into number_info (digits, value) values ('12345', '{"name":"abcde"}');

delete from rate_info;

insert into rate_info (digits, value) values ('1', '{"rate":1.0000}');
insert into rate_info (digits, value) values ('12', '{"rate":1.2000}');
insert into rate_info (digits, value) values ('1222', '{"rate":1.2220}');
insert into rate_info (digits, value) values ('123', '{"rate":1.2300}');
insert into rate_info (digits, value) values ('1234', '{"rate":1.2340}');
insert into rate_info (digits, value) values ('12345', '{"rate":1.2345}');


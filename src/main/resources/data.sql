insert into address_book (branch) values ('Melbourne');
insert into contact (name, address_book_id) values ('Naimish', (select id from address_book where branch = 'Melbourne'));
insert into contact_phone (contact_id, phone_no) select contact_id, '037038348348' from contact where name = 'Naimish';

insert into books (title, author, category) values
('the great gatsby','fitzgerald','fiction'),
('mockingbird','lee','fiction'),
('brief history of time','hawking','science'),
('origin of species','darwin','science'),
('sapiens','harari','history'),
('guns germs steel','diamond','history'),
('1984','orwell','fiction'),
('selfish gene','dawkins','science');

insert into students (student_name, email, joindate) values
('arjun','arjun@mail.com','2021-02-10'),
('sneha','sneha@mail.com','2020-07-18'),
('rahul','rahul@mail.com','2019-03-25'),
('priya','priya@mail.com','2018-11-30'),
('vikram','vikram@mail.com','2022-01-15'),
('ananya','ananya@mail.com','2023-06-05');

insert into issued_books (book_id, student_id, issueDate, returnDate) values
(1,1,date_sub(curdate(), interval 20 day), null),
(2,2,date_sub(curdate(), interval 8 day), null),
(3,3,date_sub(curdate(), interval 25 day), null),
(4,1,date_sub(curdate(), interval 12 day), curdate()),
(5,4,date_sub(curdate(), interval 1000 day), curdate()),
(6,5,date_sub(curdate(), interval 3 day), null),
(7,2,date_sub(curdate(), interval 18 day), null),
(8,6,date_sub(curdate(), interval 2 day), null);
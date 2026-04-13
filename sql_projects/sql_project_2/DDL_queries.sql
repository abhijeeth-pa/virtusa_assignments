Create database library_management_system;

Use library_management_system;

create table books(book_id int primary key auto_increment,title varchar(100) not null,
author varchar(50),category varchar(50));

create table students(student_id int primary key auto_increment,student_name varchar(100) not null,email varchar(100),JoinDate Date);

create  table issued_books(issued_id int primary key auto_increment,book_id int,student_id int,issueDate date,returnDate date,
foreign key(book_id) references books(book_id),foreign key(student_id) references students(student_id));
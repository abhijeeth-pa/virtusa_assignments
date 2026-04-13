-- overdue logic
select s.student_id, s.student_name, b.title, ib.issueDate, datediff(curdate(), ib.issueDate) as days_overdue
from issued_books ib join students s on ib.student_id = s.student_id join books b on ib.book_id = b.book_id
where ib.returnDate is null and datediff(curdate(), ib.issueDate) > 14;

-- most popular books
select b.category, count(*) as total_borrows
from issued_books ib join books b on ib.book_id = b.book_id group by b.category order by total_borrows desc;
 
-- delete inactive users
delete from students where student_id not in (select distinct student_id from issued_books where issueDate >= date_sub(curdate(), interval 3 year));
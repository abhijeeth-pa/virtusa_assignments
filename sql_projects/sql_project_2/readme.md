# Library Management System SQL Project

## Overview
This SQL project models a simple library management system. It stores books, students, and issued book records, then runs queries to find overdue books, popular categories, and inactive users.

## Features
- Creates normalized tables for books, students, and issued books
- Uses foreign keys to connect borrowing records with students and books
- Identifies overdue book issues
- Finds the most popular borrowed book categories
- Detects inactive students based on borrowing history
- Includes sample seed data and CSV output files

## Tools Used
- SQL
- Relational schema design
- DDL commands for database and table creation
- DML inserts for seed data
- SQL joins:
  - `JOIN` between `issued_books` and `students`
  - `JOIN` between `issued_books` and `books`
- SQL date functions:
  - `DATEDIFF()`
  - `CURDATE()`
  - `DATE_SUB()`
- aggregation and grouping with `COUNT()`, `GROUP BY`, and `ORDER BY`

## Files
- `DDL_queries.sql` - schema creation
- `dummy_data.sql` - sample records
- `queries.sql` - reporting and maintenance queries
- CSV files - exported outputs

## Key Queries Included
- overdue book tracking
- most popular books by category
- inactive student cleanup



## How to Use
1. Create the database and tables using `DDL_queries.sql`.
2. Insert records from `dummy_data.sql`.
3. Run the reporting queries from `queries.sql`.
## Query Output Files
The results of important SQL queries are stored as CSV files:
- `most_popular_books.csv`
- `penalities.csv`


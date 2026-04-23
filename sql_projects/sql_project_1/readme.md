# Hospital Management System SQL Project

## Overview
This SQL project models a simple hospital management system and uses analytical queries to generate insights from patient, doctor, appointment, and treatment data.

## Features
- Creates core hospital management tables
- Connects patients, doctors, appointments, and treatments using foreign keys
- Tracks doctor consultations and patient visits
- Calculates treatment revenue by month
- Identifies common diseases and top-performing doctors
- Exports result datasets into CSV files

## Tools Used
- SQL
- Relational database design
- DDL commands: `CREATE DATABASE`, `CREATE TABLE`
- DML and reporting queries
- SQL aggregation: `COUNT()`, `SUM()`
- SQL grouping and sorting: `GROUP BY`, `ORDER BY`
- SQL joins:
  - `JOIN` between `Appointments` and `Treatments` for revenue analysis
- CSV output files for query results

## Files
- `sql_complete_queries.sql` - table creation statements
- `sql_queries.sql` - analytical queries
- `hospital_data_insertion(dummy).sql` - sample data insertion
- CSV files - exported query results

## Key Queries Included
- most consulted doctors
- revenue generated in a month
- most common diseases
- most frequent patients
- doctor performance analysis

## Example Join Used
```sql
select month(appointment_date) as month, sum(t.cost) as revenue
from Appointments a
join Treatments t on a.patient_id = t.patient_id
group by month(appointment_date);
```

## How to Use
1. Create the database and tables using `sql_complete_queries.sql`.
2. Insert sample data from `hospital_data_insertion(dummy).sql`.
3. Run the analytical queries from `sql_queries.sql`.

## Query Output Files
The results of important SQL queries are stored as CSV files:
- `Most_consulted_doctors.csv`
- `most_common_diseases.csv`
- `most_freq_patient.csv`
- `revenue_in_a_month.csv`
- `doctor_performance.csv`



Use  Hospital_management_System;
Create Table Patients( patient_id int primary key,
patient_name varchar(100) ,age int, gender varchar(2));

Create Table Doctors(doctor_id int primary key,
doctor_name varchar(100),
specialization varchar(100));

Create Table Appointments(appointment_id int primary key,
patient_id int,
doctor_id int ,
appointment_date Date,
foreign key(patient_id) references Patients(patient_id),
foreign key(doctor_id) references Doctors(doctor_id));

Create Table Treatments(
treatment_id int primary key,
patient_id int,
diagnosis varchar(50),
cost Decimal(10,3),
foreign key(patient_id) references Patients(patient_id));



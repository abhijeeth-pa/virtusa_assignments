-- most consulted doctors
select doctor_id, count(*) as total_visits
from Appointments group by doctor_id order by total_visits desc;
-- revenue generated in a month
select month(appointment_date) as month,SUM(t.cost) as revenue
from Appointments a join Treatments t on a.patient_id = t.patient_id
group by month(appointment_date);
-- most common diseases
select diagnosis, count(*) as count
from Treatments group by diagnosis order by count desc;
-- most frequent patients
select patient_id, count(*) as visits
from Appointments group by patient_id order by visits desc;
-- doctors performance
select doctor_id, count(*) as patients_handled from Appointments
group by doctor_id order by patients_handled desc;
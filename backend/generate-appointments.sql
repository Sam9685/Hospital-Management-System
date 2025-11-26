-- Generate Sample Past and Future Appointments
-- This script creates appointments in May-July 2025 (past) and after September 18, 2025 (future)

-- First, let's add some more patients for testing
INSERT INTO users (id, name, firstname, lastname, email, username, password_hash, role, gender, birthdate, contact, address, city, state, country, postal_code, blood_group, emergency_contact_name, emergency_contact_num, profile_url, created_at, created_by) VALUES
(21, 'Alice Johnson', 'Alice', 'Johnson', 'alice.johnson@email.com', 'alice.johnson', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PATIENT', 'FEMALE', '1990-05-20', '+1234567830', '123 Elm St', 'Patient City', 'Patient State', 'Patient Country', '12348', 'AB+', 'David Johnson', '+1234567831', 'https://example.com/alice.jpg', CURRENT_TIMESTAMP, 1),
(22, 'Bob Brown', 'Bob', 'Brown', 'bob.brown@email.com', 'bob.brown', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PATIENT', 'MALE', '1987-09-12', '+1234567832', '456 Maple Ave', 'Patient City', 'Patient State', 'Patient Country', '12349', 'O-', 'Carol Brown', '+1234567833', 'https://example.com/bob.jpg', CURRENT_TIMESTAMP, 1),
(23, 'Carol Davis', 'Carol', 'Davis', 'carol.davis@email.com', 'carol.davis', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PATIENT', 'FEMALE', '1993-12-03', '+1234567834', '789 Cedar Rd', 'Patient City', 'Patient State', 'Patient Country', '12350', 'A-', 'Frank Davis', '+1234567835', 'https://example.com/carol.jpg', CURRENT_TIMESTAMP, 1),
(24, 'David Miller', 'David', 'Miller', 'david.miller@email.com', 'david.miller', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PATIENT', 'MALE', '1985-08-15', '+1234567836', '321 Birch St', 'Patient City', 'Patient State', 'Patient Country', '12351', 'B-', 'Grace Miller', '+1234567837', 'https://example.com/david.jpg', CURRENT_TIMESTAMP, 1),
(25, 'Eva Wilson', 'Eva', 'Wilson', 'eva.wilson@email.com', 'eva.wilson', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PATIENT', 'FEMALE', '1991-04-28', '+1234567838', '654 Spruce Ave', 'Patient City', 'Patient State', 'Patient Country', '12352', 'AB-', 'Henry Wilson', '+1234567839', 'https://example.com/eva.jpg', CURRENT_TIMESTAMP, 1);

-- Generate PAST appointments (May-July 2025)
-- These will appear in the "Past Appointments" section

-- May 2025 appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- May 15, 2025 - Dr. John Smith (Cardiologist)
(100, 5, 1, '2025-05-15', '09:00:00', '09:30:00', 'CONSULTATION', 'CONFIRMED', 'Chest pain evaluation', 'Patient reported mild chest discomfort during exercise', 150.00, '2025-05-10 10:00:00', 1),
(101, 18, 1, '2025-05-15', '10:00:00', '10:30:00', 'CONSULTATION', 'CONFIRMED', 'Annual heart checkup', 'Routine cardiovascular examination', 150.00, '2025-05-10 10:30:00', 1),
(102, 19, 1, '2025-05-15', '11:00:00', '11:30:00', 'CONSULTATION', 'CONFIRMED', 'Blood pressure monitoring', 'Follow-up for hypertension management', 150.00, '2025-05-10 11:00:00', 1),

-- May 20, 2025 - Dr. Sarah Johnson (Neurologist)
(103, 6, 2, '2025-05-20', '10:00:00', '10:45:00', 'CONSULTATION', 'CONFIRMED', 'Migraine follow-up', 'Patient reports improvement in migraine frequency', 200.00, '2025-05-15 14:00:00', 1),
(104, 20, 2, '2025-05-20', '11:00:00', '11:45:00', 'CONSULTATION', 'CONFIRMED', 'Headache consultation', 'New patient with chronic headaches', 200.00, '2025-05-15 14:30:00', 1),

-- May 25, 2025 - Dr. Michael Brown (Orthopedist)
(105, 7, 3, '2025-05-25', '08:00:00', '09:00:00', 'CONSULTATION', 'CONFIRMED', 'Knee pain evaluation', 'Post-surgery follow-up - patient doing well', 180.00, '2025-05-20 09:00:00', 1),
(106, 21, 3, '2025-05-25', '09:00:00', '10:00:00', 'CONSULTATION', 'CONFIRMED', 'Back pain assessment', 'Lower back pain evaluation', 180.00, '2025-05-20 09:30:00', 1),

-- June 2025 appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- June 5, 2025 - Dr. Lisa Anderson (Pediatrician)
(107, 5, 4, '2025-06-05', '09:00:00', '09:30:00', 'CONSULTATION', 'CONFIRMED', 'Child wellness check', 'Annual pediatric examination', 120.00, '2025-05-30 10:00:00', 1),
(108, 22, 4, '2025-06-05', '10:00:00', '10:30:00', 'CONSULTATION', 'CONFIRMED', 'Vaccination consultation', 'Routine vaccination schedule', 120.00, '2025-05-30 10:30:00', 1),

-- June 10, 2025 - Dr. David Wilson (Dermatologist)
(109, 6, 5, '2025-06-10', '10:00:00', '10:30:00', 'CONSULTATION', 'CONFIRMED', 'Skin rash evaluation', 'Patient concerned about skin irritation', 160.00, '2025-06-05 11:00:00', 1),
(110, 23, 5, '2025-06-10', '11:00:00', '11:30:00', 'CONSULTATION', 'CONFIRMED', 'Mole examination', 'Annual skin cancer screening', 160.00, '2025-06-05 11:30:00', 1),

-- June 15, 2025 - Dr. Maria Rodriguez (Gynecologist)
(111, 7, 6, '2025-06-15', '09:00:00', '09:45:00', 'CONSULTATION', 'CONFIRMED', 'Annual gynecological exam', 'Routine women\'s health checkup', 140.00, '2025-06-10 10:00:00', 1),
(112, 24, 6, '2025-06-15', '10:00:00', '10:45:00', 'CONSULTATION', 'CONFIRMED', 'Pregnancy consultation', 'First trimester checkup', 140.00, '2025-06-10 10:30:00', 1),

-- July 2025 appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- July 8, 2025 - Dr. James Taylor (General Surgeon)
(113, 5, 7, '2025-07-08', '08:00:00', '09:00:00', 'CONSULTATION', 'CONFIRMED', 'Post-surgery follow-up', 'Gallbladder removal follow-up', 200.00, '2025-07-03 09:00:00', 1),
(114, 18, 7, '2025-07-08', '09:00:00', '10:00:00', 'CONSULTATION', 'CONFIRMED', 'Surgical consultation', 'Appendectomy consultation', 200.00, '2025-07-03 09:30:00', 1),

-- July 12, 2025 - Dr. Emily Chen (Cardiologist)
(115, 19, 8, '2025-07-12', '08:30:00', '09:00:00', 'CONSULTATION', 'CONFIRMED', 'Heart rhythm evaluation', 'ECG and heart monitoring', 160.00, '2025-07-07 10:00:00', 1),
(116, 20, 8, '2025-07-12', '09:30:00', '10:00:00', 'CONSULTATION', 'CONFIRMED', 'Cardiac stress test', 'Exercise stress test evaluation', 160.00, '2025-07-07 10:30:00', 1),

-- July 18, 2025 - Dr. Robert Kim (Neurologist)
(117, 21, 9, '2025-07-18', '09:00:00', '09:45:00', 'CONSULTATION', 'CONFIRMED', 'Neurological examination', 'Comprehensive neurological assessment', 220.00, '2025-07-13 11:00:00', 1),
(118, 22, 9, '2025-07-18', '10:00:00', '10:45:00', 'CONSULTATION', 'CONFIRMED', 'Memory assessment', 'Cognitive function evaluation', 220.00, '2025-07-13 11:30:00', 1),

-- July 25, 2025 - Dr. Jennifer Lee (Orthopedist)
(119, 23, 10, '2025-07-25', '09:00:00', '10:00:00', 'CONSULTATION', 'CONFIRMED', 'Sports injury evaluation', 'Ankle sprain assessment', 190.00, '2025-07-20 12:00:00', 1),
(120, 24, 10, '2025-07-25', '10:00:00', '11:00:00', 'CONSULTATION', 'CONFIRMED', 'Joint pain consultation', 'Shoulder pain evaluation', 190.00, '2025-07-20 12:30:00', 1);

-- Generate FUTURE appointments (after September 18, 2025)
-- These will appear in the "Upcoming Appointments" section

-- September 2025 appointments (after 18th)
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- September 20, 2025 - Dr. John Smith (Cardiologist)
(121, 5, 1, '2025-09-20', '09:00:00', '09:30:00', 'CONSULTATION', 'SCHEDULED', 'Follow-up consultation', 'Post-treatment follow-up', 150.00, '2025-09-15 10:00:00', 1),
(122, 18, 1, '2025-09-20', '10:00:00', '10:30:00', 'CONSULTATION', 'SCHEDULED', 'Cardiac monitoring', 'Regular heart health check', 150.00, '2025-09-15 10:30:00', 1),
(123, 19, 1, '2025-09-20', '11:00:00', '11:30:00', 'CONSULTATION', 'SCHEDULED', 'Blood pressure check', 'Hypertension management', 150.00, '2025-09-15 11:00:00', 1),

-- September 22, 2025 - Dr. Sarah Johnson (Neurologist)
(124, 6, 2, '2025-09-22', '10:00:00', '10:45:00', 'CONSULTATION', 'SCHEDULED', 'Migraine follow-up', 'Treatment progress review', 200.00, '2025-09-17 14:00:00', 1),
(125, 20, 2, '2025-09-22', '11:00:00', '11:45:00', 'CONSULTATION', 'SCHEDULED', 'Headache management', 'Chronic headache treatment', 200.00, '2025-09-17 14:30:00', 1),

-- September 25, 2025 - Dr. Michael Brown (Orthopedist)
(126, 7, 3, '2025-09-25', '08:00:00', '09:00:00', 'CONSULTATION', 'SCHEDULED', 'Knee rehabilitation', 'Post-surgery recovery check', 180.00, '2025-09-20 09:00:00', 1),
(127, 21, 3, '2025-09-25', '09:00:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Back pain treatment', 'Lower back pain management', 180.00, '2025-09-20 09:30:00', 1),

-- October 2025 appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- October 3, 2025 - Dr. Lisa Anderson (Pediatrician)
(128, 5, 4, '2025-10-03', '09:00:00', '09:30:00', 'CONSULTATION', 'SCHEDULED', 'Child development check', 'Growth and development assessment', 120.00, '2025-09-28 10:00:00', 1),
(129, 22, 4, '2025-10-03', '10:00:00', '10:30:00', 'CONSULTATION', 'SCHEDULED', 'Immunization update', 'Vaccination schedule review', 120.00, '2025-09-28 10:30:00', 1),

-- October 8, 2025 - Dr. David Wilson (Dermatologist)
(130, 6, 5, '2025-10-08', '10:00:00', '10:30:00', 'CONSULTATION', 'SCHEDULED', 'Skin condition follow-up', 'Dermatitis treatment review', 160.00, '2025-10-03 11:00:00', 1),
(131, 23, 5, '2025-10-08', '11:00:00', '11:30:00', 'CONSULTATION', 'SCHEDULED', 'Annual skin screening', 'Comprehensive skin examination', 160.00, '2025-10-03 11:30:00', 1),

-- October 12, 2025 - Dr. Maria Rodriguez (Gynecologist)
(132, 7, 6, '2025-10-12', '09:00:00', '09:45:00', 'CONSULTATION', 'SCHEDULED', 'Pregnancy follow-up', 'Second trimester checkup', 140.00, '2025-10-07 10:00:00', 1),
(133, 24, 6, '2025-10-12', '10:00:00', '10:45:00', 'CONSULTATION', 'SCHEDULED', 'Women\'s health consultation', 'Routine gynecological exam', 140.00, '2025-10-07 10:30:00', 1),

-- October 18, 2025 - Dr. James Taylor (General Surgeon)
(134, 5, 7, '2025-10-18', '08:00:00', '09:00:00', 'CONSULTATION', 'SCHEDULED', 'Surgical follow-up', 'Post-operative care review', 200.00, '2025-10-13 09:00:00', 1),
(135, 18, 7, '2025-10-18', '09:00:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Pre-surgery consultation', 'Appendectomy preparation', 200.00, '2025-10-13 09:30:00', 1),

-- October 22, 2025 - Dr. Emily Chen (Cardiologist)
(136, 19, 8, '2025-10-22', '08:30:00', '09:00:00', 'CONSULTATION', 'SCHEDULED', 'Cardiac function test', 'Echocardiogram review', 160.00, '2025-10-17 10:00:00', 1),
(137, 20, 8, '2025-10-22', '09:30:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Heart health monitoring', 'Cardiovascular assessment', 160.00, '2025-10-17 10:30:00', 1),

-- October 28, 2025 - Dr. Robert Kim (Neurologist)
(138, 21, 9, '2025-10-28', '09:00:00', '09:45:00', 'CONSULTATION', 'SCHEDULED', 'Neurological follow-up', 'Treatment progress evaluation', 220.00, '2025-10-23 11:00:00', 1),
(139, 22, 9, '2025-10-28', '10:00:00', '10:45:00', 'CONSULTATION', 'SCHEDULED', 'Cognitive assessment', 'Memory function evaluation', 220.00, '2025-10-23 11:30:00', 1),

-- November 2025 appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- November 5, 2025 - Dr. Jennifer Lee (Orthopedist)
(140, 23, 10, '2025-11-05', '09:00:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Sports injury follow-up', 'Ankle rehabilitation progress', 190.00, '2025-10-31 12:00:00', 1),
(141, 24, 10, '2025-11-05', '10:00:00', '11:00:00', 'CONSULTATION', 'SCHEDULED', 'Joint pain management', 'Shoulder treatment review', 190.00, '2025-10-31 12:30:00', 1),

-- November 10, 2025 - Dr. Christopher Wang (Pediatrician)
(142, 5, 11, '2025-11-10', '08:00:00', '08:30:00', 'CONSULTATION', 'SCHEDULED', 'Child wellness check', 'Regular pediatric examination', 130.00, '2025-11-05 10:00:00', 1),
(143, 22, 11, '2025-11-10', '09:00:00', '09:30:00', 'CONSULTATION', 'SCHEDULED', 'Growth monitoring', 'Height and weight assessment', 130.00, '2025-11-05 10:30:00', 1),

-- November 15, 2025 - Dr. Amanda Garcia (Dermatologist)
(144, 6, 12, '2025-11-15', '09:30:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Skin treatment follow-up', 'Acne treatment review', 170.00, '2025-11-10 11:00:00', 1),
(145, 23, 12, '2025-11-15', '10:30:00', '11:00:00', 'CONSULTATION', 'SCHEDULED', 'Cosmetic consultation', 'Skin rejuvenation discussion', 170.00, '2025-11-10 11:30:00', 1),

-- November 20, 2025 - Dr. John Smith (Cardiologist)
(146, 5, 1, '2025-11-20', '09:00:00', '09:30:00', 'CONSULTATION', 'SCHEDULED', 'Cardiac stress test', 'Exercise stress test', 150.00, '2025-11-15 10:00:00', 1),
(147, 18, 1, '2025-11-20', '10:00:00', '10:30:00', 'CONSULTATION', 'SCHEDULED', 'Heart health check', 'Annual cardiovascular exam', 150.00, '2025-11-15 10:30:00', 1),

-- November 25, 2025 - Dr. Sarah Johnson (Neurologist)
(148, 6, 2, '2025-11-25', '10:00:00', '10:45:00', 'CONSULTATION', 'SCHEDULED', 'Migraine management', 'Treatment optimization', 200.00, '2025-11-20 14:00:00', 1),
(149, 20, 2, '2025-11-25', '11:00:00', '11:45:00', 'CONSULTATION', 'SCHEDULED', 'Headache therapy', 'Chronic headache treatment', 200.00, '2025-11-20 14:30:00', 1),

-- December 2025 appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, created_at, created_by) VALUES
-- December 3, 2025 - Dr. Michael Brown (Orthopedist)
(150, 7, 3, '2025-12-03', '08:00:00', '09:00:00', 'CONSULTATION', 'SCHEDULED', 'Knee recovery check', 'Post-surgery rehabilitation', 180.00, '2025-11-28 09:00:00', 1),
(151, 21, 3, '2025-12-03', '09:00:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Back pain treatment', 'Lower back therapy', 180.00, '2025-11-28 09:30:00', 1),

-- December 8, 2025 - Dr. Lisa Anderson (Pediatrician)
(152, 5, 4, '2025-12-08', '09:00:00', '09:30:00', 'CONSULTATION', 'SCHEDULED', 'Child health check', 'Regular pediatric visit', 120.00, '2025-12-03 10:00:00', 1),
(153, 22, 4, '2025-12-08', '10:00:00', '10:30:00', 'CONSULTATION', 'SCHEDULED', 'Vaccination update', 'Immunization schedule', 120.00, '2025-12-03 10:30:00', 1),

-- December 12, 2025 - Dr. David Wilson (Dermatologist)
(154, 6, 5, '2025-12-12', '10:00:00', '10:30:00', 'CONSULTATION', 'SCHEDULED', 'Skin condition review', 'Dermatitis follow-up', 160.00, '2025-12-07 11:00:00', 1),
(155, 23, 5, '2025-12-12', '11:00:00', '11:30:00', 'CONSULTATION', 'SCHEDULED', 'Annual skin exam', 'Comprehensive skin screening', 160.00, '2025-12-07 11:30:00', 1),

-- December 18, 2025 - Dr. Maria Rodriguez (Gynecologist)
(156, 7, 6, '2025-12-18', '09:00:00', '09:45:00', 'CONSULTATION', 'SCHEDULED', 'Pregnancy monitoring', 'Third trimester checkup', 140.00, '2025-12-13 10:00:00', 1),
(157, 24, 6, '2025-12-18', '10:00:00', '10:45:00', 'CONSULTATION', 'SCHEDULED', 'Women\'s health check', 'Annual gynecological exam', 140.00, '2025-12-13 10:30:00', 1),

-- December 22, 2025 - Dr. James Taylor (General Surgeon)
(158, 5, 7, '2025-12-22', '08:00:00', '09:00:00', 'CONSULTATION', 'SCHEDULED', 'Surgical consultation', 'Pre-operative assessment', 200.00, '2025-12-17 09:00:00', 1),
(159, 18, 7, '2025-12-22', '09:00:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Post-surgery follow-up', 'Recovery progress review', 200.00, '2025-12-17 09:30:00', 1),

-- December 28, 2025 - Dr. Emily Chen (Cardiologist)
(160, 19, 8, '2025-12-28', '08:30:00', '09:00:00', 'CONSULTATION', 'SCHEDULED', 'Cardiac monitoring', 'Heart function assessment', 160.00, '2025-12-23 10:00:00', 1),
(161, 20, 8, '2025-12-28', '09:30:00', '10:00:00', 'CONSULTATION', 'SCHEDULED', 'Heart health review', 'Cardiovascular checkup', 160.00, '2025-12-23 10:30:00', 1);

-- Add some cancelled appointments for testing
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, end_time, appointment_type, status, symptoms, notes, consultation_fee, cancellation_reason, cancelled_at, created_at, created_by) VALUES
-- Cancelled appointment in May 2025
(162, 25, 1, '2025-05-30', '09:00:00', '09:30:00', 'CONSULTATION', 'CANCELLED', 'Chest pain evaluation', 'Patient cancelled due to emergency', 150.00, 'Family emergency', '2025-05-28 15:00:00', '2025-05-25 10:00:00', 1),

-- Cancelled appointment in June 2025
(163, 25, 2, '2025-06-12', '10:00:00', '10:45:00', 'CONSULTATION', 'CANCELLED', 'Headache consultation', 'Patient rescheduled', 200.00, 'Schedule conflict', '2025-06-10 12:00:00', '2025-06-08 14:00:00', 1),

-- Cancelled future appointment
(164, 25, 3, '2025-10-05', '08:00:00', '09:00:00', 'CONSULTATION', 'CANCELLED', 'Back pain evaluation', 'Patient cancelled appointment', 180.00, 'Found alternative treatment', '2025-10-03 16:00:00', '2025-09-30 11:00:00', 1);

-- Update the sequence for appointments table
SELECT setval('appointments_id_seq', 164);

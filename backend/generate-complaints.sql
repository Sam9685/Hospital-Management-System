-- Generate test complaint data for super admin testing
-- This script creates various complaints with different statuses and priorities

-- Insert test complaints with different statuses and priorities
INSERT INTO complaints (
    patient_id, 
    appointment_id, 
    category, 
    title, 
    description, 
    contact_preference, 
    priority, 
    status, 
    created_at, 
    created_by
) VALUES 
-- Open complaints
(1, 1, 'TREATMENT_ISSUE', 'Doctor was late for appointment', 'The doctor arrived 30 minutes late for my scheduled appointment, causing inconvenience and delay in my treatment.', 'EMAIL', 'MEDIUM', 'OPEN', NOW(), 1),

(2, 2, 'SERVICE_ISSUE', 'Poor customer service at reception', 'The reception staff was rude and unhelpful when I tried to reschedule my appointment. They did not provide clear information about available slots.', 'CALL', 'HIGH', 'OPEN', NOW(), 2),

(3, 3, 'BILLING_ISSUE', 'Incorrect billing amount charged', 'I was charged double the consultation fee mentioned during booking. The billing department is not responding to my calls.', 'EMAIL', 'HIGH', 'OPEN', NOW(), 3),

-- In Progress complaints
(4, 4, 'STAFF_ISSUE', 'Nurse was unprofessional during treatment', 'The nurse assigned to my treatment was unprofessional and made inappropriate comments. This affected my overall experience.', 'SMS', 'MEDIUM', 'IN_PROGRESS', NOW(), 4),

(5, 5, 'FACILITY_ISSUE', 'Air conditioning not working in waiting area', 'The air conditioning in the waiting area has been broken for weeks, making it very uncomfortable for patients.', 'EMAIL', 'LOW', 'IN_PROGRESS', NOW(), 5),

-- Resolved complaints
(6, 6, 'TREATMENT_ISSUE', 'Medication side effects not explained', 'I experienced side effects from the prescribed medication that were not explained to me beforehand. Need better communication about potential risks.', 'CALL', 'MEDIUM', 'RESOLVED', NOW(), 6),

(7, 7, 'SERVICE_ISSUE', 'Long waiting time for lab results', 'Lab results took 5 days instead of the promised 2 days. This delayed my treatment plan.', 'EMAIL', 'MEDIUM', 'RESOLVED', NOW(), 7),

-- Closed complaints
(8, 8, 'BILLING_ISSUE', 'Insurance claim processing delay', 'My insurance claim has been pending for over a month without any updates from the billing department.', 'EMAIL', 'HIGH', 'CLOSED', NOW(), 8),

(9, 9, 'STAFF_ISSUE', 'Doctor did not listen to my concerns', 'The doctor seemed rushed and did not properly listen to my symptoms and concerns during the consultation.', 'CALL', 'MEDIUM', 'CLOSED', NOW(), 9),

(10, 10, 'FACILITY_ISSUE', 'Parking space shortage', 'There are not enough parking spaces available, especially during peak hours. Had to park far away and walk.', 'SMS', 'LOW', 'CLOSED', NOW(), 10),

-- Critical priority complaint
(1, 11, 'TREATMENT_ISSUE', 'Wrong diagnosis provided', 'I believe I was given an incorrect diagnosis which led to unnecessary treatment and additional costs.', 'CALL', 'CRITICAL', 'OPEN', NOW(), 1),

-- Reopened complaint
(2, 12, 'SERVICE_ISSUE', 'Previous complaint not properly addressed', 'My previous complaint about billing was marked as resolved but the issue still persists. Need to reopen this case.', 'EMAIL', 'HIGH', 'REOPENED', NOW(), 2),

-- Complaint without appointment (general complaint)
(3, NULL, 'OTHER', 'General feedback about hospital services', 'Overall experience at the hospital has been declining. Need better coordination between departments and improved patient care.', 'EMAIL', 'MEDIUM', 'OPEN', NOW(), 3),

-- Multiple complaints from same patient
(1, 13, 'BILLING_ISSUE', 'Duplicate charges on credit card', 'I noticed duplicate charges on my credit card statement for the same consultation fee.', 'CALL', 'HIGH', 'OPEN', NOW(), 1),

(1, 14, 'FACILITY_ISSUE', 'Cleanliness issues in restroom', 'The patient restroom on the second floor was not clean and lacked basic amenities like soap and paper towels.', 'EMAIL', 'LOW', 'IN_PROGRESS', NOW(), 1);

-- Update timestamps to show variety in creation dates
UPDATE complaints SET created_at = NOW() - INTERVAL '1 day' WHERE complaint_id IN (1, 2, 3);
UPDATE complaints SET created_at = NOW() - INTERVAL '3 days' WHERE complaint_id IN (4, 5);
UPDATE complaints SET created_at = NOW() - INTERVAL '7 days' WHERE complaint_id IN (6, 7);
UPDATE complaints SET created_at = NOW() - INTERVAL '14 days' WHERE complaint_id IN (8, 9, 10);
UPDATE complaints SET created_at = NOW() - INTERVAL '21 days' WHERE complaint_id IN (11, 12, 13, 14);

-- Add some resolution notes for resolved and closed complaints
UPDATE complaints SET 
    resolution_notes = 'Doctor apologized for the delay and provided additional consultation time. Staff scheduling has been improved.',
    resolution = 'Implemented better scheduling system to prevent delays. Doctor provided free follow-up consultation.',
    updated_at = NOW() - INTERVAL '5 days'
WHERE complaint_id = 6;

UPDATE complaints SET 
    resolution_notes = 'Lab processing time has been reduced to 1-2 days. New equipment installed.',
    resolution = 'Lab efficiency improved with new equipment. Standard processing time now 1-2 days.',
    updated_at = NOW() - INTERVAL '4 days'
WHERE complaint_id = 7;

UPDATE complaints SET 
    resolution_notes = 'Insurance claim processed and payment received. Billing process streamlined.',
    resolution = 'Insurance claim resolved. New billing system implemented for faster processing.',
    updated_at = NOW() - INTERVAL '10 days'
WHERE complaint_id = 8;

UPDATE complaints SET 
    resolution_notes = 'Doctor provided additional consultation time to address all concerns.',
    resolution = 'Doctor scheduling improved to allow more time per patient. Training provided on patient communication.',
    updated_at = NOW() - INTERVAL '12 days'
WHERE complaint_id = 9;

UPDATE complaints SET 
    resolution_notes = 'Additional parking spaces allocated. Valet service introduced.',
    resolution = 'Parking capacity increased by 50%. Valet parking service now available.',
    updated_at = NOW() - INTERVAL '15 days'
WHERE complaint_id = 10;

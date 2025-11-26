-- Populate complaints table with test data for super admin user
-- This script creates various complaints with different statuses, priorities, and categories

-- First, let's ensure we have a super admin user (assuming user ID 1 is super admin)
-- If not, we'll create complaints for existing users

-- Insert comprehensive test complaints for super admin testing
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
    created_by,
    updated_at,
    resolution,
    resolution_notes,
    assigned_to_id
) VALUES 
-- OPEN COMPLAINTS (Current issues)
(1, 1, 'TREATMENT_ISSUE', 'Doctor was late for appointment', 'The doctor arrived 30 minutes late for my scheduled appointment, causing inconvenience and delay in my treatment. This affected my entire day schedule.', 'EMAIL', 'MEDIUM', 'OPEN', NOW() - INTERVAL '2 days', 1, NOW() - INTERVAL '1 day', NULL, NULL, NULL),

(1, 2, 'SERVICE_ISSUE', 'Poor customer service at reception', 'The reception staff was rude and unhelpful when I tried to reschedule my appointment. They did not provide clear information about available slots and seemed disinterested in helping.', 'CALL', 'HIGH', 'OPEN', NOW() - INTERVAL '3 days', 1, NOW() - INTERVAL '2 days', NULL, NULL, NULL),

(1, 3, 'BILLING_ISSUE', 'Incorrect billing amount charged', 'I was charged double the consultation fee mentioned during booking. The billing department is not responding to my calls and emails regarding this matter.', 'EMAIL', 'HIGH', 'OPEN', NOW() - INTERVAL '1 day', 1, NOW() - INTERVAL '12 hours', NULL, NULL, NULL),

(1, 4, 'STAFF_ISSUE', 'Nurse was unprofessional during treatment', 'The nurse assigned to my treatment was unprofessional and made inappropriate comments. This affected my overall experience and made me feel uncomfortable.', 'SMS', 'MEDIUM', 'OPEN', NOW() - INTERVAL '4 days', 1, NOW() - INTERVAL '3 days', NULL, NULL, NULL),

(1, 5, 'FACILITY_ISSUE', 'Air conditioning not working in waiting area', 'The air conditioning in the waiting area has been broken for weeks, making it very uncomfortable for patients, especially elderly ones.', 'EMAIL', 'LOW', 'OPEN', NOW() - INTERVAL '5 days', 1, NOW() - INTERVAL '4 days', NULL, NULL, NULL),

-- IN PROGRESS COMPLAINTS (Being addressed)
(1, 6, 'TREATMENT_ISSUE', 'Medication side effects not explained', 'I experienced side effects from the prescribed medication that were not explained to me beforehand. Need better communication about potential risks and side effects.', 'CALL', 'MEDIUM', 'IN_PROGRESS', NOW() - INTERVAL '7 days', 1, NOW() - INTERVAL '2 days', NULL, NULL, 2),

(1, 7, 'SERVICE_ISSUE', 'Long waiting time for lab results', 'Lab results took 5 days instead of the promised 2 days. This delayed my treatment plan and caused unnecessary anxiety.', 'EMAIL', 'MEDIUM', 'IN_PROGRESS', NOW() - INTERVAL '6 days', 1, NOW() - INTERVAL '1 day', NULL, NULL, 2),

(1, 8, 'BILLING_ISSUE', 'Insurance claim processing delay', 'My insurance claim has been pending for over a month without any updates from the billing department. This is causing financial stress.', 'EMAIL', 'HIGH', 'IN_PROGRESS', NOW() - INTERVAL '8 days', 1, NOW() - INTERVAL '3 days', NULL, NULL, 3),

(1, 9, 'STAFF_ISSUE', 'Doctor did not listen to my concerns', 'The doctor seemed rushed and did not properly listen to my symptoms and concerns during the consultation. Felt dismissed and unheard.', 'CALL', 'MEDIUM', 'IN_PROGRESS', NOW() - INTERVAL '9 days', 1, NOW() - INTERVAL '4 days', NULL, NULL, 2),

(1, 10, 'FACILITY_ISSUE', 'Parking space shortage', 'There are not enough parking spaces available, especially during peak hours. Had to park far away and walk, which is difficult for elderly patients.', 'SMS', 'LOW', 'IN_PROGRESS', NOW() - INTERVAL '10 days', 1, NOW() - INTERVAL '5 days', NULL, NULL, 3),

-- RESOLVED COMPLAINTS (Successfully addressed)
(1, 11, 'TREATMENT_ISSUE', 'Wrong diagnosis provided', 'I believe I was given an incorrect diagnosis which led to unnecessary treatment and additional costs. This needs immediate attention.', 'CALL', 'CRITICAL', 'RESOLVED', NOW() - INTERVAL '15 days', 1, NOW() - INTERVAL '5 days', 'Issue has been thoroughly investigated. The diagnosis was correct, but we understand your concerns. Additional consultation provided free of charge.', 'Complaint investigated by senior medical staff. Diagnosis confirmed as accurate. Patient provided with detailed explanation and free follow-up consultation.', 2),

(1, 12, 'SERVICE_ISSUE', 'Previous complaint not properly addressed', 'My previous complaint about billing was marked as resolved but the issue still persists. Need to reopen this case and get proper resolution.', 'EMAIL', 'HIGH', 'RESOLVED', NOW() - INTERVAL '20 days', 1, NOW() - INTERVAL '8 days', 'Billing issue has been completely resolved. Refund processed and new billing system implemented to prevent future occurrences.', 'Complaint escalated to billing manager. Full refund processed. New billing procedures implemented with additional verification steps.', 3),

(1, 13, 'BILLING_ISSUE', 'Duplicate charges on credit card', 'I noticed duplicate charges on my credit card statement for the same consultation fee. This needs immediate resolution.', 'CALL', 'HIGH', 'RESOLVED', NOW() - INTERVAL '18 days', 1, NOW() - INTERVAL '6 days', 'Duplicate charges identified and refunded immediately. New payment processing system implemented to prevent future occurrences.', 'Payment system error identified and fixed. Duplicate charges refunded within 24 hours. Additional monitoring systems put in place.', 3),

(1, 14, 'STAFF_ISSUE', 'Security guard was intimidating', 'The security guard at the entrance was unnecessarily intimidating and made me feel uncomfortable. This creates a negative first impression.', 'SMS', 'LOW', 'RESOLVED', NOW() - INTERVAL '25 days', 1, NOW() - INTERVAL '10 days', 'Security guard has been retrained on customer service protocols. Additional training provided to all security staff.', 'Security staff training updated. Customer service protocols reinforced. Regular monitoring implemented to ensure professional behavior.', 2),

(1, 15, 'FACILITY_ISSUE', 'Elevator out of order', 'The main elevator has been out of order for 3 days, making it difficult for elderly patients to access upper floors.', 'EMAIL', 'MEDIUM', 'RESOLVED', NOW() - INTERVAL '30 days', 1, NOW() - INTERVAL '12 days', 'Elevator has been repaired and is now fully operational. Regular maintenance schedule implemented to prevent future breakdowns.', 'Elevator repaired within 48 hours. Preventive maintenance schedule established. Backup elevator service arranged for future incidents.', 3),

-- CLOSED COMPLAINTS (Completed cases)
(1, 16, 'TREATMENT_ISSUE', 'Follow-up appointment not scheduled', 'The doctor promised to schedule a follow-up appointment but I never received any communication about it.', 'EMAIL', 'MEDIUM', 'CLOSED', NOW() - INTERVAL '35 days', 1, NOW() - INTERVAL '15 days', 'Follow-up appointment scheduling system improved. Patient contacted and appointment scheduled. New automated reminder system implemented.', 'Follow-up scheduling process streamlined. Patient contacted immediately. Automated reminder system implemented for all future appointments.', 2),

(1, 17, 'SERVICE_ISSUE', 'Online portal not working', 'The patient portal is not working properly and I cannot access my medical records or test results.', 'EMAIL', 'HIGH', 'CLOSED', NOW() - INTERVAL '40 days', 1, NOW() - INTERVAL '18 days', 'Patient portal has been upgraded and is now fully functional. All patients have been notified of the improvements.', 'Portal upgraded with new features. Technical issues resolved. User training materials provided. All patients notified of improvements.', 3),

(1, 18, 'BILLING_ISSUE', 'Payment method not accepted', 'The hospital payment system rejected my valid credit card multiple times, causing delays in treatment.', 'CALL', 'MEDIUM', 'CLOSED', NOW() - INTERVAL '45 days', 1, NOW() - INTERVAL '20 days', 'Payment system updated and tested. Multiple payment options now available. Technical issues resolved.', 'Payment processing system upgraded. Multiple payment methods added. System tested and verified working correctly.', 3),

(1, 19, 'STAFF_ISSUE', 'Receptionist was unhelpful', 'The receptionist was unhelpful and provided incorrect information about appointment scheduling.', 'SMS', 'LOW', 'CLOSED', NOW() - INTERVAL '50 days', 1, NOW() - INTERVAL '25 days', 'Reception staff retrained on appointment procedures. New information system implemented for accurate scheduling.', 'Reception staff training completed. New scheduling system implemented. Regular quality checks established.', 2),

(1, 20, 'FACILITY_ISSUE', 'Waiting room chairs uncomfortable', 'The chairs in the waiting room are very uncomfortable and cause back pain during long waits.', 'EMAIL', 'LOW', 'CLOSED', NOW() - INTERVAL '55 days', 1, NOW() - INTERVAL '30 days', 'New comfortable chairs installed in waiting room. Seating arrangement improved for better patient comfort.', 'Waiting room furniture upgraded. New ergonomic chairs installed. Seating capacity increased for better patient flow.', 3),

-- REOPENED COMPLAINT (Previously resolved but issue persists)
(1, 21, 'OTHER', 'General feedback about hospital services', 'Overall experience at the hospital has been declining. Need better coordination between departments and improved patient care.', 'EMAIL', 'MEDIUM', 'REOPENED', NOW() - INTERVAL '60 days', 1, NOW() - INTERVAL '5 days', 'Previous resolution was insufficient. New comprehensive improvement plan implemented across all departments.', 'Complaint reopened due to insufficient previous resolution. Comprehensive improvement plan developed and implemented.', 2),

-- Additional variety complaints
(1, 22, 'TREATMENT_ISSUE', 'Medication dosage unclear', 'The medication dosage instructions were unclear and confusing. Need clearer communication about how to take medications.', 'CALL', 'MEDIUM', 'OPEN', NOW() - INTERVAL '1 day', 1, NOW() - INTERVAL '6 hours', NULL, NULL, NULL),

(1, 23, 'SERVICE_ISSUE', 'Appointment reminder not received', 'I did not receive any appointment reminder and missed my scheduled visit. This caused inconvenience and delays.', 'EMAIL', 'MEDIUM', 'IN_PROGRESS', NOW() - INTERVAL '3 days', 1, NOW() - INTERVAL '1 day', NULL, NULL, 2),

(1, 24, 'BILLING_ISSUE', 'Receipt not provided', 'I was not provided with a proper receipt for my payment. Need proper documentation for insurance claims.', 'SMS', 'LOW', 'RESOLVED', NOW() - INTERVAL '12 days', 1, NOW() - INTERVAL '4 days', 'Receipt system improved. Digital receipts now automatically sent. Paper receipts available on request.', 'Receipt generation system upgraded. Digital receipts implemented. Staff trained on new procedures.', 3),

(1, 25, 'STAFF_ISSUE', 'Doctor seemed distracted', 'The doctor seemed distracted during consultation and did not give full attention to my concerns.', 'CALL', 'MEDIUM', 'CLOSED', NOW() - INTERVAL '28 days', 1, NOW() - INTERVAL '14 days', 'Doctor scheduling improved to allow adequate consultation time. Additional training provided on patient focus.', 'Doctor scheduling optimized. Consultation time increased. Training provided on patient engagement.', 2);

-- Update some complaints to have different creation times for better testing
UPDATE complaints SET created_at = NOW() - INTERVAL '1 hour' WHERE complaint_id IN (1, 2, 3);
UPDATE complaints SET created_at = NOW() - INTERVAL '2 hours' WHERE complaint_id IN (4, 5);
UPDATE complaints SET created_at = NOW() - INTERVAL '3 hours' WHERE complaint_id IN (6, 7, 8);
UPDATE complaints SET created_at = NOW() - INTERVAL '4 hours' WHERE complaint_id IN (9, 10);
UPDATE complaints SET created_at = NOW() - INTERVAL '5 hours' WHERE complaint_id IN (11, 12, 13);
UPDATE complaints SET created_at = NOW() - INTERVAL '6 hours' WHERE complaint_id IN (14, 15);
UPDATE complaints SET created_at = NOW() - INTERVAL '7 hours' WHERE complaint_id IN (16, 17, 18);
UPDATE complaints SET created_at = NOW() - INTERVAL '8 hours' WHERE complaint_id IN (19, 20);
UPDATE complaints SET created_at = NOW() - INTERVAL '9 hours' WHERE complaint_id IN (21, 22, 23);
UPDATE complaints SET created_at = NOW() - INTERVAL '10 hours' WHERE complaint_id IN (24, 25);

-- Add some customer feedback for resolved complaints
UPDATE complaints SET customer_feedback = 'Thank you for resolving this issue quickly. The staff was very helpful and professional.' WHERE complaint_id = 11;
UPDATE complaints SET customer_feedback = 'Excellent resolution! The refund was processed quickly and the new system works much better.' WHERE complaint_id = 12;
UPDATE complaints SET customer_feedback = 'Very satisfied with the resolution. The duplicate charges were refunded immediately.' WHERE complaint_id = 13;
UPDATE complaints SET customer_feedback = 'Great improvement! The security staff is now much more professional and welcoming.' WHERE complaint_id = 14;
UPDATE complaints SET customer_feedback = 'Perfect! The elevator is working great now and maintenance seems much better.' WHERE complaint_id = 15;

-- Add some additional resolution details
UPDATE complaints SET resolution = 'Issue has been thoroughly investigated. The diagnosis was correct, but we understand your concerns. Additional consultation provided free of charge. Patient satisfaction survey shows improvement in communication.' WHERE complaint_id = 11;
UPDATE complaints SET resolution = 'Billing issue has been completely resolved. Refund processed and new billing system implemented to prevent future occurrences. Patient portal now shows real-time billing status.' WHERE complaint_id = 12;
UPDATE complaints SET resolution = 'Duplicate charges identified and refunded immediately. New payment processing system implemented to prevent future occurrences. Additional fraud detection measures added.' WHERE complaint_id = 13;

-- Create some complaints without appointments (general complaints)
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
    created_by,
    updated_at,
    resolution,
    resolution_notes
) VALUES 
(1, NULL, 'OTHER', 'General feedback about hospital services', 'Overall experience at the hospital has been good, but there are areas for improvement in patient communication and service delivery.', 'EMAIL', 'MEDIUM', 'OPEN', NOW() - INTERVAL '1 day', 1, NOW() - INTERVAL '12 hours', NULL, NULL),

(1, NULL, 'FACILITY_ISSUE', 'WiFi connectivity issues', 'The patient WiFi is very slow and unreliable. This makes it difficult to stay connected while waiting for appointments.', 'EMAIL', 'LOW', 'IN_PROGRESS', NOW() - INTERVAL '2 days', 1, NOW() - INTERVAL '1 day', NULL, NULL),

(1, NULL, 'SERVICE_ISSUE', 'Website information outdated', 'The hospital website has outdated information about services and contact details. This causes confusion for patients.', 'EMAIL', 'MEDIUM', 'RESOLVED', NOW() - INTERVAL '5 days', 1, NOW() - INTERVAL '2 days', 'Website has been updated with current information. Regular review process implemented to keep information current.', 'Website content updated. Regular review schedule established. Staff trained on content management.'),

(1, NULL, 'OTHER', 'Suggestions for improvement', 'I have some suggestions for improving patient experience and hospital operations that I would like to share.', 'CALL', 'LOW', 'CLOSED', NOW() - INTERVAL '10 days', 1, NOW() - INTERVAL '5 days', 'Thank you for your valuable suggestions. Several recommendations have been implemented to improve patient experience.', 'Patient suggestions reviewed by management. Several recommendations implemented. Feedback system improved.');

-- Display summary of created complaints
SELECT 
    status,
    COUNT(*) as count,
    GROUP_CONCAT(title SEPARATOR ', ') as titles
FROM complaints 
WHERE patient_id = 1 
GROUP BY status 
ORDER BY 
    CASE status 
        WHEN 'OPEN' THEN 1 
        WHEN 'IN_PROGRESS' THEN 2 
        WHEN 'RESOLVED' THEN 3 
        WHEN 'CLOSED' THEN 4 
        WHEN 'REOPENED' THEN 5 
    END;

-- Migration script to add separate cancellation tracking columns
-- Run this script to update the appointments table

-- Add new columns for separate cancellation tracking
ALTER TABLE appointments 
ADD COLUMN cancelled_by_user BIGINT,
ADD COLUMN cancelled_by_doctor BIGINT;

-- Add foreign key constraints
ALTER TABLE appointments 
ADD CONSTRAINT fk_appointments_cancelled_by_user 
FOREIGN KEY (cancelled_by_user) REFERENCES users(id);

ALTER TABLE appointments 
ADD CONSTRAINT fk_appointments_cancelled_by_doctor 
FOREIGN KEY (cancelled_by_doctor) REFERENCES doctors(doctor_id);

-- Migrate existing cancelled_by data to appropriate column
-- This assumes existing cancelled_by values are user IDs
UPDATE appointments 
SET cancelled_by_user = cancelled_by 
WHERE cancelled_by IS NOT NULL;

-- Drop the old cancelled_by column
ALTER TABLE appointments DROP COLUMN cancelled_by;

-- Add indexes for better performance
CREATE INDEX idx_appointments_cancelled_by_user ON appointments(cancelled_by_user);
CREATE INDEX idx_appointments_cancelled_by_doctor ON appointments(cancelled_by_doctor);

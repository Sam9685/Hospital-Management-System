-- Migration script to add temporary appointment fields to payment table
-- This supports the payment-first appointment creation flow

-- Add temporary appointment data columns to payment table
ALTER TABLE payment 
ADD COLUMN temp_doctor_id BIGINT,
ADD COLUMN temp_appointment_date DATE,
ADD COLUMN temp_appointment_time TIME,
ADD COLUMN temp_end_time TIME,
ADD COLUMN temp_appointment_type VARCHAR(50),
ADD COLUMN temp_symptoms TEXT,
ADD COLUMN temp_notes TEXT,
ADD COLUMN temp_slot_id BIGINT;

-- Make appointment_id nullable (since payment can exist before appointment)
ALTER TABLE payment 
MODIFY COLUMN appointment_id BIGINT NULL;

-- Add indexes for better performance
CREATE INDEX idx_payment_temp_doctor_id ON payment(temp_doctor_id);
CREATE INDEX idx_payment_temp_slot_id ON payment(temp_slot_id);
CREATE INDEX idx_payment_temp_appointment_date ON payment(temp_appointment_date);

-- Add foreign key constraints for temporary fields
ALTER TABLE payment 
ADD CONSTRAINT fk_payment_temp_doctor_id 
FOREIGN KEY (temp_doctor_id) REFERENCES doctors(doctor_id);

ALTER TABLE payment 
ADD CONSTRAINT fk_payment_temp_slot_id 
FOREIGN KEY (temp_slot_id) REFERENCES doctor_slots(slot_id);

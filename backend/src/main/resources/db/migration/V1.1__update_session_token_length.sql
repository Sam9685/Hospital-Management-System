-- Migration to update session_token column length to accommodate JWT tokens
-- JWT tokens can be quite long (500-1000+ characters), so we need to increase the column size

-- For H2 database
ALTER TABLE session ALTER COLUMN session_token VARCHAR(1000);

-- For PostgreSQL (if you switch to PostgreSQL later)
-- ALTER TABLE session ALTER COLUMN session_token TYPE VARCHAR(1000);

-- For MySQL (if you switch to MySQL later)  
-- ALTER TABLE session MODIFY COLUMN session_token VARCHAR(1000);

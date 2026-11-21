-- ============================================================================
-- Task 06: Campus Event Registration System
-- Course: Programming in Java (CSA09)
-- Student: Ranjith Kumar.R | Register Number: 1924L260003
-- Database Engine: MySQL 8.0+
-- ============================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS campus_events
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_events;

-- 2. Drop existing tables if re-initializing
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;

-- 3. Create Events Table
CREATE TABLE events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(120) NOT NULL UNIQUE,
    category VARCHAR(60) NOT NULL,
    event_date DATE NOT NULL,
    venue VARCHAR(120) NOT NULL,
    capacity INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. Create Registrations Table
CREATE TABLE registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    student_email VARCHAR(120) NOT NULL,
    department VARCHAR(60) NOT NULL,
    event_id INT NOT NULL,
    registration_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Foreign Key Constraint
    CONSTRAINT fk_registration_event
        FOREIGN KEY (event_id)
        REFERENCES events(event_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    -- CRITICAL REQUIREMENT: Prevent duplicate registrations for the same student and event
    CONSTRAINT uk_student_event
        UNIQUE (student_id, event_id),
    -- Performance Indexes
    INDEX idx_event_filter (event_id),
    INDEX idx_student (student_id),
    INDEX idx_department (department)
) ENGINE=InnoDB;

-- 5. Seed Initial Campus Events
INSERT INTO events (event_name, category, event_date, venue, capacity) VALUES
('AI & Machine Learning Hackathon', 'Technical', '2026-12-05', 'Main Computing Lab', 150),
('National Tech Symposium 2026', 'Academic', '2026-12-10', 'University Auditorium', 300),
('Annual Cultural Fest - Tarang', 'Cultural', '2026-12-15', 'Open Air Amphitheatre', 600),
('Autonomous Robotics Challenge', 'Technical', '2026-12-18', 'Mechanical Workshop B', 120),
('Cyber Security & Forensics Workshop', 'Workshop', '2026-12-20', 'Seminar Hall 2', 90);

-- 6. Seed Sample Initial Registrations for Demonstration
INSERT INTO registrations (student_id, student_name, student_email, department, event_id) VALUES
('1924L260001', 'Arun Kumar', 'arun.k@campus.edu', 'Computer Science', 1),
('1924L260002', 'Bhavana M', 'bhavana.m@campus.edu', 'Information Technology', 1),
('1924L260003', 'Ranjith Kumar.R', 'ranjith.r@campus.edu', 'Computer Science', 2),
('1924L260004', 'Deepak S', 'deepak.s@campus.edu', 'Electronics', 3),
('1924L260005', 'Divya P', 'divya.p@campus.edu', 'Mathematics', 4);

-- 7. Verification Queries
SELECT * FROM events;
SELECT r.registration_id, r.student_id, r.student_name, r.student_email, r.department, e.event_name, r.registration_timestamp
FROM registrations r
JOIN events e ON r.event_id = e.event_id
ORDER BY r.registration_id DESC;

CREATE DATABASE hospital_db;
GO

USE hospital_db;
GO

CREATE TABLE users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(30) NOT NULL,
    full_name NVARCHAR(100),
    created_at DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE patients (
    patient_id NVARCHAR(10) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender NVARCHAR(10) NOT NULL,
    contact_number NVARCHAR(15) NOT NULL,
    address NVARCHAR(MAX),
    emergency_contact_name NVARCHAR(100),
    emergency_contact_phone NVARCHAR(15),
    blood_group NVARCHAR(5),
    allergies NVARCHAR(MAX),
    registration_date DATETIME DEFAULT GETDATE(),
    last_updated DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE doctors (
    doctor_id NVARCHAR(10) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    specialization NVARCHAR(100) NOT NULL,
    contact_number NVARCHAR(15),
    email NVARCHAR(100),
    available_days NVARCHAR(100),
    shift_start TIME,
    shift_end TIME,
    status NVARCHAR(20) DEFAULT 'Active'
);
GO

CREATE TABLE appointments (
    appointment_id NVARCHAR(10) PRIMARY KEY,
    patient_id NVARCHAR(10) NOT NULL,
    doctor_id NVARCHAR(10) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason NVARCHAR(MAX),
    status NVARCHAR(20) DEFAULT 'Scheduled',
    cancellation_reason NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    last_updated DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);
GO

CREATE TABLE billing (
    bill_id NVARCHAR(10) PRIMARY KEY,
    patient_id NVARCHAR(10) NOT NULL,
    appointment_id NVARCHAR(10),
    consultation_fee DECIMAL(10,2) DEFAULT 0,
    medication_charges DECIMAL(10,2) DEFAULT 0,
    test_charges DECIMAL(10,2) DEFAULT 0,
    other_charges DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status NVARCHAR(20) DEFAULT 'Unpaid',
    payment_method NVARCHAR(30),
    bill_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);
GO

CREATE TABLE audit_log (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    table_name NVARCHAR(50),
    record_id NVARCHAR(10),
    changed_by NVARCHAR(50),
    change_description NVARCHAR(MAX),
    change_timestamp DATETIME DEFAULT GETDATE()
);
GO

INSERT INTO users (username, password, role, full_name) VALUES
('admin', 'admin123', 'Admin', 'System Administrator'),
('receptionist1', 'rec123', 'Receptionist', 'Sara Ahmed'),
('doctor1', 'doc123', 'Doctor', 'Dr. Khalid Mehmood');
GO

INSERT INTO doctors VALUES
('DOC-0001', 'Dr. Khalid Mehmood', 'General Physician', '03001234567', 'khalid@hospital.com', 'Mon,Tue,Wed,Thu,Fri', '09:00', '17:00', 'Active'),
('DOC-0002', 'Dr. Ayesha Noor', 'Cardiologist', '03009876543', 'ayesha@hospital.com', 'Mon,Wed,Fri', '10:00', '16:00', 'Active'),
('DOC-0003', 'Dr. Usman Tariq', 'Orthopedic', '03012345678', 'usman@hospital.com', 'Tue,Thu,Sat', '08:00', '14:00', 'Active');
GO

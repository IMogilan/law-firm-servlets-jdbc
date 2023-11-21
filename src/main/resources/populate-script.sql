-- Populate law_firms table
INSERT INTO law_firm.public.law_firms (name, company_start_day)
VALUES ('Law Firm A', '2023-01-01'),
       ('Law Firm B', '2023-02-15'),
       ('Law Firm C', '2023-03-10');

-- Populate lawyers table
INSERT INTO lawyers (first_name, last_name, job_title, hourly_rate, law_firm_id)
VALUES ('John', 'Doe', 'ASSOCIATE', 100.00, 1),
       ('Marta', 'Doe', 'PARTNER', 150.00, 1),
       ('Chris', 'Doe', 'MANAGING_PARTNER', 200.00, 1),
       ('Jane', 'Smith', 'ASSOCIATE', 150.00, 2),
       ('Max', 'Smith', 'PARTNER', 200.00, 2),
       ('Piter', 'Smith', 'MANAGING_PARTNER', 250.00, 2),
       ('Gerbert', 'Johnson', 'ASSOCIATE', 200.00, 3),
       ('Robert', 'Johnson', 'PARTNER', 250.00, 3),
       ('Julia', 'Johnson', 'MANAGING_PARTNER', 300.00, 3);

-- Populate contact_details table
INSERT INTO contact_details (id, address, tel_number, mob_number, fax_number, email)
VALUES (1, '123 Main St, Cityville', '123-456-7890', '987-654-3210', '555-123-4567', 'john.doe@example.com'),
       (2, '123 Main St, Cityville', '123-456-7890', '987-654-3210', '555-123-4567', 'john.doe@example.com'),
       (3, '123 Main St, Cityville', '123-456-7890', '987-654-3210', '555-123-4567', 'john.doe@example.com'),
       (4, '456 Oak St, Townsville', '555-111-2222', '555-333-4444', '555-555-5555', 'jane.smith@example.com'),
       (5, '456 Oak St, Townsville', '555-111-2222', '555-333-4444', '555-555-5555', 'jane.smith@example.com'),
       (6, '456 Oak St, Townsville', '555-111-2222', '555-333-4444', '555-555-5555', 'jane.smith@example.com'),
       (7, '789 Pine St, Villageton', '888-999-0000', '111-222-3333', '444-555-6666', 'robert.johnson@example.com'),
       (8, '789 Pine St, Villageton', '888-999-0000', '111-222-3333', '444-555-6666', 'robert.johnson@example.com'),
       (9, '789 Pine St, Villageton', '888-999-0000', '111-222-3333', '444-555-6666', 'robert.johnson@example.com');

-- Populate clients table
INSERT INTO clients (name, description)
VALUES ('Apple', 'Corporate client with legal needs'),
       ('Elon M.', 'Individual seeking legal advice'),
       ('Peter&Mike', 'Startup company requiring legal services');

-- Populate tasks table
INSERT INTO tasks (title, description, priority, status, receipt_date, due_date, completion_date, hours_spent_on_task,
                   client_id)
VALUES ('Task 1', 'Review contract', 'HIGH', 'ACCEPTED', '2023-01-05', '2023-01-15', NULL, 5.0, 1),
       ('Task 2', 'Legal research', 'MEDIUM', 'IN_PROGRESS', '2023-02-01', '2023-02-10', NULL, 8.0, 2),
       ('Task 3', 'Draft legal documents', 'LOW', 'RECEIVED', '2023-03-15', '2023-03-25', '2023-03-20', 10.0, 3);

-- Populate lawyers_tasks table
INSERT INTO lawyers_tasks (lawyer_id, task_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);
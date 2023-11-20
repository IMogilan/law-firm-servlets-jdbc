-- CREATE DATABASE law_firm;

CREATE TABLE IF NOT EXISTS law_firm.public.law_firms
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(254) NOT NULL UNIQUE,
    company_start_day DATE
);

CREATE TABLE IF NOT EXISTS law_firm.public.lawyers
(
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(255)     NOT NULL,
    last_name   VARCHAR(255)     NOT NULL,
    job_title   VARCHAR(20)      NOT NULL,
    hourly_rate DOUBLE PRECISION NOT NULL,
    law_firm_id BIGINT REFERENCES law_firms ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS law_firm.public.contact_details
(
    id         BIGINT PRIMARY KEY REFERENCES lawyers ON DELETE CASCADE,
    address    VARCHAR(255),
    tel_number VARCHAR(20),
    mob_number VARCHAR(20),
    fax_number VARCHAR(20),
    email      VARCHAR(320)
);

CREATE TABLE IF NOT EXISTS law_firm.public.clients
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS law_firm.public.tasks
(
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    priority            VARCHAR(6)   NOT NULL,
    status              VARCHAR(11)  NOT NULL,
    receipt_date        DATE         NOT NULL,
    due_date            DATE         NOT NULL,
    completion_date     DATE,
    hours_spent_on_task DOUBLE PRECISION,
    client_id           BIGINT REFERENCES clients ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS law_firm.public.lawyers_tasks
(
    lawyer_id BIGINT NOT NULL REFERENCES lawyers ON DELETE CASCADE,
    task_id   BIGINT NOT NULL REFERENCES tasks ON DELETE CASCADE
);
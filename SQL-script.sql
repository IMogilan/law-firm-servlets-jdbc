-- CREATE DATABASE law_firm;

CREATE TABLE IF NOT EXISTS law_firms
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(254) NOT NULL UNIQUE,
    company_start_day DATE
);

CREATE TABLE IF NOT EXISTS lawyers
(
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(254)     NOT NULL,
    last_name   VARCHAR(254)     NOT NULL,
    job_title   VARCHAR(20)      NOT NULL,
    hourly_rate DOUBLE PRECISION NOT NULL,
    law_firm_id BIGINT REFERENCES law_firms ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contact_details
(
    id         BIGINT PRIMARY KEY REFERENCES lawyers ON DELETE CASCADE,
    address    VARCHAR(254),
    tel_number VARCHAR(20),
    mob_number VARCHAR(20),
    fax_number VARCHAR(20),
    email      VARCHAR(320)
);

CREATE TABLE IF NOT EXISTS clients
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(254) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS tasks
(
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(254) NOT NULL,
    description         TEXT,
    priority            VARCHAR(6)   NOT NULL,
    status              VARCHAR(11)  NOT NULL,
    receipt_date        DATE         NOT NULL,
    due_date            DATE         NOT NULL,
    completion_date     DATE,
    hours_spent_on_task DOUBLE PRECISION,
    client_id           BIGINT REFERENCES clients ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lawyers_tasks
(
    lawyer_id BIGINT NOT NULL REFERENCES lawyers ON DELETE CASCADE,
    task_id   BIGINT NOT NULL REFERENCES tasks ON DELETE CASCADE
);

SELECT t.id,
       t.title,
       t.description task_description,
       t.priority,
       t.status,
       t.receipt_date,
       t.due_date,
       t.completion_date,
       t.hours_spent_on_task,
       t.client_id,
       c.name        client_name,
       c.description client_description,
       lt.lawyer_id,
       l.first_name,
       l.last_name,
       l.job_title,
       l.hourly_rate,
       l.law_firm_id,
       lf.name       law_firm_name,
       lf.company_start_day,
       cd.id         contact_details_id,
       address,
       tel_number,
       mob_number,
       fax_number,
       email
FROM tasks t
         LEFT JOIN clients c on c.id = t.client_id
         LEFT JOIN lawyers_tasks lt on t.id = lt.task_id
         LEFT JOIN lawyers l on l.id = lt.lawyer_id
         LEFT JOIN law_firms lf on lf.id = l.law_firm_id
         LEFT JOIN contact_details cd on l.id = cd.id

SELECT lawyer_id
from lawyers_tasks
WHERE task_id = 10;

SELECT task_id
from lawyers_tasks
WHERE lawyer_id = 10;

SELECT lawyers.id,
       first_name,
       last_name,
       job_title,
       hourly_rate,
       law_firm_id,
       lf.name,
       lf.company_start_day
FROM lawyers
         LEFT JOIN law_firms lf on lf.id = lawyers.law_firm_id
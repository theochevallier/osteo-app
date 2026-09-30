-- drop table job, city, "patient", consultation, payment_type, invoice cascade;

CREATE TABLE IF NOT EXISTS JOB (
    id serial primary key,
    name varchar(100)
);

CREATE TABLE IF NOT EXISTS CITY (
    id serial primary key,
    name varchar(100),
    postal_code varchar(10),
    departement_id int references DEPARTEMENT(id)
);

CREATE TABLE IF NOT EXISTS DEPARTEMENT (
    id serial primary key,
    departement_name varchar(100),
    region_id int references REGION(id) null
);

CREATE TABLE IF NOT EXISTS REGION (
    id serial primary key,
    region_name varchar(100)
);


CREATE TABLE IF NOT EXISTS PAYMENT_TYPE(
    id serial primary key,
    name varchar(100)
);

CREATE TABLE IF NOT EXISTS PATIENT (
    id serial primary key,
    lastname varchar(255),
    firstname varchar(255),
    birthday date not null,
    genre varchar(50),
    address varchar(255),
    email varchar(255),
    mobile_phone varchar(30),
    fix_phone varchar(30),
    tutor_relative varchar(255),
    is_english boolean,
    bruxisme boolean,
    osteoporose varchar(30),
    oil_allergy boolean,
    medical_history text,
    contraindication text,
    canceled_appointment text,
    created_at timestamp default now(),
    updated_at timestamp,
    archived_at timestamp default null,
    job_id int references JOB(id),
    city_id int references CITY(id)
);

CREATE TABLE IF NOT EXISTS PRACTITIONER (
    id serial primary key,
    firstname varchar(50),
    lastname varchar(80),
    email varchar(50),
    phone varchar(30),
    rpps_id varchar(40),
    username varchar(50),
    password varchar(300),
    created_at timestamp default now(),
    updated_at timestamp,
    archived_at timestamp default null
);

CREATE TABLE IF NOT EXISTS CONSULTATION (
    id serial primary key,
    consultation_date timestamp default now(),
    reason text,
    treatment text,
    note text,
    medical_exam text,
    exclusion_test text,
    given_advice text,
    given_exercise text,
    food_supplement text,
    is_doctor_relocation boolean,
    is_dentist_relocation boolean,
    is_orthophoniste_relocation boolean,
    is_orthodontiste_relocation boolean,
    is_orthoptiste_relocation boolean,
    is_podologue_relocation boolean,
    relocation_note text,
    created_at timestamp default now(),
    updated_at timestamp,
    patient_id int references patient(id) ON DELETE CASCADE,
    practitioner_id int references practitioner(id)
);


CREATE TABLE IF NOT EXISTS INVOICE (
    id serial primary key,
    invoice_date Date default now(),
    amount int,
    cheque_ref varchar(100),
    cheque_deposit_id varchar(100),
    created_at timestamp default now(),
    updated_at timestamp,
    payment_type_id int references PAYMENT_TYPE(id),
    consultation_id int references CONSULTATION(id)
);

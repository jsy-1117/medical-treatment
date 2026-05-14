CREATE TABLE IF NOT EXISTS doctor_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    shift_type INT NOT NULL DEFAULT 1,
    quota INT NOT NULL DEFAULT 20,
    remaining_quota INT NOT NULL DEFAULT 20,
    status INT NOT NULL DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_id BIGINT,
    username VARCHAR(64),
    name VARCHAR(64),
    job_title VARCHAR(64),
    introduction TEXT,
    phone VARCHAR(20),
    password VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(64),
    dept_desc TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

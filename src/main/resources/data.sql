-- Create an in memory database for the tools rental project.
-- This is expected to be replaced with an actual database
--
-- Create Schema for use
CREATE SCHEMA IF NOT EXISTS TOOLS_R_US_SCHEMA;
USE TOOLS_R_US_SCHEMA;

-- Create the tool type table for listing the tool types
CREATE TABLE IF NOT EXISTS Tool_Type (
    type varchar(20) NOT NULL PRIMARY KEY
);

-- Create the vendor brands table for listing the brands
CREATE TABLE IF NOT EXISTS Vendors (
    brand varchar(30) NOT NULL PRIMARY KEY
);

-- Create the tools table for listing the tools
CREATE TABLE IF NOT EXISTS Tools (
    code varchar(10) NOT NULL PRIMARY KEY,
    type varchar(20) NOT NULL REFERENCES Tool_Type(type),
    brand varchar(30) NOT NULL REFERENCES Vendors(brand)
);

-- Create the charge table based on tool type
CREATE TABLE IF NOT EXISTS Tools_Charges (
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type varchar(20) NOT NULL REFERENCES Tool_Type(type),
    daily_charge decimal(5,2),
    weekday_charge bit,
    weekend_charge bit,
    holiday_charge bit
);

-- Create the holiday day falls on table
CREATE TABLE IF NOT EXISTS Holiday_Days_Of_Week (
    day_of_the_week varchar(15) NOT NULL PRIMARY KEY
);

-- Create the allowed frequencies of holiday table
CREATE TABLE IF NOT EXISTS Holiday_Frequency (
    frequency varchar(20) NOT NULL PRIMARY KEY
);

-- Create the holidays table for checking if it is a holiday
CREATE TABLE IF NOT EXISTS Holidays (
    name varchar(20) NOT NULL PRIMARY KEY,
    holiday_month int,
    holiday_day int,
    day_of_the_week varchar(15) REFERENCES Holiday_Days_Of_Week(day_of_the_week),
    frequency varchar(20) REFERENCES Holiday_Frequency(frequency)
);

-- Create the list of valid statuses for rental
CREATE TABLE IF NOT EXISTS Tool_Status (
    status varchar(10) NOT NULL PRIMARY KEY
);

-- Create Rental table for tracking when a tool is rented
CREATE TABLE IF NOT EXISTS Tool_Rentals (
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    code varchar(10) NOT NULL REFERENCES Tools(code),
    type varchar(20) NOT NULL REFERENCES Tool_Type(type),
    brand varchar(30) NOT NULL REFERENCES Vendors(brand),
    rental_days int NOT NULL,
    checkout_date date NOT NULL,
    due_date date,
    charge_days int NOT NULL,
    due decimal(10,2) NOT NULL,
    daily_charge decimal(5,2) NOT NULL,
    pre_discount_charge decimal(10,2) NOT NULL,
    discount_percent decimal(3,3),
    discount_amount decimal(10,2),
    final_charge decimal(10,2) NOT NULL,
    status varchar(10) NOT NULL REFERENCES Tool_Status(status)
);

-- Populate the tool types
INSERT INTO Tool_Type (type)
    VALUES ('ChainSaw'),
           ('JackHammer'),
           ('Ladder');

-- Populate the vendor brands table
INSERT INTO Vendors (brand)
    VALUES ('Stihl'),
           ('Werner'),
           ('DeWalt'),
           ('Ridgid');

-- Populate the Tools table
INSERT INTO Tools (code, type, brand)
    VALUES ('CHNS', 'ChainSaw', 'Stihl'),
           ('LADW', 'Ladder', 'Werner'),
           ('JAKD', 'JackHammer', 'DeWalt'),
           ('JAKR', 'JackHammer', 'Ridgid');

-- Populate the charge table
INSERT INTO Tools_Charges (type, daily_charge, weekday_charge, weekend_charge, holiday_charge)
    VALUES ('Ladder', 1.99, 1, 1, 0),
           ('ChainSaw', 1.49, 1, 0, 1),
           ('JackHammer', 2.99, 1, 0, 0);

-- Populate the days of the week a holiday can fall on
INSERT INTO Holiday_Days_Of_Week (day_of_the_week)
   VALUES ('Monday');

-- Populate the frequencies for holidays allowed
INSERT INTO Holiday_Frequency (frequency)
    VALUES ('First');

-- Populate the holidays table
INSERT INTO Holidays (name, holiday_month, holiday_day, day_of_the_week, frequency)
    VALUES ('Independence Day', 7, 4, null, null),
           ('Labor Day', null, null, 'Monday', 'First');

-- Populate the valid statuses for rentals
INSERT INTO Tool_Status (status)
    VALUES ('ACTIVE'),
           ('CLOSED');


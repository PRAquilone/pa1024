-- Create an in memory database for the tools rental project.
-- This is expected to be replaced with an actual database
--
-- Create Schema for use
CREATE SCHEMA ToolsSchema;
USE ToolsSchema;

-- Create the tool type table for listing the tool types
CREATE TABLE IF NOT EXISTS toolType (
    type varchar(20) NOT NULL PRIMARY KEY
);

-- Create the vendor brands table for listing the brands
CREATE TABLE IF NOT EXISTS vendors (
    brand varchar(30) NOT NULL PRIMARY KEY
);

-- Create the tools table for listing the tools
CREATE TABLE IF NOT EXISTS tools (
    toolCode varchar(10) NOT NULL PRIMARY KEY,
    toolType varchar(20) NOT NULL REFERENCES toolType(type),
    toolBrand varchar(30) NOT NULL REFERENCES vendors(brand)
);

-- Create the charge table based on tool type
CREATE TABLE IF NOT EXISTS toolsCharges (
    chargesId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    toolType varchar(20) NOT NULL REFERENCES toolType(type),
    dailyCharge decimal(5,2),
    weekDayCharge bit,
    weekEndCharge bit,
    holidayCharge bit
);

-- Create the holiday day falls on table
CREATE TABLE IF NOT EXISTS days (
    dayOfWeek varchar(15) NOT NULL PRIMARY KEY
);

-- Create the allowed frequencies of holiday table
CREATE TABLE IF NOT EXISTS frequency (
    freq varchar(20) NOT NULL PRIMARY KEY
);

-- Create the holidays table for checking if it is a holiday
CREATE TABLE IF NOT EXISTS holiday (
    name varchar(20) NOT NULL PRIMARY KEY,
    holidayMonth int,
    holidayDay int,
    dayFallsOn varchar(15) REFERENCES days(dayOfWeek),
    frequency varchar(20) REFERENCES frequency(freq)
);

-- Create the list of valid statuses for rental
CREATE TABLE IF NOT EXISTS toolStatus (
    status varchar(10) NOT NULL PRIMARY KEY
);

-- Create Rental table for tracking when a tool is rented
CREATE TABLE IF NOT EXISTS rental (
    rentalId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    toolCode varchar(10) NOT NULL REFERENCES tools(toolCode),
    toolType varchar(20) NOT NULL REFERENCES toolType(type),
    toolBrand varchar(30) NOT NULL REFERENCES vendors(brand),
    rentalDays int NOT NULL,
    checkOutDate date NOT NULL,
    dueDate date,
    chargeDays int NOT NULL,
    due decimal(10,2) NOT NULL,
    dailyCharge decimal(5,2) NOT NULL,
    preDiscountCharge decimal(10,2) NOT NULL,
    discountPercent decimal(3,3),
    discountAmount decimal(10,2),
    finalCharge decimal(10,2) NOT NULL,
    status varchar(10) NOT NULL REFERENCES toolStatus(status)
);

-- Populate the tool types
INSERT INTO toolType (type)
    VALUES ('ChainSaw'),
           ('JackHammer'),
           ('Ladder');

-- Populate the vendor brands table
INSERT INTO vendors (brand)
    VALUES ('Stihl'),
           ('Werner'),
           ('DeWalt'),
           ('Ridgid');

-- Populate the Tools table
INSERT INTO tools (toolCode, toolType, toolBrand)
    VALUES ('CHNS', 'ChainSaw', 'Stihl'),
           ('LADW', 'Ladder', 'Werner'),
           ('JAKD', 'JackHammer', 'DeWalt'),
           ('JAKR', 'JackHammer', 'Ridgid');

-- Populate the charge table
INSERT INTO toolsCharges ( toolType, dailyCharge, weekDayCharge, weekEndCharge, holidayCharge)
    VALUES ('Ladder', 1.99, 1, 1, 0),
           ('ChainSaw', 1.49, 1, 0, 1),
           ('JackHammer', 2.99, 1, 0, 0);

-- Populate the days of the week a holiday can fall on
INSERT INTO days (dayOfWeek)
   VALUES ('Monday');

-- Populate the frequencies for holidays allowed
INSERT INTO frequency (freq)
    VALUES ('First');

-- Populate the holidays table
INSERT INTO holiday (name, holidayMonth, holidayDay, dayFallsOn, frequency)
    VALUES ('Independence Day', 7, 4, null, null),
           ('Labor Day', null, null, 'Monday', 'First');

-- Populate the valid statuses for rentals
INSERT INTO toolStatus (status)
    VALUES ('ACTIVE'),
           ('CLOSED');


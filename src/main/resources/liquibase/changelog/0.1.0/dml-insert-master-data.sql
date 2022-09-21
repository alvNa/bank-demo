--liquibase formatted sql
-- Database: postgresql

-- changeset anavarro:2
INSERT INTO TRANSACTION_TYPES (ID, DESCRIPTION) VALUES ('GBS_TRANSACTION_TYPE', 'GBS_TRANSACTION_TYPE_0015');

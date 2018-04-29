DROP DATABASE if exists Users;
CREATE DATABASE Users;
USE Users;

DROP TABLE if exists `users`.`user_table`;

CREATE TABLE `users`.`user_table` (
  `username` VARCHAR(55) NOT NULL,
  `password` VARCHAR(55) NOT NULL,
  PRIMARY KEY (`username`));
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema oht_log
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema oht_log
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `oht_log` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `oht_log` ;

-- -----------------------------------------------------
-- Table `oht_log`.`log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oht_log`.`log` (
  `oht_id` BIGINT NOT NULL,
  `mode` ENUM('A', 'M') NOT NULL,
  `status` ENUM('G', 'A', 'W', 'I', 'E') NOT NULL,
  `current_node` VARCHAR(20) NOT NULL,
  `next_node` VARCHAR(20) NULL DEFAULT NULL,
  `target_node` VARCHAR(20) NULL DEFAULT NULL,
  `carrier` TINYINT(1) NOT NULL,
  `error` INT NULL DEFAULT NULL,
  `oht_connect` TINYINT(1) NULL DEFAULT NULL,
  `curr_node_offset` DECIMAL(10,3) NULL DEFAULT NULL,
  `speed` DECIMAL(10,3) NOT NULL,
  `is_fail` TINYINT(1) NOT NULL,
  `curr_time` DATETIME NOT NULL,
  `start_time` DATETIME NULL DEFAULT NULL,
  `point_x` DECIMAL(10,3) NOT NULL,
  `point_y` DECIMAL(10,3) NOT NULL,
  `path` VARCHAR(20) NULL DEFAULT NULL,
  `doc_id` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`doc_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

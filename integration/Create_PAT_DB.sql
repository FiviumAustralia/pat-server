-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.1.22-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5174
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for pat
CREATE DATABASE IF NOT EXISTS `pat` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `pat`;

-- Dumping structure for table pat.appdata
DROP TABLE IF EXISTS `appdata`;
CREATE TABLE IF NOT EXISTS `appdata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_ts` datetime DEFAULT NULL,
  `csv_filename` varchar(100) DEFAULT NULL,
  `csv_content` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=537 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.clinicians
DROP TABLE IF EXISTS `clinicians`;
CREATE TABLE IF NOT EXISTS `clinicians` (
  `Email` varchar(50) NOT NULL DEFAULT '0',
  `Password` varchar(500) NOT NULL DEFAULT '0',
  `Firstname` varchar(50) NOT NULL DEFAULT '0',
  `Lastname` varchar(50) NOT NULL DEFAULT '0',
  `Token` varchar(500) NOT NULL DEFAULT '0',
  `Role` varchar(50) NOT NULL DEFAULT 'user',
  `Company` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.company
DROP TABLE IF EXISTS `company`;
CREATE TABLE IF NOT EXISTS `company` (
  `Company_Name` varchar(50) NOT NULL,
  `Terms_and_Conditions` varchar(5000) NOT NULL,
  `Permissions` varchar(50) NOT NULL,
  `Category` varchar(50) NOT NULL,
  PRIMARY KEY (`Company_Name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.errorlog
DROP TABLE IF EXISTS `errorlog`;
CREATE TABLE IF NOT EXISTS `errorlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` datetime DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.fitness_data
DROP TABLE IF EXISTS `fitness_data`;
CREATE TABLE IF NOT EXISTS `fitness_data` (
  `p_id` varchar(50) DEFAULT NULL,
  `data` varchar(50000) DEFAULT 'NULL',
  `date` date DEFAULT NULL,
  `last_sync_date` date DEFAULT NULL,
  UNIQUE KEY `fitness_data_p_id_date_pk` (`p_id`,`date`),
  CONSTRAINT `fitness_data_patient_p_id_fk` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.internaluser
DROP TABLE IF EXISTS `internaluser`;
CREATE TABLE IF NOT EXISTS `internaluser` (
  `User` varchar(50) DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
  `Salt` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.notifications
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE IF NOT EXISTS `notifications` (
  `study_id` varchar(50) NOT NULL,
  `date` date DEFAULT NULL,
  `notification_text` varchar(50) DEFAULT NULL,
  `delivery_status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`study_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.patient
DROP TABLE IF EXISTS `patient`;
CREATE TABLE IF NOT EXISTS `patient` (
  `p_id` varchar(50) NOT NULL,
  `Token` varchar(500) NOT NULL,
  `provider_user_id` varchar(50) DEFAULT NULL,
  `provider_refresh_token` varchar(500) DEFAULT NULL,
  `provider_permissions` varchar(500) DEFAULT NULL,
  `provider` varchar(50) DEFAULT NULL,
  `Active` varchar(50) NOT NULL,
  `Company` varchar(50) NOT NULL,
  `firebase_device_token` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.patient_details
DROP TABLE IF EXISTS `patient_details`;
CREATE TABLE IF NOT EXISTS `patient_details` (
  `study_id` varchar(50) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `mrn` varchar(50) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `next_of_kin_relationship` varchar(50) DEFAULT NULL,
  `next_of_kin_first_name` varchar(50) DEFAULT NULL,
  `next_of_kin_last_name` varchar(50) DEFAULT NULL,
  `next_of_kin_contact_number` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.rss_feeds
DROP TABLE IF EXISTS `rss_feeds`;
CREATE TABLE IF NOT EXISTS `rss_feeds` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `url` varchar(2000) NOT NULL,
  `color` varchar(20) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.sleepdata
DROP TABLE IF EXISTS `sleepdata`;
CREATE TABLE IF NOT EXISTS `sleepdata` (
  `study_id` varchar(100) NOT NULL,
  `date` varchar(100) NOT NULL,
  `duration` varchar(100) NOT NULL,
  `efficiency` varchar(100) NOT NULL,
  PRIMARY KEY (`study_id`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.stepdata
DROP TABLE IF EXISTS `stepdata`;
CREATE TABLE IF NOT EXISTS `stepdata` (
  `study_id` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `steps` int(11) DEFAULT NULL,
  PRIMARY KEY (`study_id`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.surveydata
DROP TABLE IF EXISTS `surveydata`;
CREATE TABLE IF NOT EXISTS `surveydata` (
  `study_id` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `survey_data` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`study_id`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table pat.weightdata
DROP TABLE IF EXISTS `weightdata`;
CREATE TABLE IF NOT EXISTS `weightdata` (
  `study_id` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `weight` int(11) DEFAULT NULL,
  PRIMARY KEY (`study_id`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

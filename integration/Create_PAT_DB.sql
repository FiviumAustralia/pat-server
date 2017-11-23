-- --------------------------------------------------------
-- Host:                         192.168.57.204
-- Server version:               10.2.9-MariaDB - Homebrew
-- Server OS:                    osx10.12
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for rns
DROP DATABASE IF EXISTS `rns`;
CREATE DATABASE IF NOT EXISTS `rns` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `rns`;

-- Dumping structure for table rns.company
DROP TABLE IF EXISTS `company`;
CREATE TABLE IF NOT EXISTS `company` (
  `Company_Name` varchar(50) NOT NULL,
  `Terms_and_Conditions` varchar(5000) NOT NULL,
  `Permissions` varchar(50) NOT NULL,
  `Category` varchar(50) NOT NULL,
  PRIMARY KEY (`Company_Name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table rns.fitness_data
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
-- Dumping structure for table rns.internaluser
DROP TABLE IF EXISTS `internaluser`;
CREATE TABLE IF NOT EXISTS `internaluser` (
  `User` varchar(50) DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
  `Salt` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table rns.notifications
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE IF NOT EXISTS `notifications` (
  `p_id` varchar(50) NOT NULL,
  `notification_date` date DEFAULT NULL,
  `notification_text` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table rns.patient
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
-- Dumping structure for table rns.rss_feeds
DROP TABLE IF EXISTS `rss_feeds`;
CREATE TABLE IF NOT EXISTS `rss_feeds` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `url` varchar(2000) NOT NULL,
  `color` varchar(20) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

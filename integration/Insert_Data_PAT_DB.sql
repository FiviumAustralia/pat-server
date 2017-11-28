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


-- Dumping database structure for pat
CREATE DATABASE IF NOT EXISTS `pat` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `pat`;

-- Dumping structure for table pat.company
CREATE TABLE IF NOT EXISTS `company` (
  `Company_Name` varchar(50) NOT NULL,
  `Terms_and_Conditions` varchar(5000) NOT NULL,
  `Permissions` varchar(50) NOT NULL,
  `Category` varchar(50) NOT NULL,
  PRIMARY KEY (`Company_Name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table pat.company: ~2 rows (approximately)
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` (`Company_Name`, `Terms_and_Conditions`, `Permissions`, `Category`) VALUES
	('ABC', 'General Site Usage\r\n\r\nLast Revised: December 16, 2013\r\n\r\nWelcome to www.lorem-ipsum.info. This site is provided as a service to our visitors and may be used for informational purposes only. Because the Terms and Conditions contain legal obligations, please read them carefully.\r\n\r\n1. YOUR AGREEMENT\r\n\r\nBy using this Site, you agree to be bound by, and to comply with, these Terms and Conditions. If you do not agree to these Terms and Conditions, please do not use this site.\r\n\r\nPLEASE NOTE: We reserve the right, at our sole discretion, to change, modify or otherwise alter these Terms and Conditions at any time. Unless otherwise indicated, amendments will become effective immediately. Please review these Terms and Conditions periodically. Your continued use of the Site following the posting of changes and/or modifications will constitute your acceptance of the revised Terms and Conditions and the reasonableness of these standards for notice of changes. For your information, this page was last updated as of the date at the top of these terms and conditions.\r\n2. PRIVACY\r\n\r\nPlease review our Privacy Policy, which also governs your visit to this Site, to understand our practices.\r\n\r\n3. LINKED SITES\r\n\r\nThis Site may contain links to other independent third-party Web sites ("Linked Sites”). These Linked Sites are provided solely as a convenience to our visitors. Such Linked Sites are not under our control, and we are not responsible for and does not endorse the content of such Linked Sites, including any information or materials contained on such Linked Sites. You will need to make your own independent judgment regarding your interaction with these Linked Sites.\r\n\r\n4. FORWARD LOOKING STATEMENTS\r\n\r\nAll materials reproduced on this site speak as of the original date of publication or filing. The fact that a document is available on this site does not mean that the information contained in such document has not been modified or superseded by events or by a subsequent document or filing. We have no duty or policy to update any information or statements contained on this site and, therefore, such information or statements should not be relied upon as being current as of the date you access this site.\r\n\r\n5. DISCLAIMER OF WARRANTIES AND LIMITATION OF LIABILITY\r\n\r\nA. THIS SITE MAY CONTAIN INACCURACIES AND TYPOGRAPHICAL ERRORS. WE DOES NOT WARRANT THE ACCURACY OR COMPLETENESS OF THE MATERIALS OR THE RELIABILITY OF ANY ADVICE, OPINION, STATEMENT OR OTHER INFORMATION DISPLAYED OR DISTRIBUTED THROUGH THE SITE. YOU EXPRESSLY UNDERSTAND AND AGREE THAT: (i) YOUR USE OF THE SITE, INCLUDING ANY RELIANCE ON ANY SUCH OPINION, ADVICE, STATEMENT, MEMORANDUM, OR INFORMATION CONTAINED HEREIN, SHALL BE AT YOUR SOLE RISK; (ii) THE SITE IS PROVIDED ON AN "AS IS" AND "AS AVAILABLE" BASIS; (iii) EXCEPT AS EXPRESSLY PROVIDED HEREIN WE DISCLAIM ALL WARRANTIES OF ANY KIND, WHETHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, WORKMANLIKE EFFORT, TITLE AND NON-INFRINGEMENT; (iv) WE MAKE NO WARRANTY WITH RESPECT TO THE RESULTS THAT MAY BE OBTAINED FROM THIS SITE, THE PRODUCTS OR SERVICES ADVERTISED OR OFFERED OR MERCHANTS INVOLVED; (v) ANY MATERIAL DOWNLOADED OR OTHERWISE OBTAINED THROUGH THE USE OF THE SITE IS DONE AT YOUR OWN DISCRETION AND RISK; and (vi) YOU WILL BE SOLELY RESPONSIBLE FOR ANY DAMAGE TO YOUR COMPUTER SYSTEM OR FOR ANY LOSS OF DATA THAT RESULTS FROM THE DOWNLOAD OF ANY SUCH MATERIAL.\r\n\r\nB. YOU UNDERSTAND AND AGREE THAT UNDER NO CIRCUMSTANCES, INCLUDING, BUT NOT LIMITED TO, NEGLIGENCE, SHALL WE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES THAT RESULT FROM THE USE OF, OR THE INABILITY TO USE, ANY OF OUR SITES OR MATERIALS OR FUNCTIONS ON ANY SUCH SITE, EVEN IF WE HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. THE FOREGOING LIMITATIONS SHALL APPLY NOTWITHSTANDING ANY FAILURE OF ESSENTIAL PURPOSE OF ANY LIMITED REMEDY.\r\n\r\n6. EXCLUSIONS AND LIMITATIONS\r\n\r\nSOME JURISDICTIONS DO NOT ALLOW THE EXCLUSION OF CERTAIN WARRANTIES OR THE LIMITATION OR EXCLUSION OF LIABILITY FOR INCIDENTAL OR CONSEQUENTIAL DAMAGES. ACCORDINGLY, OUR LIABILITY IN SUCH JURISDICTION SHALL BE LIMITED TO THE MAXIMUM EXTENT PERMITTED BY LAW.\r\n\r\n7. OUR PROPRIETARY RIGHTS\r\n\r\nThis Site and all its Contents are intended solely for personal, non-commercial use. Except as expressly provided, nothing within the Site shall be construed as conferring any license under our or any third party\'s intellectual property rights, whether by estoppel, implication, waiver, or otherwise. Without limiting the generality of the foregoing, you acknowledge and agree that all content available through and used to operate the Site and its services is protected by copyright, trademark, patent, or other proprietary rights. You agree not to: (a) modify, alter, or deface any of the trademarks, service marks, trade dress (collectively "Trademarks") o', 'sleep activity profile settings location', 'C'),
	('XYZ', 'fyufyf', 'sleep', 'C');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;

-- Dumping structure for table pat.fitness_data
CREATE TABLE IF NOT EXISTS `fitness_data` (
  `p_id` varchar(50) DEFAULT NULL,
  `data` varchar(50000) DEFAULT 'NULL',
  `date` date DEFAULT NULL,
  `last_sync_date` date DEFAULT NULL,
  UNIQUE KEY `fitness_data_p_id_date_pk` (`p_id`,`date`),
  CONSTRAINT `fitness_data_patient_p_id_fk` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table pat.fitness_data: ~15 rows (approximately)
/*!40000 ALTER TABLE `fitness_data` DISABLE KEYS */;
INSERT INTO `fitness_data` (`p_id`, `data`, `date`, `last_sync_date`) VALUES
	('1', '{"pId":"1","activityDate":"2017-10-31","dailyStepData":{"dateTime":"2017-10-31","value":3569},"deviceData":[{"battery":"High","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-10-31T16:55:52.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-10-31"}', '2017-10-31', '2017-10-31'),
	('1', '{"pId":"1","activityDate":"2017-11-01","dailyStepData":{"dateTime":"2017-11-01","value":9433},"deviceData":[{"battery":"High","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-01T21:37:17.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-01"}', '2017-11-01', '2017-10-31'),
	('1', '{"pId":"1","activityDate":"2017-11-02","dailyStepData":{"dateTime":"2017-11-02","value":11180},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-02T19:58:36.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-02"}', '2017-11-02', '2017-11-01'),
	('1', '{"pId":"1","activityDate":"2017-11-03","dailyStepData":{"dateTime":"2017-11-03","value":0},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-02T19:58:36.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-02"}', '2017-11-03', '2017-11-02'),
	('1', '{"pId":"1","activityDate":"2017-11-04","dailyStepData":{"dateTime":"2017-11-04","value":0},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-02T19:58:36.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-02"}', '2017-11-04', '2017-11-02'),
	('1', '{"pId":"1","activityDate":"2017-11-05","dailyStepData":{"dateTime":"2017-11-05","value":3959},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-05T22:36:02.446","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-05"}', '2017-11-05', '2017-11-02'),
	('1', '{"pId":"1","activityDate":"2017-11-06","dailyStepData":{"dateTime":"2017-11-06","value":1197},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-06T16:59:17.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-06"}', '2017-11-06', '2017-11-05'),
	('1', '{"pId":"1","activityDate":"2017-11-07","dailyStepData":{"dateTime":"2017-11-07","value":3396},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-07T17:56:41.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-07"}', '2017-11-07', '2017-11-06'),
	('44', '{"pId":"44","activityDate":"2017-11-07","dailyStepData":{"dateTime":"2017-11-07","value":5236},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"492186654","lastSyncTime":"2017-11-07T22:54:28.964","mac":"20471254F2F6","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-07"}', '2017-11-07', '2017-11-07'),
	('1', '{"pId":"1","activityDate":"2017-11-08","dailyStepData":{"dateTime":"2017-11-08","value":0},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-07T17:56:41.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-07"}', '2017-11-08', '2017-11-07'),
	('44', '{"pId":"44","activityDate":"2017-11-08","dailyStepData":{"dateTime":"2017-11-08","value":2074},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"492186654","lastSyncTime":"2017-11-08T11:08:31.106","mac":"20471254F2F6","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-08"}', '2017-11-08', '2017-11-07'),
	('33', '{"pId":"33","activityDate":"2017-11-08","dailyStepData":{"dateTime":"2017-11-08","value":2494},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"492186654","lastSyncTime":"2017-11-08T13:49:35.693","mac":"20471254F2F6","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-08"}', '2017-11-08', '2017-11-08'),
	('37', '{"pId":"37","activityDate":"2017-11-08","dailyStepData":{"dateTime":"2017-11-08","value":5617},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"492186654","lastSyncTime":"2017-11-08T19:37:01.000","mac":"20471254F2F6","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-08"}', '2017-11-08', '2017-11-08'),
	('1', '{"pId":"1","activityDate":"2017-11-09","dailyStepData":{"dateTime":"2017-11-09","value":0},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"426929266","lastSyncTime":"2017-11-07T17:56:41.000","mac":"48CF5193B9CF","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-07"}', '2017-11-09', '2017-11-07'),
	('37', '{"pId":"37","activityDate":"2017-11-09","dailyStepData":{"dateTime":"2017-11-09","value":1447},"deviceData":[{"battery":"Medium","deviceVersion":"Charge 2","id":"492186654","lastSyncTime":"2017-11-09T08:39:58.000","mac":"20471254F2F6","type":"TRACKER","features":[]}],"lastSyncDate":"2017-11-09"}', '2017-11-09', '2017-11-08');
/*!40000 ALTER TABLE `fitness_data` ENABLE KEYS */;

-- Dumping structure for table pat.internaluser
CREATE TABLE IF NOT EXISTS `internaluser` (
  `User` varchar(50) DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
  `Salt` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table pat.internaluser: ~1 rows (approximately)
/*!40000 ALTER TABLE `internaluser` DISABLE KEYS */;
INSERT INTO `internaluser` (`User`, `Password`, `Salt`) VALUES
	('superuser', '[¡ÍçÎ#Ä¯îIÉ.§Vë¹h²úÿõ·+·Ö×Åkj', 'T½PÅ?A7ýõ?Æ<ýë');
/*!40000 ALTER TABLE `internaluser` ENABLE KEYS */;

-- Dumping structure for table pat.notifications
CREATE TABLE IF NOT EXISTS `notifications` (
  `p_id` varchar(50) NOT NULL,
  `notification_date` date NOT NULL,
  `notification_text` varchar(50) NOT NULL,
  `delivery_status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`p_id`,`notification_text`),
  CONSTRAINT `notifications_patient_p_id_fk` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table pat.notifications: ~2 rows (approximately)
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` (`p_id`, `notification_date`, `notification_text`, `delivery_status`) VALUES
	('1', '2017-11-06', 'Please charge your Fitbit', 'false'),
	('1', '2017-11-09', 'Please sync with Fitbit', 'false');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;

-- Dumping structure for table pat.patient
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

-- Dumping data for table pat.patient: ~53 rows (approximately)
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` (`p_id`, `Token`, `provider_user_id`, `provider_refresh_token`, `provider_permissions`, `provider`, `Active`, `Company`, `firebase_device_token`) VALUES
	('1', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIn0.OzqiHsm7N5zI-WATQHKSqn9Tx2bWdcbINaf0oTtXzKbii2jeg6pB_F6FHLjm-EJT_MxFzDgPxXRfHHcfs5IctQ', '5ND9BK', 'af418020a722ad2dffcef8623722593e5df4b712469c6870afe2b95ed69adf0f', 'sleep location settings activity profile', 'fitbit', 'Active', 'ABC', 'doeSqNkyfLg:APA91bFgC7D460D8JYNiI3nzm_yvQ80bOm2R8fItdIdmiIBGJKHoHGIEjlp1HVOSQ0VfPvPgu9Ydjy6h7C6vvoVIC85sOoNjR7ovo5P0dAgdlqJwarQjj-mf4gTio2Gbyn5wRRFHciOn'),
	('10', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('11', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSJ9.2-TRpSDfY1baXu7G6yqAx7rKHViCSEFl_fMsQyPEIVcWND6SxIYboG5jJ7AU-3C9-YC-rR6iVm__0K1asGNeOg', '5T9GT2', '2d6449f0b9d089db42df229cac523e1008d19b7bbe49700b2dcb35dfd85f0633', 'settings sleep activity profile location', 'fitbit', 'Active', 'ABC', 'dVsy8yCG56s:APA91bGnM5tL2uYxNqGrjowPA4cHluaf1uX5-4czjw3fjBqv5dza2Rqetc4VLgWRhSXnvZF2BdoefmqHRVmvmuI4JpRKG4z1nRSE3fym1XV69-R1aNkAEsDWBgapVqkL1P8NMhzO2pBa'),
	('12', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMiJ9.LuAX0fYcXL5NegehoJgVDS6Pty_5IMCNSOJsxOlA7Aozt6uSrHfBMidMiBazGimyzpYuwrSD4hLJGvl63eWxTg', '', '', '', '', 'Active', 'ABC', NULL),
	('13', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMyJ9.Qxov8_EnUvTticKIO0nxaCedNrOrBZCWyjKx1M0QoUx9DiuAtSL6uRcx8dpV14lyQj3JoQtGLmWcdboYAutugg', '', '', '', '', 'Active', 'ABC', NULL),
	('14', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('15', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNSJ9.HvX4nZoaSTU8t8TUSbfmZkPGPtX8y4iIMJoarUvbhNuU_B3kZzPOwjS-dBg9ehz9FGUzavqocdLwRbm4tIK2QQ', '5T9GT2', '61a1dde2e58dd24f7ed13eb3f0f4c7a258371944d3baf5a851a56a19dce9189a', 'profile settings activity location sleep', 'fitbit', 'Active', 'ABC', 'askdjfn4ho23h3oth'),
	('16', '', '', '', '', '', 'Not Active', 'ABC', 'askdjfn4ho23h3oth'),
	('17', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('18', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('19', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('20', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('21', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMSJ9.oUKI4DJVQGzUeljxVTt0YPM92oZi2DGtGHVXpCUCQ4u1aPKbIkyPxnplNVT1HXrmJuOYIDlwsdgciwShOJdigQ', '', '', '', '', 'Active', 'ABC', NULL),
	('22', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('23', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMyJ9.hrUFnLjHdBD0y9XQ6qJf0RJAfHCMwywCwK6dPx3GEEIELctD--BPJeGWY1tp4Are9HRh6dqGIP9XUwCFvZfgMQ', '', '', '', '', 'Active', 'ABC', NULL),
	('24', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('25', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('26', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('27', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('28', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('29', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('3', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('30', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('31', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('32', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMiJ9.ihcIAfwrgeHJgINPBGEQPGBzhy6gCH948KQJ4rx_04MotQROCztv7zSwcVkFWCzb_XSNRFMVVLN1c3HXq7tQbw', '', '', '', '', 'Active', 'ABC', NULL),
	('33', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMyJ9.yFEud22eRjY82xtEszETuYd9CjQKO5Irl__V-OtDXoNvlARz0VpQ4Fv96LHpX-3BS9XqAopUcwo3_DpDQ-7kCg', '63QW7N', '411bfdfe46c0497e4d43820612dea70f9fe31b314903b90e6e23bc068ed4b124', 'settings sleep activity profile location', 'fitbit', 'Active', 'ABC', NULL),
	('34', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('35', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('36', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzNiJ9.KogX0kTAkLWyXwJqIDPaCYsxKs8MYHHAlnmd_l9rbL-avVGkiN-lQHZqzTVhnwTNMPiQ_9Z0frB2pMUOSxMhUw', '', '', '', '', 'Active', 'ABC', NULL),
	('37', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzNyJ9.YbxMrpYYDbUusuvivOWnoVx8-ugoUdnX2Ur8qFnw0L-Eh5mv5okEHBGoLOxrqi9oU8QUZNgx1_XPO-JidfbbRg', '63QW7N', '496568d5a6fe8b5a01a9950a372a91180e698342ec7c440d8a31724a04df2a1d', 'settings activity profile location sleep', 'fitbit', 'Active', 'ABC', NULL),
	('38', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzOCJ9.PYAzJMbg_aZllzhnClfMjfcVcDzFJmP4JTROqidhW-T93Oiu0JbWF2E3ePQN5n6XmvNTxrs78ZQE8xysgsg4xA', '', '', '', '', 'Active', 'ABC', NULL),
	('39', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzOSJ9.YySFmDcaRK2VO4rMGLpmdqTVyRqdlnzXi3rllWQhgFVXbFTEdFsDtWjlm-pyqVtoTyLiAfrU2D6L9_VQ7mT5nA', '5T9GT2', 'c014b9aff7d2c8f1d0b67dddcf473cffe0f6179e396e39d49708dd731acf556a', 'sleep settings location profile activity', 'fitbit', 'Active', 'ABC', NULL),
	('4', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('40', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('41', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('42', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('43', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('44', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0NCJ9.8ha1MCKdI3LhDWtDOGOm79SXAbLCOvnRjfOOkoov2_UONaOCSUfEOmbfTO-7GTrsQll3ZsdKSz1Uj7TRV_K9eA', '63QW7N', 'bcb74d28ebbef1f2502dcd689fba4fe455cdbba1744f95163dfcfe8ee926bec8', 'activity settings location profile sleep', 'fitbit', 'Active', 'ABC', NULL),
	('45', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('46', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('47', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('48', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('49', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0OSJ9.hnENirZ6uz-6bcpTldGgbRNIhzSPfIMz0GTzypb7iejnBTqhZ4njIal-f5RDmVjpmSBb-e197BIYpkqGgDGKMw', '', '', '', '', 'Active', 'ABC', NULL),
	('5', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('50', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('51', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('52', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('53', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('54', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('6', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('7', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('8', '', '', '', '', '', 'Not Active', 'ABC', NULL),
	('9', '', '', '', '', '', 'Not Active', 'ABC', NULL);
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;

-- Dumping structure for table pat.rss_feeds
CREATE TABLE IF NOT EXISTS `rss_feeds` (
  `feed_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `url` varchar(2000) NOT NULL,
  `color` varchar(20) NOT NULL,
  PRIMARY KEY (`feed_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

-- Dumping data for table pat.rss_feeds: ~9 rows (approximately)
/*!40000 ALTER TABLE `rss_feeds` DISABLE KEYS */;
INSERT INTO `rss_feeds` (`feed_id`, `title`, `url`, `color`) VALUES
	(1, 'Healthline', 'https://www.healthline.com/rss/health-news', '#05a2d3'),
	(2, 'Ovarian Cancer', 'https://rss.medicalnewstoday.com/ovariancancer.xml', '#ecb8ef'),
	(3, 'MedlinePlus', 'https://medlineplus.gov/feeds/news_en.xml', '#39b54a'),
	(4, 'MedPageToday', 'https://www.medpagetoday.com/rss/Headlines.xml', '#00235f'),
	(12, 'arpit.arora@fivium.com.au', 'gmail test', 'NK is here'),
	(13, 'arpit.arora@fivium.com.au', 'gmail test', 'NK is here'),
	(14, 'arpit.arora@fivium.com.au', 'gmail test', 'NK is here'),
	(15, 'arpit.arora@fivium.com.au', 'gmail test', 'NK is here'),
	(16, 'arpit.arora@fivium.com.au', 'gmail test', 'NK is here');
/*!40000 ALTER TABLE `rss_feeds` ENABLE KEYS */;

-- Dumping data for table pat_internal.clinicians: ~2 rows (approximately)
/*!40000 ALTER TABLE `clinicians` DISABLE KEYS */;
INSERT INTO `clinicians` (`Email`, `Password`, `Firstname`, `Lastname`, `Token`, `Role`) VALUES
  ('clayton.blake@fivium.com.au', '$2a$10$25s97I7dNEop5MKlbuayDuyBXkOSGXErxZSSxSkbgKV9oYvOq03fa', 'Bat', 'Man', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbGF5dG9uLmJsYWtlQGZpdml1bS5jb20uYXUifQ.K7skx6mGBjhbVd_l6ttE-LxDmte4ZqUldME2QpygghMzND_hKNK1hlm6wclfLXGG87GdkUxws4X9w0TpqTDWdA', 'superuser'),
  ('sampleuser@email.com ', '$2a$10$EdZN6K6Gd8fSpYMVL/yGB.lXUgZ1XDDlDscwQ85GvPwOIaSmplwmq', 'FirstName', 'LastName', '0', 'superuser');
/*!40000 ALTER TABLE `clinicians` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

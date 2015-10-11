--
-- Current Database: `orders`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `orders` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `orders`;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer` (
  `ID` int(11) NOT NULL,
  `FIRST_NAME` varchar(50) default NULL,
  `LAST_NAME` varchar(50) default NULL,
  `ADDRESS` varchar(150) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1054058776,'alex','no','no'),(891166698,'Alexander','Toschev','as');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `orders` (
  `ID` int(11) NOT NULL,
  `CUST_ID` int(11) default NULL,
  `DATE_PLACED` date default NULL,
  `AMOUNT` int(11) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1054019230,1054058776,'2010-10-10',2),(891146281,891166698,'2007-10-10',2);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;


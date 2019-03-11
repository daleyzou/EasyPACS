/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : dbdicom

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 11/03/2019 15:09:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tbl_equipment
-- ----------------------------
DROP TABLE IF EXISTS `tbl_equipment`;
CREATE TABLE `tbl_equipment`  (
  `pkTBLEquipmentID` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversionType` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createdDate` datetime(0) NULL DEFAULT NULL,
  `deviceSerialNumber` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `institutionAddress` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `institutionName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `institutionalDepartmentName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `manufacturer` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `manufacturerModelName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modality` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifiedDate` datetime(0) NULL DEFAULT NULL,
  `softwareVersion` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stationName` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pkTBLSeriesID` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`pkTBLEquipmentID`) USING BTREE,
  INDEX `FK_tbl_equipment_pkTBLSeriesID`(`pkTBLSeriesID`) USING BTREE,
  CONSTRAINT `FK_tbl_equipment_pkTBLSeriesID` FOREIGN KEY (`pkTBLSeriesID`) REFERENCES `tbl_series` (`pkTBLSeriesID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tbl_instance
-- ----------------------------
DROP TABLE IF EXISTS `tbl_instance`;
CREATE TABLE `tbl_instance`  (
  `pkTBLInstanceID` bigint(20) NOT NULL AUTO_INCREMENT,
  `acquisitionDateTime` datetime(0) NULL DEFAULT NULL,
  `contentDateTime` datetime(0) NULL DEFAULT NULL,
  `createdDate` datetime(0) NULL DEFAULT NULL,
  `exposureTime` int(11) NULL DEFAULT NULL,
  `imageOrientation` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `imagePosition` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `imageType` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `instanceNumber` int(11) NULL DEFAULT NULL,
  `kvp` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mediaStorageSopInstanceUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifiedDate` datetime(0) NULL DEFAULT NULL,
  `patientOrientation` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pixelSpacing` float NULL DEFAULT NULL,
  `sliceLocation` float NULL DEFAULT NULL,
  `sliceThickness` float NULL DEFAULT NULL,
  `sopClassUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sopInstanceUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `transferSyntaxUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `windowCenter` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `windowWidth` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `xrayTubeCurrent` int(11) NULL DEFAULT NULL,
  `pkTBLSeriesID` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`pkTBLInstanceID`) USING BTREE,
  INDEX `FK_tbl_instance_pkTBLSeriesID`(`pkTBLSeriesID`) USING BTREE,
  CONSTRAINT `FK_tbl_instance_pkTBLSeriesID` FOREIGN KEY (`pkTBLSeriesID`) REFERENCES `tbl_series` (`pkTBLSeriesID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tbl_patient
-- ----------------------------
DROP TABLE IF EXISTS `tbl_patient`;
CREATE TABLE `tbl_patient`  (
  `pkTBLPatientID` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime(0) NULL DEFAULT NULL,
  `modifiedDate` datetime(0) NULL DEFAULT NULL,
  `patientAge` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `patientBirthday` date NULL DEFAULT NULL,
  `patientID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `patientName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `patientSex` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pkTBLPatientID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tbl_series
-- ----------------------------
DROP TABLE IF EXISTS `tbl_series`;
CREATE TABLE `tbl_series`  (
  `pkTBLSeriesID` bigint(20) NOT NULL AUTO_INCREMENT,
  `bodyPartExamined` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createdDate` datetime(0) NULL DEFAULT NULL,
  `laterality` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifiedDate` datetime(0) NULL DEFAULT NULL,
  `operatorsName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `patientPosition` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `protocolName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `seriesDateTime` datetime(0) NULL DEFAULT NULL,
  `seriesDescription` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `seriesInstanceUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `seriesNumber` int(11) NULL DEFAULT NULL,
  `pkTBLStudyID` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`pkTBLSeriesID`) USING BTREE,
  INDEX `FK_tbl_series_pkTBLStudyID`(`pkTBLStudyID`) USING BTREE,
  CONSTRAINT `FK_tbl_series_pkTBLStudyID` FOREIGN KEY (`pkTBLStudyID`) REFERENCES `tbl_study` (`pkTBLStudyID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tbl_study
-- ----------------------------
DROP TABLE IF EXISTS `tbl_study`;
CREATE TABLE `tbl_study`  (
  `pkTBLStudyID` bigint(20) NOT NULL AUTO_INCREMENT,
  `accessionNumber` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `additionalPatientHistory` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `admittingDiagnosesDescription` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createdDate` datetime(0) NULL DEFAULT NULL,
  `modifiedDate` datetime(0) NULL DEFAULT NULL,
  `referringPhysicianName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `studyDateTime` datetime(0) NULL DEFAULT NULL,
  `studyDescription` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `studyID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `studyInstanceUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `studyPriorityID` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `studyStatusID` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pkTBLPatientID` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`pkTBLStudyID`) USING BTREE,
  INDEX `FK_tbl_study_pkTBLPatientID`(`pkTBLPatientID`) USING BTREE,
  CONSTRAINT `FK_tbl_study_pkTBLPatientID` FOREIGN KEY (`pkTBLPatientID`) REFERENCES `tbl_patient` (`pkTBLPatientID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

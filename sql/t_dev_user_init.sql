/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50529
Source Host           : localhost:3306
Source Database       : pyl_dev

Target Server Type    : MYSQL
Target Server Version : 50529
File Encoding         : 65001

Date: 2017-12-24 15:44:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_dev_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_dev_role`;
CREATE TABLE `t_dev_role` (
  `id` int(64) DEFAULT NULL,
  `role` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_dev_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_dev_role_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_dev_role_user`;
CREATE TABLE `t_dev_role_user` (
  `roleid` int(64) NOT NULL,
  `userid` int(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_dev_role_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_dev_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_dev_user`;
CREATE TABLE `t_dev_user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `cnname` varchar(40) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `status` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_dev_user
-- ----------------------------

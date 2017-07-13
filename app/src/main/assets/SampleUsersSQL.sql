BEGIN TRANSACTION;
CREATE TABLE `Users` (
	`Name`	TEXT,
	`Email`	TEXT,
	`Gender`	TEXT,
	`CurrentEvent`	TEXT,
	PRIMARY KEY(`Name`)
);
INSERT INTO `Users` VALUES ('Isabella
','isabella@gmail.com','F','KA Party');
INSERT INTO `Users` VALUES ('Amulya','amulya@gmail.com','F','KA Party');
INSERT INTO `Users` VALUES ('Bruce','bruce@gmail.com','M','Homework Party');
INSERT INTO `Users` VALUES ('Abhik','abhik@gmail.com','M','Menlo Park');
INSERT INTO `Users` VALUES ('Vinny','vinny@gmail.com','M','Homework Party');
INSERT INTO `Users` VALUES ('Robel','robel@gmail.com','M','Menlo Park');
INSERT INTO `Users` VALUES ('India','india@gmail.com','F','KA Party');
INSERT INTO `Users` VALUES ('Nicki','nicki@gmail.com','F','KA Party');
INSERT INTO `Users` VALUES ('Ryan','ryan@gmail.com','M','KA Party');
INSERT INTO `Users` VALUES ('Ari','ari@gmail.com','F','KA Party');
INSERT INTO `Users` VALUES ('Chris','chris@gmail.com','M','KA Party');
INSERT INTO `Users` VALUES ('Joseph','joseph@gmail.com','M','KA Party');
INSERT INTO `Users` VALUES ('Emily','emily@gmail.com','F','Texas Party');
INSERT INTO `Users` VALUES ('Ambria','ambria@gmail.com','F ','Texas Party');
INSERT INTO `Users` VALUES ('Daniel','daniel@gmail.com','M','Menlo Park');
INSERT INTO `Users` VALUES ('Mark','zuck@gmail.com','M','Facebook Party');
INSERT INTO `Users` VALUES ('Test','test@gmail.com','M','Test Party');
COMMIT;

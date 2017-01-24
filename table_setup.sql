
DROP TABLE fileservicedata;

/*
 * The nested heirarchy table
 */
CREATE TABLE fileservicedata (
 id INT PRIMARY KEY AUTO_INCREMENT,
 password VARBINARY(1024) DEFAULT NULL,
 salt VARBINARY(32) DEFAULT NULL,
 create_date DATETIME DEFAULT NULL
);

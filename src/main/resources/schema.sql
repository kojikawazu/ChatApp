CREATE TABLE chat_user
(
   id INT NOT NULL AUTO_INCREMENT,
   name VARCHAR(20) NOT NULL,
   email VARCHAR(20) NOT NULL,
   passwd VARCHAR(20) NOT NULL,
   forgot_passwd VARCHAR(20) NOT NULL,
   created DATETIME NOT NULL,
   updated DATETIME NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE chat_room
(
   id INT NOT NULL AUTO_INCREMENT,
   name VARCHAR(20) NOT NULL,
   comment VARCHAR(50) NOT NULL,
   tag VARCHAR(20) NOT NULL,
   max_roomsum INT NOT NULL,
   user_id INT NOT NULL,
   created DATETIME NOT NULL,
   updated DATETIME NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE chat_comment
(
   id INT NOT NULL AUTO_INCREMENT,
   comment VARCHAR(50) NOT NULL,
   room_id INT NOT NULL,
   user_id INT NOT NULL,
   created DATETIME NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE chat_login
(
   id INT NOT NULL AUTO_INCREMENT,
   room_id INT NOT NULL,
   user_id INT NOT NULL,
   created DATETIME NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE chat_enter
(
   id INT NOT NULL AUTO_INCREMENT,
   room_id INT NOT NULL,
   user_id INT NOT NULL,
   manager_id INT NOT NULL,
   max_sum INT NOT NULL,
   created DATETIME NOT NULL,
   PRIMARY KEY(id)
);
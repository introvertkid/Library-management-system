drop database if exists librarydb;

create database if not exists librarydb;

use librarydb;

CREATE TABLE IF NOT EXISTS tags (
    tagID INT AUTO_INCREMENT primary key,
    tagName VARCHAR(255) not null unique
);

CREATE TABLE IF NOT EXISTS documents (
    documentID INT AUTO_INCREMENT primary key,
    documentName VARCHAR(255) not null,
    tagID INT,
    authors longtext,
    fileName varchar(255) not null unique,
    FOREIGN KEY (tagID) REFERENCES tags(tagID) ON UPDATE CASCADE
);

create table if not exists document_tag_bridge(
	documentID int not null,
    tagID int not null,
    primary key(documentID, tagID),
    foreign key(documentID) references documents(documentID) on update cascade,
	foreign key(tagID) references tags(tagID) on update cascade
);

CREATE TABLE IF NOT EXISTS users (
    userID INT AUTO_INCREMENT primary key,
    username varchar(255) not null unique,
    hashedPassword varchar(255) not null,
    userFullName VARCHAR(100) not null,
    role varchar(100) default 'Normal user',
    gmail VARCHAR(100),
    phoneNumber VARCHAR(15),
-- 	borrowingID INT,
    dateOfBirth DATE,
    avatar VARCHAR(255) default 'userAvatar.png'
--     foreign key (borrowingID) references borrowings(borrowingID) on update cascade
);

CREATE TABLE IF NOT EXISTS documentOwner (
    documentID INT,
    ownerID INT,
    PRIMARY KEY (documentID, ownerID),
    FOREIGN KEY (documentID) REFERENCES documents(documentID) ON UPDATE CASCADE,
    FOREIGN KEY (ownerID) REFERENCES users(userID) ON UPDATE CASCADE
);

-- CREATE TABLE IF NOT EXISTS borrowings (
--     borrowingID INT AUTO_INCREMENT,
--     userID INT,
--     documentID INT,
--     borrowDate DATETIME,
--     dueDate DATE,
--     returnDate DATETIME,
--     borrowingStatus VARCHAR(20),
--     PRIMARY KEY (borrowingID),
--     FOREIGN KEY (documentID) REFERENCES documents(documentID) ON UPDATE CASCADE
-- );

create table if not exists reports(
	reportID int auto_increment primary key,
	userID int,
    reportType varchar(10),
    title varchar(255),
    content longtext,
    foreign key (userID) references users(userID) on update cascade
);

#insert value for users
insert into users(username, hashedPassword, userFullName) value('admin', 'admin', 'admin');

#insert value for tags
insert into tags(tagName)
values ('javafx'), ('java'), ('c'), ('cpp'), ('c++'), ('regex'), ('document');

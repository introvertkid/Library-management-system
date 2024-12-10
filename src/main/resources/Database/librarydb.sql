drop database if exists librarydb;

create database if not exists librarydb;

use librarydb;

CREATE TABLE IF NOT EXISTS tags (
    tagID INT AUTO_INCREMENT primary key,
    tagName VARCHAR(255) not null unique
);

CREATE TABLE IF NOT EXISTS documents (
    documentID INT AUTO_INCREMENT primary key,
    documentName VARCHAR(255) not null unique,
    authors longtext,
    description longtext,
    fileName varchar(255) not null unique,
    quantity INT NOT NULL DEFAULT 1,
    status varchar(10) not null default 'Pending'
);

create table if not exists document_tag(
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
    role varchar(100) default 'normal user',
    gmail VARCHAR(100),
    phoneNumber VARCHAR(15),
    dateOfBirth DATE,
    avatar VARCHAR(255) default 'userAvatar.png'
);

CREATE TABLE IF NOT EXISTS documentOwner (
    documentID INT,
    ownerID INT,
    PRIMARY KEY (documentID, ownerID),
    FOREIGN KEY (documentID) REFERENCES documents(documentID) ON UPDATE CASCADE,
    FOREIGN KEY (ownerID) REFERENCES users(userID) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS borrowings (
  userName VARCHAR(255) NOT NULL,
  documentName VARCHAR(255) NOT NULL,
  returned BOOLEAN DEFAULT FALSE
);

create table if not exists reports(
	reportID int auto_increment primary key,
	userID int,
    reportType varchar(10),
    title varchar(255),
    content longtext,
    status varchar(10) not null default 'Pending',
    foreign key (userID) references users(userID) on update cascade
);

create table if not exists comments(
	commentID int auto_increment primary key,
    documentID int,
    userID int,
    content varchar(255),
    foreign key (documentID) references documents(documentID) on update cascade,
    foreign key (userID) references users(userID) on update cascade
);

#insert value for users
insert into users(username, hashedPassword, userFullName) value('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin');

#insert value for tags
insert into tags(tagName)
values ('javafx'), ('java'), ('c'), ('cpp'), ('c++'), ('regex'), ('document'), ('uet'), ('hust'), ('final exam'), ('mid exam'), ('dsa'),
		('vnu'), ('linear algebra'), ('calculus'), ('mysql'), ('oop'), ('math'), ('physics'), ('machine learning');


INSERT INTO documents (documentName, authors, fileName, quantity,status, description)
VALUES
('Đề thi OOP', 'VNU', 'oop.pdf', 10, 'Available', 'Đề thi cuối kì Lập trình hướng đối tượng HK1 2020 - 2021'),
('Đề thi XSTK', 'VNU', 'xstk.pdf', 10, 'Available', 'Đề thi giữa kì môn xác suất thống kê năm học 2020 - 2021 Học kỳ hè'),
('Đề thi DSA', 'VNU', 'dsa.docx', 10, 'Available', 'Đề thi học phần cấu trúc dữ liệu và giải thuật HK1 NĂM HỌC 2021 – 2022');

#insert documents
INSERT INTO documents (documentName, authors, fileName, quantity,status)
VALUES
('The Catcher in the Rye', 'J.D. Salinger', 'catcher_in_the_rye.pdf', 10, 'Available'),
('To Kill a Mockingbird', 'Harper Lee', 'to_kill_a_mockingbird.pdf', 10, 'Available'),
('1984', 'George Orwell', '1984.pdf', 10, 'Available'),
('Pride and Prejudice', 'Jane Austen', 'pride_and_prejudice.pdf', 10, 'Available'),
('The Great Gatsby', 'F. Scott Fitzgerald', 'the_great_gatsby.pdf', 10, 'Available'),
('Moby-Dick', 'Herman Melville', 'moby_dick.pdf', 10, 'Available'),
('War and Peace', 'Leo Tolstoy', 'war_and_peace.pdf', 10, 'Available'),
('The Odyssey', 'Homer', 'the_odyssey.pdf', 10, 'Available'),
('Jane Eyre', 'Charlotte Brontë', 'jane_eyre.pdf', 10, 'Available'),
('Wuthering Heights', 'Emily Brontë', 'wuthering_heights.pdf', 10, 'Available'),
('The Hobbit', 'J.R.R. Tolkien', 'the_hobbit.pdf', 10, 'Available'),
('Brave New World', 'Aldous Huxley', 'brave_new_world.pdf', 10, 'Available'),
('Fahrenheit 451', 'Ray Bradbury', 'fahrenheit_451.pdf', 10, 'Available'),
('The Lord of the Rings: The Fellowship of the Ring', 'J.R.R. Tolkien', 'lord_of_the_rings_1.pdf', 10, 'Available'),
('The Lord of the Rings: The Two Towers', 'J.R.R. Tolkien', 'lord_of_the_rings_2.pdf', 10, 'Available'),
('The Lord of the Rings: The Return of the King', 'J.R.R. Tolkien', 'lord_of_the_rings_3.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Lion, the Witch and the Wardrobe', 'C.S. Lewis', 'narnia_1.pdf', 10, 'Available'),
('The Chronicles of Narnia: Prince Caspian', 'C.S. Lewis', 'narnia_2.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Voyage of the Dawn Treader', 'C.S. Lewis', 'narnia_3.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Silver Chair', 'C.S. Lewis', 'narnia_4.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Last Battle', 'C.S. Lewis', 'narnia_5.pdf', 10, 'Available');

#insert tag
INSERT INTO document_tag (documentID, tagID)
VALUES
('1','10'),
('1','17'),
('1','8'),
('2', '11'),
('2', '18'),
('3', '12'),
('3', '10');

UPDATE users SET role = "Admin" WHERE username = "admin";

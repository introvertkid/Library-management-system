use librarydb;
CREATE TABLE IF NOT EXISTS categories (
    categoryID INT AUTO_INCREMENT,
    categoryName VARCHAR(100),
    PRIMARY KEY (categoryID)
);

CREATE TABLE IF NOT EXISTS authors (
    authorID INT AUTO_INCREMENT,
    authorName VARCHAR(255),
    PRIMARY KEY (authorID)
);

CREATE TABLE IF NOT EXISTS documents (
    documentID INT AUTO_INCREMENT,
    documentName VARCHAR(255),
    categoryID INT,
    copies INT,
    PRIMARY KEY (documentID),
    FOREIGN KEY (categoryID) REFERENCES categories(categoryID) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS document_authors (
    documentID INT,
    authorID INT,
    PRIMARY KEY (documentID, authorID),
    FOREIGN KEY (documentID) REFERENCES documents(documentID) ON UPDATE CASCADE,
    FOREIGN KEY (authorID) REFERENCES authors(authorID) ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS borrowings (
    borrowingID INT AUTO_INCREMENT,
    userID INT,
    documentID INT,
    borrowDate DATETIME,
    dueDate DATE,
    returnDate DATETIME,
    borrowingStatus VARCHAR(20),
    PRIMARY KEY (borrowingID),
    FOREIGN KEY (documentID) REFERENCES documents(documentID) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    userID INT AUTO_INCREMENT,
    userName VARCHAR(100),
    gmail VARCHAR(100),
    phoneNumber VARCHAR(10),
	borrowingID INT,
    accountID INT,
    dateOfBirth DATE,
    PRIMARY KEY (userID),
    foreign key (borrowingID) references borrowings(borrowingID) on update cascade
);



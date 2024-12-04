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
    tagID INT,
    authors longtext,
    fileName varchar(255) not null unique,
    quantity INT NOT NULL DEFAULT 1,
    status varchar(10) not null default 'Pending',
    FOREIGN KEY (tagID) REFERENCES tags(tagID) ON UPDATE CASCADE
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
--     foreign key (borrowingID) references borrowings(borrowingID) on update cascade
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
values ('javafx'), ('java'), ('c'), ('cpp'), ('c++'), ('regex'), ('document');

#insert documents
INSERT INTO documents (documentName, tagID, authors, fileName, quantity,status)
VALUES
('The Catcher in the Rye', 1, 'J.D. Salinger', 'catcher_in_the_rye.pdf', 10, 'Available'),
('To Kill a Mockingbird', 1, 'Harper Lee', 'to_kill_a_mockingbird.pdf', 10, 'Available'),
('1984', 1, 'George Orwell', '1984.pdf', 10, 'Available'),
('Pride and Prejudice', 1, 'Jane Austen', 'pride_and_prejudice.pdf', 10, 'Available'),
('The Great Gatsby', 1, 'F. Scott Fitzgerald', 'the_great_gatsby.pdf', 10, 'Available'),
('Moby-Dick', 1, 'Herman Melville', 'moby_dick.pdf', 10, 'Available'),
('War and Peace', 1, 'Leo Tolstoy', 'war_and_peace.pdf', 10, 'Available'),
('The Odyssey', 1, 'Homer', 'the_odyssey.pdf', 10, 'Available'),
('Jane Eyre', 1, 'Charlotte Brontë', 'jane_eyre.pdf', 10, 'Available'),
('Wuthering Heights', 1, 'Emily Brontë', 'wuthering_heights.pdf', 10, 'Available'),
('The Hobbit', 1, 'J.R.R. Tolkien', 'the_hobbit.pdf', 10, 'Available'),
('Brave New World', 1, 'Aldous Huxley', 'brave_new_world.pdf', 10, 'Available'),
('Fahrenheit 451', 1, 'Ray Bradbury', 'fahrenheit_451.pdf', 10, 'Available'),
('The Lord of the Rings: The Fellowship of the Ring', 1, 'J.R.R. Tolkien', 'lord_of_the_rings_1.pdf', 10, 'Available'),
('The Lord of the Rings: The Two Towers', 1, 'J.R.R. Tolkien', 'lord_of_the_rings_2.pdf', 10, 'Available'),
('The Lord of the Rings: The Return of the King', 1, 'J.R.R. Tolkien', 'lord_of_the_rings_3.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Lion, the Witch and the Wardrobe', 1, 'C.S. Lewis', 'narnia_1.pdf', 10, 'Available'),
('The Chronicles of Narnia: Prince Caspian', 1, 'C.S. Lewis', 'narnia_2.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Voyage of the Dawn Treader', 1, 'C.S. Lewis', 'narnia_3.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Silver Chair', 1, 'C.S. Lewis', 'narnia_4.pdf', 10, 'Available'),
('The Chronicles of Narnia: The Last Battle', 1, 'C.S. Lewis', 'narnia_5.pdf', 10, 'Available'),
('The Da Vinci Code', 1, 'Dan Brown', 'da_vinci_code.pdf', 10, 'Available'),
('Angels & Demons', 1, 'Dan Brown', 'angels_and_demons.pdf', 10, 'Available'),
('The Hunger Games', 1, 'Suzanne Collins', 'hunger_games.pdf', 10, 'Available'),
('Catching Fire', 1, 'Suzanne Collins', 'catching_fire.pdf', 10, 'Available'),
('Mockingjay', 1, 'Suzanne Collins', 'mockingjay.pdf', 10, 'Available'),
('The Maze Runner', 1, 'James Dashner', 'maze_runner.pdf', 10, 'Available'),
('The Scorch Trials', 1, 'James Dashner', 'scorch_trials.pdf', 10, 'Available'),
('The Death Cure', 1, 'James Dashner', 'death_cure.pdf', 10, 'Available'),
('Divergent', 1, 'Veronica Roth', 'divergent.pdf', 10, 'Available'),
('Insurgent', 1, 'Veronica Roth', 'insurgent.pdf', 10, 'Available'),
('Allegiant', 1, 'Veronica Roth', 'allegiant.pdf', 10, 'Available'),
('The Girl on the Train', 1, 'Paula Hawkins', 'girl_on_the_train.pdf', 10, 'Available'),
('Gone Girl', 1, 'Gillian Flynn', 'gone_girl.pdf', 10, 'Available'),
('Big Little Lies', 1, 'Liane Moriarty', 'big_little_lies.pdf', 10, 'Available'),
('The Help', 1, 'Kathryn Stockett', 'the_help.pdf', 10, 'Available'),
('The Fault in Our Stars', 1, 'John Green', 'fault_in_our_stars.pdf', 10, 'Available'),
('Looking for Alaska', 1, 'John Green', 'looking_for_alaska.pdf', 10, 'Available'),
('Turtles All the Way Down', 1, 'John Green', 'turtles_all_the_way_down.pdf', 10, 'Available'),
('The Alchemist', 1, 'Paulo Coelho', 'the_alchemist.pdf', 10, 'Available'),
('The Secret', 1, 'Rhonda Byrne', 'the_secret.pdf', 10, 'Available'),
('You Are a Badass', 1, 'Jen Sincero', 'you_are_a_badaass.pdf', 10, 'Available'),
('Atomic Habits', 1, 'James Clear', 'atomic_habits.pdf', 10, 'Available'),
('The Power of Habit', 1, 'Charles Duhigg', 'the_power_of_habit.pdf', 10, 'Available'),
('The Subtle Art of Not Giving a F*ck', 1, 'Mark Manson', 'subtle_art_of_not_giving_a_fuck.pdf', 10, 'Available'),
('Educated', 1, 'Tara Westover', 'educated.pdf', 10, 'Available'),
('Becoming', 1, 'Michelle Obama', 'becoming.pdf', 10, 'Available'),
('Sapiens', 1, 'Yuval Noah Harari', 'sapiens.pdf', 10, 'Available'),
('Homo Deus', 1, 'Yuval Noah Harari', 'homo_deus.pdf', 10, 'Available'),
('Grit', 1, 'Angela Duckworth', 'grit.pdf', 10, 'Available'),
('Outliers', 1, 'Malcolm Gladwell', 'outliers.pdf', 10, 'Available'),
('Blink', 1, 'Malcolm Gladwell', 'blink.pdf', 10, 'Available'),
('David and Goliath', 1, 'Malcolm Gladwell', 'david_and_goliath.pdf', 10, 'Available'),
('Freakonomics', 1, 'Steven D. Levitt', 'freakonomics.pdf', 10, 'Available'),
('Thinking, Fast and Slow', 1, 'Daniel Kahneman', 'thinking_fast_and_slow.pdf', 10, 'Available'),
('The 7 Habits of Highly Effective People', 1, 'Stephen R. Covey', '7_habits_highly_effective.pdf', 10, 'Available'),
('The Lean Startup', 1, 'Eric Ries', 'lean_startup.pdf', 10, 'Available'),
('Start with Why', 1, 'Simon Sinek', 'start_with_why.pdf', 10, 'Available');

UPDATE users SET role = "Admin" WHERE username = "admin";
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS user;

CREATE TABLE user(
    userName VARCHAR(20) PRIMARY KEY,
    password VARCHAR(256) NOT NULL,
    connected BOOLEAN DEFAULT false
);

CREATE TABLE friendship(
    userSender VARCHAR(20) NOT NULL,
    userReceiver VARCHAR(20) NOT NULL,
    confirmedFriendship BOOLEAN DEFAULT false,
    FOREIGN KEY (userSender) REFERENCES user(userName) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (userReceiver) REFERENCES user(userName) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY(userSender, userReceiver)
);

INSERT INTO user(userName, password) VALUES ('manu', sha2('21012000', 256));
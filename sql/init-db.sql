CREATE TABLE expense
(
    id         int(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amount     int(8)      NOT NULL,
    note       varchar(50) NOT NULL,
    category   varchar(50),
    date       date        NOT NULL,
    created_at datetime DEFAULT current_timestamp(),
    updated_at datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
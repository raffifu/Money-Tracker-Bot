CREATE TABLE expense (
    id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    amount int(8) NOT NULL,
    description varchar(100) NOT NULL,
    date date NOT NULL,
    created_at datetime DEFAULT current_timestamp(),
    updated_at datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
)
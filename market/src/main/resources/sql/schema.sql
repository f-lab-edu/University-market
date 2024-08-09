DROP TABLE IF EXISTS market.chat_member;
DROP TABLE IF EXISTS market.message;
DROP TABLE IF EXISTS market.chat;
drop table if exists market.offer;
drop table if exists market.dibs;
drop table if exists market.item;
drop table if exists market.member;
drop table if exists market.email;

create table member(
      id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
      name varchar(255) NOT NULL,
      email varchar(255) NOT NULL UNIQUE,
      password varchar(255) NOT NULL,
      university int NOT NULL,
      auth enum('ROLE_USER','ROLE_VERIFY_USER','ROLE_ADMIN') NOT NULL,
      member_status enum('ONLINE', 'OFFLINE') NOT NULL,
      created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      is_deleted boolean NOT NULL DEFAULT FALSE
);

create table email(
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email varchar(255) NOT NULL,
    verification_code varchar(255) NOT NULL,
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table item(
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title varchar(255) NOT NULL,
    description TEXT NOT NULL,
    image_url varchar(255) NOT NULL,
    seller bigint NOT NULL,
    status enum('SELLING', 'TRADING', 'FINISH') NOT NULL,
    auction boolean NOT NULL,
    price int NOT NULL,
    is_deleted boolean NOT NULL,
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller) REFERENCES member(id)
);

create table offer(
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item bigint NOT NULL,
    buyer bigint NOT NULL,
    price int NOT NULL,
    status enum('OFFER', 'ACCEPT', 'DECLINE', 'CANCEL') NOT NULL,
    is_deleted boolean NOT NULL,
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (item) REFERENCES item(id),
    FOREIGN KEY (buyer) REFERENCES member(id)
);

create table dibs(
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item bigint NOT NULL,
    member bigint NOT NULL,
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (item) REFERENCES item(id),
    FOREIGN KEY (member) REFERENCES member(id)
);

CREATE TABLE chat (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    item BIGINT,
    is_deleted BOOLEAN NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (item) REFERENCES item(id)
);

CREATE TABLE chat_member (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chat_auth ENUM('GUEST', 'HOST') NOT NULL,
    chat BIGINT NOT NULL,
    member BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    last_read_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat) REFERENCES chat(id),
    FOREIGN KEY (member) REFERENCES member(id)
);

CREATE TABLE message (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chat BIGINT NOT NULL,
    sender BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat) REFERENCES chat(id),
    FOREIGN KEY (sender) REFERENCES member(id)
);

CREATE INDEX idx_item_id_is_deleted ON item (id, is_deleted);
CREATE INDEX idx_chat_id_is_deleted ON chat (id, is_deleted);

SET GLOBAL event_scheduler = ON;

CREATE EVENT delete_old_emails
ON SCHEDULE EVERY 1 MINUTE
DO
DELETE FROM email
WHERE created_at < NOW() - INTERVAL 1 MINUTE;
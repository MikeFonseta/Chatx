CREATE TABLE IF NOT EXISTS User_account
(
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS Chat_room
(
    chat_room_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_room_name VARCHAR NOT NULL,
    room_owner INTEGER,
    CONSTRAINT fk_owner FOREIGN KEY(room_owner)
    REFERENCES User_account(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Join_requests
(
    user_id INTEGER,
    chat_room INTEGER,
    accepted BOOLEAN,
    CONSTRAINT fk_user FOREIGN KEY(user_id)
    REFERENCES User_account(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_chat FOREIGN KEY(chat_room)
    REFERENCES Chat_room(chat_room_id) ON DELETE CASCADE
);

ALTER TABLE Join_requests ADD PRIMARY KEY (user_id, chat_room);

CREATE TABLE IF NOT EXISTS Message
(
    message_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender INTEGER,
    chat INTEGER,
    message_content VARCHAR,
    sending_time TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY(sender)
    REFERENCES User_account(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_chat FOREIGN KEY(chat)
    REFERENCES Chat_room(chat_room_id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION add_owner() RETURNS TRIGGER LANGUAGE PLPGSQL AS $$
    BEGIN
        INSERT INTO Join_requests VALUES (NEW.room_owner, NEW.chat_room_id, true);
        RETURN NEW;
    END;
    $$;

CREATE OR REPLACE TRIGGER join_owner
AFTER INSERT ON chat_room
FOR EACH ROW
EXECUTE PROCEDURE add_owner();
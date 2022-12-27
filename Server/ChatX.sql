CREATE TABLE User_account
(
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

CREATE TABLE Chat_room
(
    chat_room_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_room_name VARCHAR NOT NULL,
    room_owner INTEGER,
    CONSTRAINT fk_owner FOREIGN KEY(room_owner)
    REFERENCES User_account(user_id) ON DELETE CASCADE
);

CREATE TABLE Join_requests
(
    user_id INTEGER,
    chat_room INTEGER,
    accepted BOOLEAN,
    CONSTRAINT fk_user FOREIGN KEY(user_id)
    REFERENCES User_account(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_chat FOREIGN KEY(chat_room)
    REFERENCES Chat_room(chat_room_id) ON DELETE CASCADE
);

CREATE TABLE Message
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
)
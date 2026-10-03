CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE requests (
                          id UUID PRIMARY KEY,
                          title VARCHAR(255),
                          description VARCHAR(255),
                          internal_request_category VARCHAR(255),
                          creation_date TIMESTAMP,
                          internal_request_status VARCHAR(255),
                          user_id UUID,
                          CONSTRAINT fk_requests_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
);
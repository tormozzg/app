CREATE TABLE users (
  id       BIGSERIAL    NOT NULL PRIMARY KEY,
  email    VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  enabled  BOOLEAN      NOT NULL,
  created  TIMESTAMP    NOT NULL,
  updated  TIMESTAMP    NOT NULL,
  CONSTRAINT uk_email_constraint UNIQUE (email)
);

CREATE TABLE roles (
  id      BIGSERIAL    NOT NULL  PRIMARY KEY,
  name    VARCHAR(100) NOT NULL,
  created TIMESTAMP    NOT NULL,
  CONSTRAINT uk_role_name UNIQUE (name)
);

CREATE TABLE users_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user__users_roles FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_role__users_roles FOREIGN KEY (role_id) REFERENCES roles(id)
)
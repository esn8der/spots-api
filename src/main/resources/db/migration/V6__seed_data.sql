-- plantas base
INSERT INTO planta (nombre)
VALUES ('banano'),
       ('coco'),
       ('palma'),
       ('piña'),
       ('platano');

-- usuarios demo
INSERT INTO usuario (nombre, email, password_hash)
VALUES ('Pedro', 'pedro@mail.com', '{argon2}hash'),
       ('Maria', 'maria@mail.com', '{argon2}hash'),
       ('Juan', 'juan@mail.com', '{argon2}hash'),
       ('Ana', 'ana@mail.com', '{argon2}hash'),
       ('Luis', 'luis@mail.com', '{argon2}hash');
-- insert admin (username a, password aa)


INSERT INTO IWUser (id, correo, downloaded, enabled,first,grado,nivel,password,roles,uploaded,username,titulacion_id)
VALUES (1,'pepe@ucm.es',10,TRUE,FALSE,2,3,'{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'ADMIN,USER',11, 'a',null);

INSERT INTO IWUser (id, correo, downloaded, enabled,first, grado,nivel,password,roles,uploaded,username,titulacion_id)
VALUES (2,'juan@ucm.es',5,TRUE,TRUE,1,1,'{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER',1100, 'b',null);


INSERT INTO Universidad (id, descripcion,localizacion,nombre)
VALUES (1,'UCM','Madrid','Universidad Complutense de Madrid');
INSERT INTO Universidad (id, descripcion,localizacion,nombre)
VALUES (2,'UPM','Madrid','Universidad Politécnica de Madrid');
INSERT INTO Universidad (id, descripcion,localizacion,nombre)
VALUES (3,'URJC','Madrid','Universidad Rey Juan Carlos');
INSERT INTO Universidad (id, descripcion,localizacion,nombre)
VALUES (4,'UPV','Valencia','Universidad Politécnica de Valencia');
INSERT INTO Universidad (id, descripcion,localizacion,nombre)
VALUES (5,'ULPGC','Las Palmas de Gran Canaria','Universidad de Las Palmas de Gran Canaria');
INSERT INTO Universidad (id, descripcion,localizacion,nombre)
VALUES (6,'UAM','Madrid','Universidad Autónoma de Madrid');

INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(1,'Ingenieria Informatica',1);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(2,'Ingenieria Software',1);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(3,'Ingenieria Computadores',1);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(4,'VideoJuegos',1);

INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(5,'Ingenieria Informatica',2);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(6,'Ingenieria Software',2);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(7,'Ingenieria Computadores',2);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(8,'VideoJuegos',2);

INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(9,'Ingenieria Informatica',3);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(10,'Ingenieria Software',3);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(11,'Ingenieria Computadores',3);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(12,'VideoJuegos',3);

INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(13,'Ingenieria Informatica',4);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(14,'Ingenieria Software',4);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(15,'Ingenieria Computadores',4);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(16,'VideoJuegos',4);

INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(17,'Ingenieria Informatica',5);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(18,'Ingenieria Software',5);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(19,'Ingenieria Computadores',5);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(20,'VideoJuegos',5);

INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(21,'Ingenieria Informatica',6);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(22,'Ingenieria Software',6);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(23,'Ingenieria Computadores',6);
INSERT INTO Titulacion (id,grado,universidad_id)
VALUES(24,'VideoJuegos',6);


-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;

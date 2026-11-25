CREATE TABLE Usuarios (
   
    ID BIGINT NOT NULL AUTO_INCREMENT,
    Rut VARCHAR(12) NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Correo VARCHAR(120) NOT NULL,
    Comuna VARCHAR(60) NOT NULL,
    Telefono VARCHAR(15),
    Pin_Hash VARCHAR(255) NOT NULL,
    Rol VARCHAR(10) NOT NULL,
    Activo BIT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_rut (Rut),
    UNIQUE KEY uk_usuarios_correo (Correo)

);
Create Table Reportes (

    ID          Bigint  Not Null Auto_Increment,
    Numero      Varchar(20),
    Compromiso_ID Bigint Not Null,
    Folio        Varchar(20),
    Usuario_ID  Bigint Not Null,
    Motivo      Varchar(20) Not Null,
    Comentario  Varchar(300),
    Estado      Varchar(15) Not Null,
    Respuesta   Varchar(300),
    Fecha       Date,

    Primary Key (ID),
    Unique Key UK_Reportes_Numero (Numero)

);
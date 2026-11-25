Create Table Recomendaciones (

    ID              Bigint Not Null Auto_Increment,
    Numero          Varchar(20),
    Usuario_ID      Bigint Not Null,
    Organizacion    Varchar(100) Not Null,
    Rubro           Varchar(60) Not Null,
    Comuna          Varchar(60) Not Null,
    Contacto        Varchar(100),
    Estado          Varchar(15) Not Null,
    Fecha           Date,
    Primary Key(ID),
    Unique Key UK_Recomendaciones_Numero (Numero)

);
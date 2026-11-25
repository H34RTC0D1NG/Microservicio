Create Table Comprobantes (

    ID                Bigint       Not Null Auto_Increment,
    Numero            Varchar(20)  Not Null,
    Compromiso_ID     Bigint       Not Null,
    Folio             Varchar(20)  Not Null,
    Usuario_ID        Bigint       Not Null,
    Empresa           Varchar(100) Not Null,
    Concepto          Varchar(150) Not Null,
    Monto             Int          Not Null,
    Fecha             Date         Not Null,
    Hora              Varchar(  5),
    Medio             Varchar( 30),
    Canal             Varchar( 20),
    Puntual               Bit(  1) Not Null,
    Primary Key (ID),
    Unique Key UK_Comprobantes_Numero (Numero),
    Constraint FK_Comprobante_Compromiso Foreign Key (Compromiso_ID) References Compromisos (ID) 

);
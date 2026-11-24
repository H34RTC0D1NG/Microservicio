Create Table Compromisos (

    ID          Bigint       Not Null Auto_Increment,
    Folio       Varchar(20),
    Usuario_ID  Bigint       Not Null,
    Empresa     Varchar(100) Not Null,
    Tipo        Varchar( 15) Not Null,
    Concepto    Varchar(100) Not Null,
    Cuota           Int      Not Null,
    Total_Cuotas    Int      Not Null,
    Monto           Int      Not Null,
    Vencimiento    Date      Not Null,
    Estado      Varchar( 15) Not Null,

    Primary Key(ID), 
    Unique Key UK_Compromisos_Folio(Folio)

);
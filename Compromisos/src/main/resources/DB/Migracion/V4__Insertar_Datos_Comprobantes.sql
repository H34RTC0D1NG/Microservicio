Insert Into Comprobantes (

    ID, Numero, Compromiso_ID, Folio, Usuario_ID, Empresa,
    Concepto, Monto, Fecha, Hora, Medio, Canal, Puntual

) Values (

    1, 'C-5001', 1, 'PF-1001', 2, 'Plaza Oriente', 'Arriendo Depto', 420000,
    Curdate() - Interval 38 Day, '10:15', 'Transferencia', 'App', 1

) , (

    2, 'C_5002', 4, 'PF-1004', 2, 'Caja los Andes', 'Crédito de Consumo', 89900,
    Curdate() - Interval 25 Day, '10:15', 'Transferencia', 'Presencial', 0

);
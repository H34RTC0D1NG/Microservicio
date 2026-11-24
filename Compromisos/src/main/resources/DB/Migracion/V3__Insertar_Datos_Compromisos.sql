Insert Into Compromisos (

    ID, Folio, Usuario_ID, Empresa, Tipo,
    Concepto, Cuota, Total_Cuotas, Monto,
    Vencimiento, Estado

) Values (

    1, 'PF-1001', 2, 'Plaza Oriente', 'Arriendo', 'Arriendo Depto', 2, 12, 
    420000, Curdate() - Interval 36 Day, 'Pagado'

), (

    2, 'PF-1002', 2, 'Plaza Oriente', 'Arriendo', 'Arriendo depto', 3, 12, 
    420000, CURDATE() - INTERVAL 6 DAY, 'Pendiente'
    
), (
    
    3, 'PF-1003', 2, 'Plaza Oriente', 'Arriendo', 'Arriendo depto', 4, 12, 
    420000, CURDATE() + INTERVAL 24 DAY, 'Pendiente'
    
), (
    
    4, 'PF-1004', 2, 'Caja Los Andes', 'Credito', 'Crédito de consumo', 6, 24, 
    89900, CURDATE() - INTERVAL 28 DAY, 'Pagado'
    
), (
    
    5, 'PF-1005', 2, 'Caja Los Andes', 'Credito', 'Crédito de consumo', 7, 24, 
    89900, CURDATE() + INTERVAL 2 DAY, 'Pendiente'
    
), (
    
    6, 'PF-1006', 2, 'Club Deportivo Las Mercedes', 'Club', 'Cuota social', 10, 12, 
    15000, CURDATE() + INTERVAL 9 DAY, 'Pendiente'
    
), (
    
    7, 'PF-1007', 2, 'Fundación Las Rosas', 'Fundacion', 'Aporte mensual', 0, 0, 
    10000, CURDATE() + INTERVAL 15 DAY, 'Pendiente'
    
);
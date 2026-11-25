Insert Into Usuarios (

    ID, Rut, Nombre, Correo, Comuna, 
    Telefono, Pin_Hash, Rol, Activo

) Values (
    
    1, '11111111-1', 'Administrador PagoFlex', 'admin@pagoflex.cl', 'Santiago', '', 
    '$2a$10$Z0U4JOOSdvFCuoD6Hvpnqe0Tcj7mk9obmqM/wEdJclDN1LnuY1sui', 'Admin', 1

) , (
    
    2, '15834207-3', 'Ana Rojas Pérez', 'ana.rojas@correo.cl', 'Independencia', '912345678', 
    '$2a$10$Z0U4JOOSdvFCuoD6Hvpnqe0Tcj7mk9obmqM/wEdJclDN1LnuY1sui', 'Cliente', 1
    
) , (
    
    3, '12345678-5', 'Pedro Soto Muñoz', 'pedro.soto@correo.cl', 'Recoleta', '', 
    '$2a$10$Z0U4JOOSdvFCuoD6Hvpnqe0Tcj7mk9obmqM/wEdJclDN1LnuY1sui', 'Cliente', 1
    
);

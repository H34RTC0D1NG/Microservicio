Insert Into Recomendaciones (

    ID, Numero, Usuario_ID, Organizacion,
    Rubro, Comuna, Contacto, Estado, Fecha

) Values (

    1, 'REC-101', 2, 'Jardín Infantil Los Peques', 'Jardín Infantil',
    'Independencia', '', 'Conectada', Curdate() - Interval 10 Day

);
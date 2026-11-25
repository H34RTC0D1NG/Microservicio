> # Microservicios - PagoFlex

```json
{
    "Microservicios" : [
        {
            "Usuarios" : [
                {
                    "Carpeta" : "ms-usuarios",
                    "Puerto" : 8081,
                    "Base_Datos" : "MySQL - pagoflex_usuarios",
                    "Ruta_Base" : "/api",
                    "Descripcion" : "Maneja el registro, inicio de sesion, perfil, roles y activar o desactivar personas.",
                    "Tabla" : {
                        "Nombre" : "usuarios",
                        "Columnas" : ["id", "rut", "nombre", "correo", "comuna", "telefono", "pin_hash", "rol", "activo"]
                    },
                    "Endpoints" : [
                        { "Metodo" : "POST", "Ruta" : "/auth/registro", "Hace" : "Crea una cuenta nueva con rol CLIENTE" },
                        { "Metodo" : "POST", "Ruta" : "/auth/login", "Hace" : "Valida RUT y PIN y devuelve los datos del usuario" },
                        { "Metodo" : "GET", "Ruta" : "/usuarios", "Hace" : "Lista todas las personas" },
                        { "Metodo" : "GET", "Ruta" : "/usuarios/{id}", "Hace" : "Devuelve una persona" },
                        { "Metodo" : "POST", "Ruta" : "/usuarios", "Hace" : "Crea una persona eligiendo el rol (Admin)" },
                        { "Metodo" : "PUT", "Ruta" : "/usuarios/{id}", "Hace" : "Edita nombre, correo, comuna, telefono y PIN" },
                        { "Metodo" : "PATCH", "Ruta" : "/usuarios/{id}/rol?valor=", "Hace" : "Cambia el rol a ADMIN o CLIENTE" },
                        { "Metodo" : "PATCH", "Ruta" : "/usuarios/{id}/activo?valor=", "Hace" : "Activa o desactiva la cuenta" },
                        { "Metodo" : "DELETE", "Ruta" : "/usuarios/{id}", "Hace" : "Elimina la persona" }
                    ],
                    "Clases" : {
                        "Aplicacion" : "UsuariosApplication",
                        "Controlador" : "UsuarioController",
                        "Servicio" : "UsuarioService",
                        "Modelo" : "Usuario",
                        "Repositorio" : "UsuarioRepository"
                    },
                    "Detalles" : [
                        "PIN de 4 numeros encriptado con BCrypt (spring-security-crypto)",
                        "Valida el RUT con digito verificador y lo guarda sin puntos (12345678-5)",
                        "RUT y correo no se pueden repetir",
                        "La respuesta nunca incluye el pin_hash (@JsonIgnore)",
                        "Migraciones Flyway : V1 crea usuarios, V2 carga 3 cuentas de prueba con PIN 1234"
                    ]
                }
            ]
        },
        {
            "Compromisos" : [
                {
                    "Carpeta" : "ms-compromisos",
                    "Puerto" : 8082,
                    "Base_Datos" : "MySQL - Compromisos",
                    "Ruta_Base" : "/Api/Compromiso",
                    "Descripcion" : "Maneja los cobros de cada cliente, los pagos y los comprobantes.",
                    "Tabla" : [
                        {
                            "Nombre" : "compromisos",
                            "Columnas" : ["id", "folio", "usuario_id", "empresa", "tipo", "concepto", "cuota", "total_cuotas", "monto", "vencimiento", "estado"]
                        },
                        {
                            "Nombre" : "comprobantes",
                            "Columnas" : ["id", "numero", "compromiso_id", "folio", "usuario_id", "empresa", "concepto", "monto", "fecha", "hora", "medio", "canal", "puntual"]
                        }
                    ],
                    "Endpoints" : [
                        { "Metodo" : "GET", "Ruta" : "/Listar?Usuario_ID=", "Hace" : "Lista los cobros (sin parametro lista todos)" },
                        { "Metodo" : "GET", "Ruta" : "/Obtener/{ID}", "Hace" : "Devuelve un cobro" },
                        { "Metodo" : "POST", "Ruta" : "/Crear", "Hace" : "Crea un cobro y le genera el folio PF-1000 + ID" },
                        { "Metodo" : "PUT", "Ruta" : "/Actualizar/{ID}", "Hace" : "Edita un cobro que no este pagado" },
                        { "Metodo" : "PATCH", "Ruta" : "/Modificar/{ID}/Estado?Valor=", "Hace" : "Cambia el estado del cobro" },
                        { "Metodo" : "DELETE", "Ruta" : "/Eliminar/{ID}", "Hace" : "Elimina un cobro que no tenga comprobante" },
                        { "Metodo" : "POST", "Ruta" : "/Pagar/{ID}", "Hace" : "Paga el cobro y genera el comprobante C-5000 + ID" },
                        { "Metodo" : "GET", "Ruta" : "/Comprobantes?Usuario_ID=", "Hace" : "Lista el historial de pagos" }
                    ],
                    "Clases" : {
                        "Controlador" : "CompromisoControlador",
                        "Servicio" : "CompromisoServicio",
                        "Modelo" : ["Compromiso", "Comprobante"],
                        "Repositorio" : ["CompromisoRepositorio", "ComprobanteRepositorio"]
                    },
                    "Detalles" : [
                        "Tipos : Arriendo, Credito, Club, Fundacion",
                        "Estados : Pendiente, Pagado, En_Revision, Anulado (acepta mayuscula o minuscula con equalsIgnoreCase)",
                        "Recargo por atraso : Arriendo $7.500, Credito $12.000, Club $2.000, Fundacion $0",
                        "No deja pagar una cuota si hay una anterior de la misma empresa sin pagar",
                        "No deja pagar si el cobro esta Pagado, En_Revision o Anulado",
                        "Un pago unico lleva cuota 0 y total 0",
                        "Pagar usa @Transactional : cambia el estado y crea el comprobante juntos",
                        "Migraciones Flyway : V1 crea compromisos y comprobantes, V2 carga 7 cobros y 2 comprobantes"
                    ]
                }
            ]
        },
        {
            "Reportes" : [
                {
                    "Carpeta" : "ms-reportes",
                    "Puerto" : 8083,
                    "Base_Datos" : "MySQL - pagoflex_reportes",
                    "Ruta_Base" : "/api/reportes",
                    "Descripcion" : "Guarda los problemas que reportan los clientes con un cobro y permite que el Admin los resuelva.",
                    "Tabla" : {
                        "Nombre" : "reportes",
                        "Columnas" : ["id", "numero", "compromiso_id", "folio", "usuario_id", "motivo", "comentario", "estado", "respuesta", "fecha"]
                    },
                    "Endpoints" : [
                        { "Metodo" : "GET", "Ruta" : "/?usuarioId=", "Hace" : "Lista los reportes (sin parametro lista todos)" },
                        { "Metodo" : "GET", "Ruta" : "/{id}", "Hace" : "Devuelve un reporte" },
                        { "Metodo" : "POST", "Ruta" : "/", "Hace" : "Crea un reporte con estado ABIERTO" },
                        { "Metodo" : "PATCH", "Ruta" : "/{id}/resolver?accion=", "Hace" : "Cierra el reporte : RECHAZAR, ANULAR_COBRO o MARCAR_PAGADO" }
                    ],
                    "Clases" : {
                        "Aplicacion" : "ReportesApplication",
                        "Controlador" : "ReporteController",
                        "Servicio" : "ReporteService",
                        "Modelo" : "Reporte",
                        "Repositorio" : "ReporteRepository"
                    },
                    "Detalles" : [
                        "Motivos : YA_PAGUE, NO_RECONOZCO, MONTO_INCORRECTO, OTRO",
                        "Si el motivo es OTRO el comentario es obligatorio",
                        "Solo puede haber un reporte ABIERTO por cobro",
                        "Al resolver deja una respuesta automatica segun la accion",
                        "No cambia el estado del cobro, eso lo hace la app llamando a Compromisos",
                        "Numero del reporte : R-300 + ID",
                        "Migracion Flyway : V1 crea reportes"
                    ]
                }
            ]
        },
        {
            "Referidos" : [
                {
                    "Carpeta" : "ms-referidos",
                    "Puerto" : 8084,
                    "Base_Datos" : "MySQL - pagoflex_referidos",
                    "Ruta_Base" : "/api/recomendaciones",
                    "Descripcion" : "Guarda las organizaciones que los clientes recomiendan para unirse a PagoFlex.",
                    "Tabla" : {
                        "Nombre" : "recomendaciones",
                        "Columnas" : ["id", "numero", "usuario_id", "organizacion", "rubro", "comuna", "contacto", "estado", "fecha"]
                    },
                    "Endpoints" : [
                        { "Metodo" : "GET", "Ruta" : "/?usuarioId=", "Hace" : "Lista las recomendaciones (sin parametro lista todas)" },
                        { "Metodo" : "POST", "Ruta" : "/", "Hace" : "Crea una recomendacion con estado RECIBIDA" },
                        { "Metodo" : "PATCH", "Ruta" : "/{id}/estado?valor=", "Hace" : "Cambia el estado de la recomendacion" },
                        { "Metodo" : "DELETE", "Ruta" : "/{id}", "Hace" : "Retira una recomendacion que siga RECIBIDA" }
                    ],
                    "Clases" : {
                        "Aplicacion" : "ReferidosApplication",
                        "Controlador" : "RecomendacionController",
                        "Servicio" : "RecomendacionService",
                        "Modelo" : "Recomendacion",
                        "Repositorio" : "RecomendacionRepository"
                    },
                    "Detalles" : [
                        "Estados : RECIBIDA, CONTACTADA, EN_EVALUACION, SE_UNIO, DESCARTADA",
                        "Maximo 5 recomendaciones por cliente",
                        "Si se deja un contacto la persona debe autorizar que la contacten",
                        "Numero de la recomendacion : REC-100 + ID",
                        "Migraciones Flyway : V1 crea recomendaciones, V2 carga 1 recomendacion de ejemplo"
                    ]
                }
            ]
        }
    ],

    "Comun" : {
        "Tecnologias" : ["Java 17", "Spring Boot 3.5.6", "Spring Web", "Spring Data JPA", "Validation", "Flyway", "MySQL (Laragon)", "Lombok", "SpringDoc OpenAPI"],
        "Conexion" : "Los microservicios no se llaman entre ellos, la app Android llama directo a los 4 (http://10.0.2.2:8081 a 8084)",
        "Errores" : "ResponseStatusException con server.error.include-message=always para que la app reciba el mensaje",
        "Swagger" : "http://localhost:PUERTO/swagger-ui.html",
        "Estructura" : "Controlador -> Servicio -> Repositorio -> Modelo"
    }
}
```

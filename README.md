# PagoFlex

App móvil para pagar en un solo lugar los compromisos del mes (arriendo, créditos, cuotas de club y aportes a fundaciones), con recordatorios, comprobantes, reportes de problemas, recomendaciones y un panel de administración.

**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles · Evaluación Parcial 4

## 1. Nombre del proyecto

**PagoFlex** (paquete `com.movil.pagoflex`)

## 2. Integrantes

| Nombre | Usuario GitHub | Rol |
|---|---|---|
| Isam Mejias | H34RTHC0D1NG | Diseñador Backend |
| Gonzalo Gallardo | CALEB | Diseñador Frontend |

Tablero Trello: _pegar enlace_

## 3. Funcionalidades

**Cliente**
- Crear cuenta e ingresar con RUT y PIN de 4 números (bloqueo de 30 segundos tras 3 intentos fallidos).
- Ver los compromisos del mes con recargo por atraso y filtrarlos por estado.
- Pagar un compromiso (pago simulado) respetando el orden de las cuotas, y ver y compartir el comprobante.
- Historial de pagos filtrable por empresa, indicador de cumplimiento y logros.
- Reportar un problema con un cobro (queda En revisión hasta que el administrador lo resuelva).
- Recomendar organizaciones (máximo 5) y retirar una recomendación.
- Editar perfil, cambiar PIN y eliminar la cuenta.
- Indicadores económicos del día (UF, dólar, euro, UTM, IPC) y total del mes convertido a UF.
- Recordatorios configurables, ocultar montos, texto grande y alto contraste.

**Administrador**
- Panel con totales: personas, cobros por pagar y reportes abiertos.
- Personas: agregar cliente o administrador, cambiar rol, activar o desactivar y eliminar.
- Cobros: crear, editar y eliminar compromisos de cada cliente.
- Reportes: rechazar, anular el cobro o marcarlo como pagado.

**Pantallas (20):** Ingresar, Crear cuenta, Mi mes, Mis compromisos, Detalle, Pagar, Comprobante, Reportar problema, Historial y logros, Indicadores económicos, Recomendar, Mi perfil, Editar perfil, Recordatorios y accesibilidad, Panel de administración, Personas, Agregar persona, Cobros, Cobro (crear/editar) y Reportes de clientes.

**Cuentas de prueba** (PIN `1234`)

| Rol | RUT |
|---|---|
| Administrador | 11.111.111-1 |
| Cliente (con cobros de ejemplo) | 15.834.207-3 |
| Cliente | 12.345.678-5 |

### Arquitectura

```
App Android (Kotlin + Jetpack Compose, MVVM)
ui/screens  ->  viewmodel (StateFlow)  ->  data (Repositorio + SQLite)  ->  network (RetrofitService)
                                                                              |
        +--------------------+----------------------+----------------------+---------------------+
        |                    |                      |                      |                     |
   Usuarios :8081      Compromisos :8082      Reportes :8083       Referidos :8084        mindicador.cl
   base Usuarios       base Compromisos       base Reportes        base Referidos         (API externa)
```

- La app llama directo a cada microservicio. Los microservicios no se llaman entre ellos.
- Lo que responde el servidor se copia a SQLite. Si un microservicio está apagado la app sigue funcionando con los datos del teléfono.
- La base local se crea desde el archivo `app/src/main/assets/BaseDatos.sql` (tablas y datos de prueba). `BaseDatos.kt` lo lee y ejecuta cada sentencia al instalar la app.

```
PagoFlex/                         Proyecto Android Studio
  app/src/main/assets/BaseDatos.sql   Script SQL de la base local (5 tablas + datos)
  app/src/main/java/com/movil/pagoflex/
    model/        Modelos.kt (modelos, enums y estados de pantalla)
    data/         BaseDatos.kt (SQLite), Repositorio.kt, Reglas.kt, Validador.kt
    network/      RetrofitService.kt (interfaces Retrofit), Dtos.kt
    viewmodel/    PagoFlexViewModel.kt, AdminViewModel.kt
    nativo/       Nativo.kt (vibración y compartir)
    ui/           components, screens/pagoflex, screens/admin, theme
  app/src/test/   Pruebas unitarias (JUnit + MockK)
  pagoflex-release.jks / keystore.properties
Microservicio-main/                Spring Boot 4.0.10 · Java 21 · Flyway · MySQL (Laragon)
  Usuarios / Compromisos / Reportes / Referidos
  Cada uno: Controlador -> Servicio -> Repositorio -> Modelo
  Scripts Flyway en src/main/resources/DB/Migracion
```

## 4. Endpoints utilizados

### API externa

| Método | Endpoint | Uso en la app |
|---|---|---|
| GET | `https://mindicador.cl/api` | Pantalla Indicadores económicos y tarjeta "Tu mes en UF" |

### Usuarios (`http://localhost:8081/Api/Usuario`)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/Registro` | Crea cuenta con rol Cliente (PIN cifrado con BCrypt) |
| POST | `/Login` | Valida RUT y PIN. Body: `{"Rut":"15834207-3","Pin":"1234"}` |
| GET | `/Listar` | Lista personas |
| GET | `/Obtener/{ID}` | Una persona |
| POST | `/Crear` | Crea persona eligiendo rol (Admin o Cliente) |
| PUT | `/Actualizar/{ID}` | Edita datos y PIN |
| PATCH | `/Modificar/{ID}/Rol?Valor=` | Cambia el rol |
| PATCH | `/Modificar/{ID}/Activo?Valor=` | Activa o desactiva |
| DELETE | `/Eliminar/{ID}` | Elimina |

### Compromisos (`http://localhost:8082/Api/Compromiso`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/Listar?Usuario_ID=` | Lista cobros (sin parámetro: todos) |
| GET | `/Obtener/{ID}` | Un cobro |
| POST | `/Crear` | Crea un cobro (folio PF-1000 + ID) |
| PUT | `/Actualizar/{ID}` | Edita un cobro no pagado |
| PATCH | `/Modificar/{ID}/Estado?Valor=` | Pendiente, Pagado, En_Revision o Anulado |
| DELETE | `/Eliminar/{ID}` | Elimina un cobro sin comprobante |
| POST | `/Pagar/{ID}` | Paga y genera comprobante. Body: `{"Usuario_ID":2,"Medio":"Transferencia"}` |
| GET | `/Comprobantes?Usuario_ID=` | Historial de pagos |

### Reportes (`http://localhost:8083/Api/Reporte`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/Listar?Usuario_ID=` | Lista reportes |
| GET | `/Obtener/{ID}` | Un reporte |
| POST | `/Crear` | Crea un reporte Abierto. Body: `{"Compromiso_ID":2,"Usuario_ID":2,"Folio":"PF-1002","Motivo":"Ya_Pague","Comentario":""}` |
| PATCH | `/Resolver/{ID}?Accion=` | Rechazar, Anular_Cobro o Marcar_Pagado |

### Referidos (`http://localhost:8084/Api/Recomendaciones`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/Listar?Usuario_ID=` | Lista recomendaciones |
| GET | `/Obtener/{ID}` | Una recomendación |
| POST | `/Crear` | Crea una recomendación (máximo 5 por cliente) |
| PATCH | `/Modificar/{ID}/Estado?Valor=` | Cambia estado |
| DELETE | `/Eliminar/{ID}` | Retira una recomendación Recibida |

Swagger de cada microservicio: `http://localhost:PUERTO/swagger-ui.html`

## 5. Pasos para ejecutar

### Requisitos
- Android Studio con el SDK 37 y un emulador.
- JDK 21 y Maven (o IntelliJ IDEA).
- Laragon con MySQL en `localhost:3306`, usuario `root` sin clave.

### 5.1 Base de datos
Encender MySQL en Laragon. Las bases `Usuarios`, `Compromisos`, `Reportes` y `Referidos` se crean solas y Flyway ejecuta los scripts de `src/main/resources/DB/Migracion` de cada microservicio (tablas y datos de prueba).

La app tiene su propia base SQLite que se arma con `app/src/main/assets/BaseDatos.sql`.

### 5.2 Microservicios
Cada uno en su terminal (el orden no importa):

```bash
cd Microservicio-main/Usuarios && mvn spring-boot:run
cd Microservicio-main/Compromisos && mvn spring-boot:run
cd Microservicio-main/Reportes && mvn spring-boot:run
cd Microservicio-main/Referidos && mvn spring-boot:run
```

### 5.3 App
1. Abrir la carpeta `PagoFlex` en Android Studio y esperar el Gradle Sync.
2. Iniciar el emulador y presionar **Run**. El emulador llega al PC con `10.0.2.2`.
3. En un celular físico, cambiar `SERVIDOR` en `network/RetrofitService.kt` por la IP del PC (misma red WiFi).

### 5.4 Pruebas unitarias
78 pruebas con **JUnit 4 + MockK + kotlinx-coroutines-test** en `app/src/test`:

| Archivo | Qué prueba | Qué se mockea |
|---|---|---|
| `viewmodel/PagoFlexViewModelTest` | Ingreso, bloqueo por intentos, registro, navegación, pago, reporte, ajustes | `Repositorio`, `Vibrador`, `RetrofitService` |
| `viewmodel/AdminViewModelTest` | Cargar datos, crear persona, cambiar rol, eliminar cobro, resolver reporte | `Repositorio` |
| `data/RepositorioTest` | Login y registro con y sin servidor, pagar, reportar, máximo de recomendaciones, resolver reporte | `BaseDatos`, `SharedPreferences`, las 4 APIs |
| `data/ReglasTest` | Orden de cuotas, bloqueos de pago, avisos, logros, cumplimiento, total del mes | Nada (lógica pura) |
| `data/ValidadorTest` | RUT, PIN, correo, teléfono y formularios | Nada |
| `model/ModelosTest` | Fechas, recargos, cuotas, formatos de RUT y pesos, estados de pantalla | Nada |
| `network/MapeosTest` | JSON de los 4 microservicios a modelos, nombres que se envían (`Usuario_ID`, `Rut`, `Folio`...), fechas | Nada |
| `data/BaseDatosSqlTest` | Que el archivo `BaseDatos.sql` exista, cree las 5 tablas y traiga los datos de prueba | Nada |

Ejecutar: clic derecho en `app/src/test` > **Run 'Tests in pagoflex'**, o `./gradlew testDebugUnitTest`.

### 5.5 Generar el APK firmado
La firma ya está configurada en `app/build.gradle.kts` (`signingConfigs`), y lee la llave desde `keystore.properties`.

- Desde Android Studio: **Build > Generate Signed App Bundle or APK > APK**, elegir `pagoflex-release.jks`, alias `pagoflex`, variante `release`.
- Desde la terminal: `./gradlew assembleRelease`.
- El APK queda en `app/build/outputs/apk/release/app-release.apk`.

## 6. APK firmado y .jks

| Dato | Valor |
|---|---|
| Archivo de llave | `PagoFlex/pagoflex-release.jks` |
| Alias | `pagoflex` |
| Algoritmo | RSA 2048, validez 10.000 días |
| Configuración | `PagoFlex/keystore.properties` y `signingConfigs` en `app/build.gradle.kts` |
| APK | `app/build/outputs/apk/release/app-release.apk` |

_Pegar aquí la captura del APK firmado generado._

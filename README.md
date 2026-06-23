\# Vapor \- API REST con Spring Boot \+ JPA \+ MySQL \+ Steam API

Proyecto de integración y gestión de perfiles de Steam utilizando arquitectura por capas con Spring Boot:

\- \`controller\` (capa web / endpoints REST)  
\- \`service\` (lógica de negocio e integración con APIs externas)  
\- \`repository\` (acceso a datos con \*\*JPA / Hibernate\*\*)  
\- \`model\` (entidades JPA / estructura de datos)  
\- \`security\` (autenticación y autorización con JWT)

\---

\#\# Índice

\- \[1) Requisitos\](\#1-requisitos)  
\- \[2) Configuración de base de datos y entorno\](\#2-configuración-de-base-de-datos-y-entorno)  
\- \[3) ¿Cómo ejecutar el proyecto?\](\#3-cómo-ejecutar-el-proyecto)  
\- \[4) URL base de la API\](\#4-url-base-de-la-api)  
\- \[5) Endpoints disponibles\](\#5-endpoints-disponibles)  
\- \[6) Estructura del proyecto y explicación por capas\](\#6-estructura-del-proyecto-y-explicación-por-capas)  
\- \[7) Colección de Postman\](\#7-colección-de-postman)  
\- \[8) Dependencias principales (pom.xml)\](\#8-dependencias-principales-pomxml)  
\- \[9) JpaRepository: métodos usados en este proyecto\](\#9-jparepository-métodos-usados-en-este-proyecto)  
\- \[10) Manejador global de errores (@RestControllerAdvice)\](\#10-manejador-global-de-errores-restcontrolleradvice)  
\- \[11) ResponseEntity: manejo de respuestas HTTP\](\#11-responseentity-manejo-de-respuestas-http)  
\- \[12) WebClient y consumo de APIs externas (Steam)\](\#12-webclient-y-consumo-de-apis-externas-steam)  
\- \[13) Spring Security y autenticación JWT\](\#13-spring-security-y-autenticación-jwt)  
\- \[14) Swagger / OpenAPI\](\#14-swagger--openapi)  
\- \[15) Pruebas unitarias: JUnit \+ Mockito\](\#15-pruebas-unitarias-junit--mockito)  
\- \[16) Docker: guía para principiantes\](\#16-docker-guía-para-principiantes)  
\- \[17) Despliegue en Railway (GitHub \+ MySQL)\](\#17-despliegue-en-railway-github--mysql)  
\- \[18) Autor\](\#18-autor)

\---

\#\# 1\) Requisitos

\- Java 17  
\- Maven (opcional si usas \`mvnw\`)  
\- \*\*Docker y Docker Compose\*\* (recomendado para levantar MySQL y la app fácilmente)  
\- IDE recomendado: VS Code / IntelliJ / Eclipse  
\- Postman (para probar la API)  
\- \*\*Steam API Key\*\* (Obligatorio para sincronizar perfiles y juegos de la API de Steam)

\> Hibernate crea automáticamente las tablas \`steam\_users\`, \`steam\_game\` y \`app\_user\` al iniciar la aplicación (\`ddl-auto=update\`).

\---

\#\# 2\) Configuración de base de datos y entorno

El proyecto está diseñado para leer credenciales de forma segura mediante variables de entorno. Debes crear un archivo \`.env\` en la raíz del proyecto (al mismo nivel que \`docker-compose.yml\`) con la siguiente estructura:

\`\`\`env  
\# Contenedor de MySQL  
MYSQL\_ROOT\_PASSWORD=root  
MYSQL\_DATABASE=vapor  
MYSQL\_HOST\_PORT=3307  
MYSQL\_CONTAINER\_PORT=3306

\# Contenedor de la Aplicación  
APP\_HOST\_PORT=8080

\# Configuración de Spring Boot (Dentro de Docker)  
DB\_URL\_DOCKER=jdbc:mysql://mysql:3306/vapor?createDatabaseIfNotExist=true\&useSSL=false\&allowPublicKeyRetrieval=true\&serverTimezone=UTC  
DB\_USERNAME=root  
DB\_PASSWORD=root

\# Seguridad JWT  
JWT\_SECRET=vapor-app-jwt-secret-key-security-2026

\# APIs Externas (Steam Integration)  
STEAM\_API\_URL=\[https://api.steampowered.com\](https://api.steampowered.com)  
STEAM\_API\_KEY=tu\_steam\_api\_key\_aqui

Nota: Si falta alguna variable de entorno, el contenedor de Docker fallará al iniciar o Spring Boot no podrá conectarse a la API de Steam (comportamiento intencional por seguridad).

## **3\) ¿Cómo ejecutar el proyecto?**

### **Opción A: Usando Docker Compose (Recomendado)**

Abre una terminal en la raíz del proyecto y ejecuta:

Bash  
docker compose up \--build \-d

Esto construirá la imagen de Java 17, levantará la base de datos MySQL 8.4 y la conectará automáticamente con tu aplicación.

### **Opción B: Desarrollo local (usando Maven)**

Si prefieres correr la aplicación desde tu IDE y usar Docker solo para la base de datos:

1. Levanta solo MySQL: docker compose up mysql \-d  
2. Ejecuta el wrapper de Maven en tu terminal:  
   * Windows: .\\mvnw.cmd spring-boot:run  
   * Mac/Linux: ./mvnw spring-boot:run

## **4\) URL base de la API**

Por defecto Spring Boot levanta en puerto 8080:

Plaintext  
http://localhost:8080

## **5\) Endpoints disponibles**

### **5.1 Autenticación (Auth Controller)**

| Método | Endpoint | Descripción | Requiere Token |
| :---- | :---- | :---- | :---- |
| POST | /api/v1/auth/register | Registra un nuevo usuario en la app (AppUser) | ❌ NO |
| POST | /api/v1/auth/login | Inicia sesión y devuelve un token JWT | ❌ NO |

### **5.2 Integración con Steam (SteamUser Controller)**

| Método | Endpoint | Descripción | Requiere Token |
| :---- | :---- | :---- | :---- |
| GET | /api/v1/steam | Lista todos los usuarios de Steam guardados en la BD local | ✅ SÍ |
| GET | /api/v1/steam/{steamId} | Consulta el perfil en vivo desde la API de Steam (No guarda) | ✅ SÍ |
| POST | /api/v1/steam/register/{steamId} | Registra un usuario de Steam en tu BD local | ✅ SÍ |
| POST | /api/v1/steam/library/{steamId} | Sincroniza y guarda los juegos de la biblioteca del usuario | ✅ SÍ |
| PUT | /api/v1/steam/update/{steamId} | Actualiza nombre y estado de conexión del usuario local | ✅ SÍ |
| DELETE | /api/v1/steam/{id} | Elimina el usuario de Steam de la BD local | ✅ SÍ |

## **6\) Estructura del proyecto y explicación por capas**

Plaintext  
src/main/java/com/plataforma/Vapor/  
├── controller/     (Recibe HTTP Requests y responde JSON)  
├── dto/            (Objetos de transferencia: AuthRequest, SteamUserDTO, SteamGameDTO)  
├── exception/      (Manejo global de errores y formato de respuestas)  
├── model/          (Entidades: AppUser, SteamUser, SteamGame)  
├── repository/     (Interfaces Spring Data JPA)  
├── security/       (Filtros JWT, UserDetailsService, Config)  
└── service/        (Lógica de negocio y uso de WebClient)

### **Explicación breve:**

* **Controller:** Recibe peticiones, delega lógica al servicio y retorna ResponseEntity.  
* **Service:** Contiene las reglas de negocio y consume la API externa de Steam mediante WebClient.  
* **Repository:** Interfaces de Spring Data para comunicarse con MySQL sin escribir SQL.  
* **Model:** Clases mapeadas a tablas (ej. @Entity SteamUser).

## **7\) Colección de Postman**

El proyecto cuenta con una colección optimizada de Postman que utiliza **Herencia de Autenticación** a nivel de carpeta.

1. Importa la colección JSON (Vapor\_API\_Collection.postman\_collection.json) en Postman.  
2. Ejecuta el endpoint Login User dentro de la carpeta **Auth**.  
3. Copia el token JWT generado en la respuesta.  
4. Haz clic derecho en la carpeta **Steam Users** \> Edit \> Pestaña Authorization.  
5. Selecciona el tipo Bearer Token y pega el token.  
   ¡Listo\! Todos los endpoints de Steam heredarán el acceso automáticamente.

## **8\) Dependencias principales (pom.xml)**

* spring-boot-starter-web (Construcción de API REST)  
* spring-boot-starter-data-jpa (Persistencia de datos e Hibernate)  
* spring-boot-starter-webflux (Provee WebClient para consumir la API de Steam)  
* spring-boot-starter-security (Filtros y protección de rutas)  
* jjwt-api, jjwt-impl, jjwt-jackson (Manejo de tokens JWT, versión 0.12.x)  
* mysql-connector-j (Driver de base de datos)  
* lombok (Reducción de código repetitivo)

## **9\) JpaRepository: métodos usados en este proyecto**

El proyecto abstrae el SQL usando interfaces como:

Java  
@Repository  
public interface SteamUserRepository extends JpaRepository\<SteamUser, Long\> { }

@Repository  
public interface SteamGameRepository extends JpaRepository\<SteamGame, Long\> { }

### **Métodos principales usados:**

* findAll(): Para listar todos los usuarios en la BD local.  
* findById(id): Para buscar un usuario o juego específico. Al usar el ID real de Steam (Long), evitamos duplicados.  
* save(entity): Guarda o actualiza (ej. al sincronizar la biblioteca).  
* deleteById(id): Borra el perfil localmente.  
* existsById(id): Verifica la existencia antes de borrar o actualizar.

## **10\) Manejador global de errores (@RestControllerAdvice)**

Para evitar llenar los controladores con bloques try-catch para excepciones no controladas, se implementó una clase global de asesoramiento:

Java  
@RestControllerAdvice  
public class Exceptions {  
   @ExceptionHandler(Exception.class)  
    public ResponseEntity\<ApiError\> handleGenericError(Exception e) {  
        ApiError error \= new ApiError(500, "Error en la integridad de los datos ingresados", e.getMessage());  
        return ResponseEntity.status(HttpStatus.INTERNAL\_SERVER\_ERROR).body(error);  
    }  
}

Esto garantiza que la aplicación siempre responda con un JSON estructurado y un código HTTP correcto en vez de romper la ejecución del cliente con la traza de Java.

## **11\) ResponseEntity: manejo de respuestas HTTP**

Se utiliza ResponseEntity para tener control absoluto sobre los códigos de estado HTTP enviados al cliente:

| Método | Código HTTP | Cuándo se usa en Vapor |
| :---- | :---- | :---- |
| ResponseEntity.ok(body) | **200 OK** | Al sincronizar una biblioteca o devolver perfiles. |
| ResponseEntity.status(HttpStatus.CREATED).body(body) | **201 Created** | Al registrar un nuevo usuario de Steam en la BD. |
| ResponseEntity.badRequest().body(msg) | **400 Bad Request** | Cuando fallan las validaciones de negocio en el registro. |
| ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() | **401 Unauthorized** | Cuando las credenciales del login son inválidas. |
| ResponseEntity.status(HttpStatus.NOT\_FOUND).body(msg) | **404 Not Found** | Al intentar actualizar o borrar un usuario que no existe. |

## **12\) WebClient y consumo de APIs externas (Steam)**

Para consultar datos asíncronos y modernos, usamos **WebClient** (parte de Spring WebFlux) en lugar de RestTemplate.  
El SteamUserService configura dinámicamente las llamadas integrando nuestra API KEY:

Java  
SteamUserDTO apiUser \= steamWebClient.get()  
        .uri(uriBuilder \-\> uriBuilder  
                .path("/ISteamUser/GetPlayerSummaries/v0002/")  
                .queryParam("key", steamConfig.getApiKey())  
                .queryParam("steamids", steamId)  
                .build())  
        .retrieve()  
        .bodyToMono(SteamUserDTO.class)  
        .block(); 

Este fragmento consulta en tiempo real el servidor de Steam, descarga el JSON gigante y lo mapea automáticamente a nuestro objeto SteamUserDTO. El mismo patrón se utiliza para leer la biblioteca de juegos en /IPlayerService/GetOwnedGames/v0001/.

## **13\) Spring Security y autenticación JWT**

La API de Vapor está protegida bajo una arquitectura **Stateless** (sin estado) usando Tokens JWT.

### **Flujo:**

1. **Login:** Haces POST a /api/v1/auth/login. Spring valida la password contra BCrypt en BD.  
2. **Generación:** JwtUtil crea un token cifrado con tus roles (ROLE\_USER).  
3. **Filtro:** JwtFilter intercepta cada petición a /api/v1/steam/. Revisa la cabecera Authorization: Bearer \<token\>, comprueba que la firma sea legítima, y si todo está bien, permite que el controlador procese la solicitud.

## **14\) Swagger / OpenAPI**

Swagger UI permite visualizar y probar la API desde el navegador (si está configurado en tu pom.xml).

* URL de acceso (con el contenedor levantado): http://localhost:8080/swagger-ui/index.html  
* Para usarlo en rutas protegidas por JWT, debes incluir el token en el botón "Authorize" de la interfaz de Swagger.

## **15\) Pruebas unitarias: JUnit \+ Mockito**

Se integró un ecosistema de Testing sin requerir conexión real a Steam ni a MySQL. Se utilizan Mocks para simular el comportamiento de los servicios y repositorios.  
Ejemplo en SteamUserControllerTest:

Java  
@Test  
void registerUser\_retorna201\_cuandoRegistroEsExitoso() {  
    Long steamId \= 76561198000000000L;  
    // Usamos Collections.emptyList() en lugar de instanciar implementaciones fijas  
    SteamUser mockUser \= new SteamUser(steamId, "VaporPlayer", "Real Name", 1, Collections.emptyList());

    when(steamUserService.registerSteamUser(steamId)).thenReturn(mockUser);

    var respuesta \= steamUserController.registerUser(steamId);

    assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());  
    assertEquals("VaporPlayer", ((SteamUser) respuesta.getBody()).getUsername());  
}

Esto valida la lógica y respuestas del controlador (Arrange, Act, Assert) garantizando el código HTTP correcto.

## **16\) Docker: guía para principiantes**

### **¿Qué se agregó en este proyecto?**

* Dockerfile: Un *multi-stage build* que compila el proyecto con Maven y luego genera una imagen liviana solo con Java 17 JRE.  
* docker-compose.yml: Define y orquesta dos servicios (mysql y app).  
* .env: Gestiona de forma externa y segura las contraseñas, URLs y API Keys.

### **Flujo completo**

1. docker compose up \--build \-d → Levanta y compila todo en segundo plano.  
2. docker compose logs app \-f → Permite ver los logs en tiempo real de Spring Boot.  
3. docker compose down → Apaga los contenedores y elimina la red interna.

El contenedor app cuenta con un depends\_on: mysql con condición service\_healthy. Esto asegura que Spring Boot no intente arrancar hasta que MySQL esté 100% listo para recibir conexiones.

## **17\) Despliegue en Railway (GitHub \+ MySQL)**

Guía rápida para publicar esta API en la nube con [Railway](https://railway.app):

1. En Railway, crea un proyecto nuevo.  
2. Elige la opción de desplegar desde GitHub.  
3. Conecta tu cuenta y selecciona este repositorio.  
4. Railway detectará el Dockerfile / Proyecto Maven y hará el primer deploy.  
5. Agrega un servicio de base de datos MySQL dentro del mismo proyecto.  
6. Copia las credenciales de la BD y agrégalas en las "Variables" del servicio de tu App (SPRING\_DATASOURCE\_URL, SPRING\_DATASOURCE\_USERNAME, SPRING\_DATASOURCE\_PASSWORD).  
7. Añade tus variables secretas (JWT\_SECRET, STEAM\_API\_URL, STEAM\_API\_KEY).  
8. Haz redeploy. Genera un dominio público desde "Settings \> Domains" y tu API estará online.

## **18\) Autor**

* **\[Tu Nombre / Tu Organización\]**  
* **Correo:** \[tu\_correo@ejemplo.com\]  
* **Proyecto:** Vapor (Steam API Integration)
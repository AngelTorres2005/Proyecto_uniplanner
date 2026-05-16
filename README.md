# UniPlanner - Plataforma de Productividad Universitaria

UniPlanner es una aplicación web integral diseñada para ayudar a estudiantes universitarios a gestionar su vida académica. La plataforma permite organizar materias, construir horarios semanales dinámicos, administrar tareas por prioridad y recibir notificaciones automatizadas por correo electrónico sobre entregas próximas a vencer.

---

## Características Principales

* **Autenticación y Seguridad Robusta:** Sistema de inicio de sesión y registro protegido mediante **Spring Security**, con contraseñas encriptadas y persistencia de sesión segura.
* **Horario Semanal Dinámico:** Algoritmo en el frontend (JavaScript/Fetch) que renderiza de manera cronológica bloques horarios, cruzando dinámicamente los días de la semana y validando choques de horario desde el backend.
* **Gestión de Tareas Inteligente:** Panel completo para crear, actualizar, listar y marcar como entregadas las tareas pendientes, vinculadas a materias específicas y niveles de prioridad.
* **Motor de Notificaciones Automatizadas (Gmail SMTP):** Tarea programada en segundo plano (`@Scheduled`) respaldada por transacciones de base de datos (`@Transactional`) que analiza las entregas próximas a vencer y despacha correos recordatorios personalizados usando la API de Gmail.
* **Panel de Configuración de Perfil:** Gestión de datos personales del estudiante asistido por `GlobalControllerAdvice` para inyecciones dinámicas en el lado del servidor mediante Thymeleaf.
* **Interfaz Adaptativa y Modo Oscuro:** Diseño limpio, responsivo y moderno basado en **Bootstrap 5**, con soporte nativo de modo oscuro guardado en el almacenamiento local (`LocalStorage`).

---

## Tecnologías Utilizadas ##

### Backend
* **Java 17**
* **Spring Boot **
* **Spring Security** (Autenticación y Autorización)
* **Spring Data JPA** (Persistencia y consultas JPQL relacionales)
* **Spring Boot Starter Mail** (Despacho de correos electrónicos vía SMTP)

### Frontend
* **Thymeleaf** 
* **Bootstrap 5** 
* **JavaScript (ES6+)** 
* **SweetAlert2**

### Base de Datos
* **PostgreSQL** 


## Arquitectura de la Base de Datos

El proyecto implementa un diseño modular dividido en múltiples esquemas dentro de PostgreSQL:
* `seguridad.usuarios`: Información de cuentas, estados y encriptación.
* `academico.materias` y `academico.horarios`: Catálogo relacional de la carga académica del estudiante.
* `operaciones.tareas`: Registro transaccional de actividades escolares con estados de entrega y marcas de recordatorio enviado.
* `catalogo.roles` y `catalogo.prioridad`: Tablas estables para la integridad de datos.


##  Instalación y Configuración

### Requisitos Previos
* Java Development Kit (JDK) 17 o superior instalado.
* Maven 3.x instalado (o usar el empaquetador `./mvnw` incluido).
* Servidor PostgreSQL activo.

## 1. Traer el proyecto y clonarlo (Se uso IntellijIDEA, no eh probado en otro IDEs)

* git clone [https://github.com/AngelTorres2005/Proyecto_uniplanner.git](https://github.com/AngelTorres2005/Proyecto_uniplanner.git)
cd Proyecto_uniplanner

## 2. El script de la base de datos se encuentra en la carpeta database, crear la base de datos en un servidor postgre(tambien se puede usar otro pero el sistema ya tiene configurada la conexion para postgre)

## 3. Configurar el archivo application.properties en base a su servidor de base de datos, una vez listo correr el proyecto y no deberia haber problemas



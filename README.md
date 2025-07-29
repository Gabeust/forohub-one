
<h1 align="center">🧠 ForoHub Backend</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.13-brightgreen" />
  <img src="https://img.shields.io/badge/JWT-Authorization-blue" />
  <img src="https://img.shields.io/badge/Redis-Token%20Storage-red" />
  <img src="https://img.shields.io/badge/Kafka-Event%20Streaming-orange" />
  <img src="https://img.shields.io/badge/Docker-Ready-blue" />
</p>


<p align="center"><b>API backend para una plataforma de foros moderna y escalable.</b></p>

---

## 🚀 Tecnologías utilizadas

- Java 23
- Spring Boot
- Spring Security + JWT
- Redis para validación y almacenamiento de tokens
- Apache Kafka para comunicación de eventos
- MySQL como base de datos principal
- Docker y Docker Compose
- Maven

---

## 📌 Descripción

Este es el backend del proyecto **ForoHub**, un sistema robusto de foros que permite a los usuarios:

- Registrarse e iniciar sesión con JWT
- Crear y comentar publicaciones
- Reaccionar con emojis 
- Tener perfiles con nickname, foto, estadísticas
- Soporta notificaciones 
- Validación de tokens con Redis para alta performance
- Kafka para eventos como nuevos posts o reacciones

---

## 🐳 Docker

Puedes levantar todo el entorno con:

```bash
docker-compose up --build
```

Esto inicia:

- `backend`: API Spring Boot
- `mysql`: Base de datos persistente
- `redis`: Cache y autenticación JWT
- `kafka + zookeeper`: Streaming de eventos

---

## 🛡️ Seguridad

- Autenticación vía JWT (Access + Refresh tokens)
- Redis como blacklist y control de tokens expirados
- Roles: USER, MODERATOR, ADMIN
- Protección CORS y CSRF

---

## 🧠 Arquitectura general

```plaintext
Usuarios --> API REST (Spring Boot)
                 |
              Redis (JWT)
                 |
             MySQL + Kafka
```


## 📂 Estructura del proyecto

```plaintext
src/
 ┣ config/          → Configuración de seguridad, CORS, Redis
 ┣ controller/      → Endpoints públicos y protegidos
 ┣ dto/             → Clases de transferencia
 ┣ entity/          → Entidades JPA (User, Post, Comment...)
 ┣ repository/      → Interfaces de acceso a datos
 ┣ service/         → Lógica de negocio
 ┗ util/            → Utilidades como manejo de tokens
```


## 🤝 Autor

Gabriel Romero – [github.com/gabeust](https://github.com/gabeust)

---


# WorkSí

Plataforma de reclutamiento laboral que combina una aplicación móvil para candidatos y una aplicación web para administración y reclutamiento, con matching semántico asistido por inteligencia artificial.

**Repositorio:** https://github.com/AmaVetro/worksi

## Descripción del proyecto

WorkSí permite a los candidatos explorar ofertas laborales mediante una experiencia tipo swipe, postular con un porcentaje de compatibilidad calculado por IA y gestionar su perfil, postulaciones, ofertas guardadas y mensajería con empresas.

En la web, un administrador da de alta empresas y reclutadores; los reclutadores publican ofertas, revisan postulantes, consultan el desglose del score de matching y establecen contacto mediante mensajería asíncrona.

El sistema prioriza simplicidad arquitectónica, reducción de sesgos en el proceso de selección y una experiencia moderna para cada rol.

## Tecnologías utilizadas

| Capa | Tecnologías |
|------|-------------|
| Backend | Java 21, Spring Boot 3.2.x, Spring Security (JWT), JPA/Hibernate, Flyway, Apache Tika |
| Base de datos | MySQL 8 |
| Servicio IA | Python 3.11, FastAPI, sentence-transformers, spaCy, scikit-learn |
| App móvil | Kotlin, Android Studio, Jetpack Compose, Retrofit |
| App web | React, Vite |
| Infraestructura | Docker, Docker Compose |

## Estructura del equipo

| Integrante | Rol en el proyecto |
|------------|-------------------|
| Vicente Felipe Aravena Pavez | Desarrollo full stack / coordinación técnica |
| Ricardo Cuevas Rojas | Desarrollo backend y base de datos |
| Fabián Alberto Ortega Rojas | Desarrollo frontend web y móvil |

Lista oficial de integrantes: [`integrantes.txt`](integrantes.txt)

## Estructura del repositorio

```
worksi/
├── documentacion/          Documentación técnica, workflow y arquitectura
├── gestion/                Documento de definición e identificación del proyecto
├── producto/               Código fuente y configuración de ejecución
│   ├── backend/            API REST (Spring Boot)
│   ├── ai-service/         Servicio de matching (FastAPI)
│   ├── frontWeb/           Portal web ADMIN y RECRUITER (React)
│   ├── frontmovil/         App Android candidatos (Kotlin/Compose)
│   └── docker-compose.yml  Orquestación local de servicios
└── integrantes.txt         Nombres completos del equipo
```

## Documentación relevante

- [Estructura y arquitectura del proyecto](documentacion/ESTRUCTURA-Y-ARQUITECTURA.md)
- [Documento de definición e identificación](gestion/DOCUMENTO-DEFINICION-IDENTIFICACION.md)
- [Arranque local del entorno](producto/README.md) (sección 3 en adelante)
- [Ejecución de pruebas](producto/README.md) (sección 13)

## Entorno de producción (demo cloud)

Despliegue académico en **Vercel** (web) + **Railway** (MySQL, backend, IA).

| Componente | URL |
|------------|-----|
| Portal web (ADMIN / RECRUITER) | https://worksi.vercel.app |
| API backend | https://backend-production-f9dc.up.railway.app |
| Health backend | https://backend-production-f9dc.up.railway.app/health |
| Health IA (público, pruebas) | https://worksi-production.up.railway.app/health |

La app móvil en demo apunta al backend Railway (`RetrofitClient.kt`). Desarrollo local sigue usando Docker Compose (sección siguiente).

## Arranque rápido

Requisitos: Docker Desktop en ejecución, puertos `3306`, `8080` y `8000` libres.

```powershell
cd producto
docker compose up -d --build
```

Verificación:

```powershell
curl http://localhost:8080/health
curl http://localhost:8000/health
```

Instrucciones detalladas, variables de entorno y ejemplos de API en [`producto/README.md`](producto/README.md).

## Roles del sistema

| Rol | Canal | Función principal |
|-----|-------|-------------------|
| CANDIDATE | App móvil | Registro, perfil, swipe de ofertas, postulaciones, match y mensajería |
| RECRUITER | Web | Gestión de ofertas, revisión de postulantes, matching y mensajería |
| ADMIN | Web | Alta y gestión de empresas, reclutadores y ofertas globales |

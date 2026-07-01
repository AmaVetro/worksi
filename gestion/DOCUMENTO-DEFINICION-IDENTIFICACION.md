# Documento de registro de definición e identificación del proyecto

## WorkSí — Plataforma de reclutamiento laboral con matching inteligente

---

## 1. Identificación del proyecto

| Campo | Valor |
|-------|-------|
| Nombre del proyecto | WorkSí |
| Tipo de proyecto | Aplicación de software (móvil + web + backend + IA) |
| Institución | Duoc UC |
| Repositorio oficial | https://github.com/AmaVetro/worksi |
| Fecha de entrega referencial | 24 de junio de 2026 |

---

## 2. Equipo de trabajo

| N° | Nombre completo |
|----|-----------------|
| 1 | Vicente Felipe Aravena Pavez |
| 2 | Ricardo Cuevas Rojas |
| 3 | Fabián Alberto Ortega Rojas |

**Composición:** 3 integrantes.

**Distribución orientativa de responsabilidades:**

| Integrante | Áreas de contribución |
|------------|----------------------|
| Vicente Felipe Aravena Pavez | Coordinación, backend, integración IA, app móvil |
| Ricardo Cuevas Rojas | Backend, base de datos, API REST |
| Fabián Alberto Ortega Rojas | Frontend web, app móvil, experiencia de usuario |

Lista oficial en repositorio: `integrantes.txt`

---

## 3. Definición del problema

Los sistemas actuales de reclutamiento laboral presentan deficiencias recurrentes:

- Postulaciones masivas sin filtrado efectivo.
- Matching basado en palabras clave, poco preciso.
- Alto costo de revisión manual para reclutadores.
- Falta de transparencia en los criterios de selección.
- Presencia de sesgos (edad, género, nacionalidad, institución de procedencia, entre otros).
- Experiencias de usuario poco modernas para candidatos.

Las empresas, especialmente PYMEs y startups, necesitan herramientas que agilicen la preselección sin sacrificar calidad ni equidad en el proceso.

---

## 4. Definición de la solución

WorkSí es una plataforma de reclutamiento que integra:

1. **App móvil para candidatos:** exploración de ofertas con interfaz tipo swipe, visualización de porcentaje de compatibilidad, gestión de perfil, postulaciones, ofertas guardadas y mensajería con empresas tras match.

2. **Portal web para administración y reclutamiento:** el administrador gestiona empresas y reclutadores; los reclutadores publican ofertas, revisan postulantes, consultan el desglose del score y establecen contacto mediante mensajería.

3. **Motor de matching con IA:** servicio desacoplado que aplica fairness (eliminación de datos sensibles), genera embeddings semánticos y calcula compatibilidad entre CV y oferta laboral.

4. **Backend monolítico:** API REST centralizada con autenticación JWT, persistencia en MySQL y orquestación de la lógica de negocio.

---

## 5. Objetivos del proyecto

### 5.1 Objetivo general

Desarrollar una plataforma funcional de reclutamiento laboral que mejore la calidad del matching entre candidatos y ofertas, reduzca sesgos en el proceso de selección y ofrezca una experiencia moderna diferenciada por rol.

### 5.2 Objetivos específicos

1. Implementar registro, autenticación y gestión de perfiles para candidatos, reclutadores y administradores.
2. Permitir la publicación y administración de ofertas laborales con ciclo de vida completo.
3. Desarrollar un sistema de postulación con cálculo de compatibilidad basado en inteligencia artificial.
4. Aplicar filtros de fairness para mitigar sesgos antes del procesamiento semántico del CV.
5. Entregar interfaces móvil (candidatos) y web (admin/reclutador) alineadas a un contrato API unificado.
6. Habilitar mensajería asíncrona entre reclutador y candidato tras establecer match.
7. Documentar arquitectura, flujos y procedimientos de despliegue local reproducible y despliegue cloud demo (Sprint 13: Vercel + Railway).

---

## 6. Usuarios y stakeholders

### 6.1 Usuarios finales

| Usuario | Canal | Necesidad principal |
|---------|-------|---------------------|
| Candidato | App móvil Android | Encontrar empleo compatible de forma ágil y transparente |
| Reclutador | Portal web | Publicar ofertas y evaluar postulantes con apoyo de IA |
| Administrador | Portal web | Configurar empresas, reclutadores y supervisar el sistema |

### 6.2 Stakeholders

| Stakeholder | Interés |
|-------------|---------|
| Equipo de desarrollo | Entregar MVP funcional dentro del plazo académico |
| Docentes / evaluadores | Verificar cumplimiento de requisitos técnicos y de producto |
| Empresas empleadoras | Reducir tiempo de preselección y mejorar calidad de candidatos |
| Candidatos | Acceso justo y experiencia moderna en búsqueda de empleo |

---

## 7. Alcance del producto (MVP)

### 7.1 Dentro del alcance

- Registro y login de candidatos (móvil) y reclutadores/admin (web).
- CRUD de empresas y reclutadores por administrador.
- Creación, edición, activación, desactivación y eliminación lógica de ofertas.
- Feed de ofertas con swipe, postulación y score de compatibilidad.
- Desglose de matching para reclutadores (5 dimensiones).
- Fairness en pipeline de IA (regex + spaCy).
- Cancelación de postulación y ofertas guardadas.
- Match y mensajería asíncrona reclutador–candidato.
- Despliegue local con Docker Compose (MySQL + backend + IA).

### 7.2 Fuera del alcance

- Notificaciones push.
- OCR para CV escaneados (solo PDF con texto seleccionable).
- Cierre automático de ofertas al vencer `closing_date`.
- Microservicios adicionales más allá del servicio de IA.
- Dashboards analíticos avanzados.
- Eliminación de cuenta de usuario (HU-22, backlog opcional).

---

## 8. Propuesta de valor

| Beneficio | Descripción |
|-----------|-------------|
| Matching inteligente | Compatibilidad calculada con IA semántica, no solo keywords |
| Transparencia | Desglose del score visible para el reclutador |
| Reducción de sesgos | Fairness aplicado antes del embedding del CV |
| Experiencia moderna | Swipe móvil para candidatos; portal web operativo para empresas |
| Simplicidad técnica | Arquitectura monolítica + un servicio IA, desplegable con Docker |

---

## 9. Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Java 21, Spring Boot 3.2.x, Spring Security, JPA, Flyway, Apache Tika |
| Base de datos | MySQL 8 |
| IA | Python 3.11, FastAPI, sentence-transformers, spaCy, scikit-learn |
| Móvil | Kotlin, Jetpack Compose, Retrofit |
| Web | React, Vite |
| Infraestructura | Docker, Docker Compose |

---

## 10. Arquitectura resumida

- **Backend monolítico** (Spring Boot) expone API REST en puerto 8080.
- **Servicio IA** (FastAPI) expone `POST /match` en puerto 8000.
- **MySQL 8** como base de datos relacional única en puerto 3306.
- **Filesystem** para almacenamiento de CV e imágenes de empresa.
- Comunicación Spring Boot → FastAPI vía HTTP.
- Clientes móvil y web consumen la API con JWT.

Documentación detallada: [`documentacion/ESTRUCTURA-Y-ARQUITECTURA.md`](../documentacion/ESTRUCTURA-Y-ARQUITECTURA.md)

---

## 11. Metodología y planificación

| Aspecto | Definición |
|---------|------------|
| Metodología | Desarrollo iterativo por sprints |
| Duración referencial | 13 semanas |
| Esfuerzo estimado | ~155 horas de equipo |
| Equipo | 3 integrantes |
| Herramienta de control de versiones | Git / GitHub |
| Documentación de sprints | `documentacion/Documentación de workflow/Sprint oficiales.txt` |

---

## 12. Entregables del proyecto

### 12.1 Producto

- Código fuente: backend, servicio IA, app web y app móvil.
- Scripts de base de datos: migraciones Flyway (creación de tablas y datos semilla).
- Configuración de ejecución: `docker-compose.yml`, Dockerfiles, dependencias.

### 12.2 Documentación

- Documento técnico y contrato API unificado.
- Estructura y arquitectura del proyecto (`ESTRUCTURA-Y-ARQUITECTURA.md`).
- Flujos de pantallas (móvil y web).
- Historias de usuario y plan de sprints.
- Informe académico (carpeta `documentacion/`).

### 12.3 Gestión

- Este documento de definición e identificación.
- Archivo de integrantes (`integrantes.txt`).

---

## 13. Criterios de éxito

1. El repositorio es público y accesible en la URL registrada.
2. El entorno local se levanta con Docker Compose sin errores.
3. Un candidato puede registrarse, postular y ver su porcentaje de compatibilidad.
4. Un reclutador puede crear ofertas y revisar postulantes con desglose de score.
5. El servicio de IA responde correctamente al endpoint `/match`.
6. La mensajería post-match funciona entre reclutador y candidato.
7. La documentación describe arquitectura, roles y procedimiento de arranque.

---

## 14. Riesgos identificados

| Riesgo | Mitigación |
|--------|------------|
| PDF sin texto seleccionable | Validación en cliente y mensaje claro al usuario |
| Sesgos residuales en matching | Doble filtro fairness (regex + spaCy) |
| Complejidad de integración IA | Servicio desacoplado con contrato HTTP simple |
| Plazos académicos ajustados | Alcance MVP definido y priorizado por sprints |
| Repositorio privado en evaluación | Repositorio configurado como público |

---

## 15. Conclusión

WorkSí responde a la necesidad de modernizar el proceso de reclutamiento laboral mediante una plataforma que combina experiencia de usuario contemporánea, matching semántico asistido por IA y medidas de fairness para reducir sesgos. El proyecto se desarrolla con un stack tecnológico estándar de la industria, arquitectura deliberadamente simple y documentación que permite su evaluación, reproducción y evolución futura.

---

*Documento de registro — Proyecto WorkSí — Equipo Aravena / Cuevas / Ortega.*

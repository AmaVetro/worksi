# WorkSi - Arranque local (Sprint 1)

Este documento define el arranque mínimo para que cualquier integrante clone el repo y levante el entorno base del proyecto usando Docker Compose.

## 1) Requisitos previos

- Docker Desktop instalado y en ejecución.
- Git instalado.
- Estos puertos deben estar libres en la máquina:
  - `3306` (MySQL)
  - `8080` (Backend Spring Boot)
  - `8000` (AI FastAPI)

## 2) Estructura relevante

Desde la raíz del repo (`worksi`), el código del producto vive en:

- `producto/backend`
- `producto/ai-service`
- `producto/docker-compose.yml`

## 3) Levantar el entorno

Abrir PowerShell y ejecutar:

```powershell
cd "C:\RUTA\A\worksi\producto"
docker compose up -d --build
```

## 4) Verificar que todo quedo operativo

### 4.1 Estado de contenedores

```powershell
docker compose ps
```

Se espera ver:

- `worksi-mysql` en estado `healthy`
- `worksi-backend` en estado `Up`
- `worksi-ai` en estado `Up`

### 4.2 Health checks de servicios

```powershell
curl http://localhost:8080/health
curl http://localhost:8000/health
```

Respuesta esperada en ambos casos:

```json
{"status":"UP"}
```

### 4.3 API Sprint 1 (catálogos públicos)

Regiones (público):

```powershell
curl http://localhost:8080/api/v1/catalogs/regions
```

Comunas por región (público; `region_id` numérico según filas en `regions`):

```powershell
curl http://localhost:8080/api/v1/catalogs/regions/1/communes
```

(Usa un `region_id` que exista en la respuesta de regiones; las comunas deben corresponder a esa región.)

## 5) Puertos del entorno

- MySQL: `localhost:3306`
- Backend: `localhost:8080`
- AI: `localhost:8000`

## 6) Variables minimas usadas por Docker Compose

En `producto/docker-compose.yml` se usan estos valores (con defaults):

- `MYSQL_ROOT_PASSWORD` (default: `root`)
- `MYSQL_DATABASE` (default: `worksi`)
- `MYSQL_USER` (default: `worksi`)
- `MYSQL_PASSWORD` (default: `worksi`)

Tambien se inyectan al backend:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `WORKSI_CV_STORAGE_DIR` (default operativo en compose: `/data/cv`)

## 7) Apagar el entorno

```powershell
docker compose down
```

## 8) Resetear entorno y borrar volumenes de datos

Usar sólo si se necesita reiniciar base de datos, su contenido y volumenes:

```powershell
docker compose down -v
```

Esto elimina los volumenes creados por Compose (incluido MySQL y volumen de CV).

## 9) Autenticacion en cliente (app movil) y referencia futura web

Esta seccion documenta donde vive la sesion en el cliente actual y el cierre de sesion, para alinear al equipo sin depender del frontend web (aun no existe en el repo).

**App Android (`producto/frontmovil`)**

- **Access JWT**: se guarda solo el access token (sin refresh), en `EncryptedSharedPreferences` mediante `com.worksi.app.data.local.SecureTokenStore` (nombre interno del almacen `worksi_auth_secure`, clave `access_token`). Se inicializa en `MainActivity` antes de componer la UI.
- **Logout**: en la pantalla de sesion de prueba (`SessionPlaceholderScreen`), el boton de cerrar sesion llama a `SecureTokenStore.clear()` y navega al flujo de bienvenida limpiando el back stack.
- **Recuperacion de contrasena (mock)**: las pantallas de recuperacion llaman a `POST /api/v1/auth/password-recovery/request`, `verify` y `reset` segun el Contrato Unificado API. No hay envio real de correo; en backend el codigo de un solo uso se emite en log nivel **DEBUG** para entornos de desarrollo (`Recuperacion mock codigo email=... codigo=...` en `PasswordRecoveryService`).
- **Politica de contrasena** (registro candidato en app, alta reclutador, recuperacion): minimo 10 caracteres; al menos una mayuscula, una minuscula, un numero y un simbolo. Backend: `cl.duoc.worksi.validation.PasswordRules`; app Android: `com.worksi.app.validation.PasswordPolicy` (misma regla que el backend).

**Web (futuro Sprint 7 / cuando exista el proyecto web)**

- Checklist: persistir el access JWT segun el mismo contrato (solo access); logout debe invalidar en cliente borrando el token almacenado y redirigiendo al login unificado; conectar recuperacion cuando la UI este en alcance.

## 10) Verificacion Sprint 2 (HU-02 admin) — Postman / curl

Flujo feliz (B5): login **ADMIN** sembrado (migracion `V6__seed_system_admin.sql`, email `admin@dominio.cl`) con la contrasena que el equipo acordó para el hash bcrypt de esa migracion → **POST** multipart empresa → **GET** empresas paginado → **POST** reclutador → login **RECRUITER** → intento **GET** `/api/v1/admin/companies` con token reclutador → **401/403** según configuracion (sin rol ADMIN).

**Variables**: `BASE=http://localhost:8080`, `TOKEN_ADMIN` y `TOKEN_REC` obtenidos de `POST /api/v1/auth/login`.

**Login admin** (ajustar contrasena):

```powershell
curl -s -X POST "$BASE/api/v1/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"admin@dominio.cl\",\"password\":\"SU_PASSWORD_ADMIN\"}"
```

**Crear empresa** (parte `data` JSON; `region_id`, `commune_id`, `sector_id` deben existir en BD, comuna coherente con region; `rut` chileno valido):

```powershell
curl -s -X POST "$BASE/api/v1/admin/companies" -H "Authorization: Bearer TOKEN_ADMIN" -F "data={\"commercial_name\":\"Empresa Demo\",\"legal_name\":\"Empresa Demo SPA\",\"phone\":\"+56912345678\",\"address\":\"Calle 123\",\"rut\":\"76123456-7\",\"region_id\":7,\"commune_id\":XX,\"sector_id\":1,\"worker_count_approx\":10};type=application/json"
```

Sustituir `commune_id` por un id valido para esa region (por ejemplo tras `GET /api/v1/catalogs/regions` y `GET /api/v1/catalogs/regions/{id}/communes`). La respuesta **201** incluye `company_id`.

**Listar empresas**:

```powershell
curl -s "$BASE/api/v1/admin/companies?page=1&size=20&sort=created_at,desc" -H "Authorization: Bearer TOKEN_ADMIN"
```

**Crear reclutador** (`password` debe cumplir politica del backend: mayuscula, minuscula, numero, simbolo, longitud minima 10):

```powershell
curl -s -X POST "$BASE/api/v1/admin/recruiters" -H "Authorization: Bearer TOKEN_ADMIN" -H "Content-Type: application/json" -d "{\"email\":\"reclutador@test.cl\",\"password\":\"Aa!1234567890\",\"first_name\":\"Ana\",\"last_name_paternal\":\"Rojas\",\"last_name_maternal\":\"Soto\",\"rut\":\"12345678-5\",\"phone\":null,\"mobile\":\"+56912345678\",\"birth_date\":\"1990-05-15\",\"company_id\":COMPANY_ID}"
```

**Login reclutador** y prueba de acceso admin sin rol:

```powershell
curl -s -X POST "$BASE/api/v1/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"reclutador@test.cl\",\"password\":\"Aa!1234567890\"}"
curl -s -o NUL -w "%{http_code}" "$BASE/api/v1/admin/companies?page=1&size=5" -H "Authorization: Bearer TOKEN_REC"
```

Se espera codigo HTTP **403** (o **401** si el filtro JWT no autentica) al llamar recurso **/api/v1/admin/** con usuario **RECRUITER**.

**Imagen empresa (opcional)**: misma peticion multipart añadiendo archivo en parte `image` (PNG o JPEG); el backend guarda bajo `worksi.storage.company-images` (por defecto `{WORKSI_CV_STORAGE_DIR}/company-images` en Docker: `/data/cv/company-images`).

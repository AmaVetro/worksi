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

### 4.3 API Sprint 1 (catálogos y validación de registro candidato, sin persistir alta)

Regiones (público):

```powershell
curl http://localhost:8080/api/v1/catalogs/regions
```

Comunas por región (público; `region_id` numérico según filas en `regions`):

```powershell
curl http://localhost:8080/api/v1/catalogs/regions/1/communes
```

Validación de datos de registro candidato (no crea usuario; útil con Postman). Debe usarse cuerpo JSON alineado al contrato (campos `email`, `password`, nombres, `phone`, `rut`, `document_number`, `region_id`, `commune_id`, etc.); ejemplo mínima de forma:

```powershell
curl -X POST http://localhost:8080/api/v1/validation/candidate-registration -H "Content-Type: application/json" -d "{\"email\":\"nuevo@ejemplo.cl\",\"password\":\"Abcdefghij1!\",\"first_name\":\"Juan\",\"last_name_paternal\":\"Perez\",\"last_name_maternal\":\"Lopez\",\"phone\":\"+56912345678\",\"rut\":\"11111111-1\",\"document_number\":\"11111111\",\"region_id\":1,\"commune_id\":1}"
```

(Usa un par `region_id` / `commune_id` coherente; con la semilla por defecto, la región 1 y la comuna 1 suelen corresponder a Arica y Parinacota. Si falla, consulta `GET /api/v1/catalogs/regions` y `GET /api/v1/catalogs/regions/{region_id}/communes` y asegúrate de que el correo no exista en `users`.)

Éxito: `{"valid":true}`. Error de validación: cuerpo JSON de error bajo el manejador global (p. ej. email duplicado, política de contraseña, RUT, comuna no pertenece a región).

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

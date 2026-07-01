# WorkSí — frontWeb

Portal web React + Vite para roles **ADMIN** y **RECRUITER**.

## Producción

- URL: https://worksi.vercel.app
- Root en Vercel: `producto/frontWeb`
- Proxy API: `vercel.json` reenvía `/api/*` a `https://backend-production-f9dc.up.railway.app`

## Desarrollo local

```powershell
cd producto/frontWeb
npm install
npm run dev
```

El proxy de desarrollo hacia el backend local está en `vite.config.js` (`localhost:8080`).

## Más información

- [`producto/README.md`](../README.md) — arranque local, entorno cloud y auth en cliente

# Tower Defense Frontend UI

This frontend includes:

- PvZ-like game interface (lanes, cells, side panels, HUD, event feed)
- SVG asset pack (minimal-clean style) for UI, entities, and board
- SDK for backend REST operations
- SSE client + polling fallback
- Hooks for connection state and commands
- Vitest coverage for SDK, hooks, UI interactions, and SVG exports

## Scripts

- `npm run dev` start Vite dev server
- `npm run test` run Vitest once
- `npm run test:watch` run Vitest in watch mode
- `npm run build` build production bundle

## Environment

Copy `.env.example` to `.env` and adjust if needed.

If `VITE_API_BASE_URL` is empty, Vite proxy routes are used during dev:

- `/health`
- `/game/*`
- `/towers/*`
- `/wave/*`

## UI controls overview

- **Top HUD**: coins, base health, connection status, version
- **Build panel**: build tower at selected lane/cell
- **Board**: click empty cells to set build target; click tower cells to select tower
- **Upgrade panel**: apply `RAPID_FIRE`, `FREEZE_SHOT`, `REINFORCED` to selected tower
- **Wave controls**: start wave, resolve step, auto-resolve
- **Event feed + toasts**: action log and API/domain errors

## Local run flow

1. Start backend (`demo`) at `http://localhost:4567`
2. Start frontend in `front/`:
   - `npm run dev`
3. Open the app in the Vite URL (usually `http://localhost:5173`)

## Optional runtime console helpers

`GamePage` exposes helper methods in `window.gameRuntime` for quick manual checks:

```js
await window.gameRuntime.runSmokeFlow()
```

You can also invoke command helpers directly:

```js
await window.gameRuntime.commands.startGame()
await window.gameRuntime.commands.buildTower('tower-manual')
await window.gameRuntime.commands.addUpgrade('tower-manual', 'rapid_fire')
await window.gameRuntime.commands.startWave()
await window.gameRuntime.commands.resolveWave()
```

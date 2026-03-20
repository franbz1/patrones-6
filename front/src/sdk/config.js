export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
export const REQUEST_TIMEOUT_MS = Number(import.meta.env.VITE_REQUEST_TIMEOUT_MS ?? 8000)
export const POLLING_FALLBACK_MS = Number(import.meta.env.VITE_POLLING_FALLBACK_MS ?? 2000)
export const POLLING_MAX_BACKOFF_MS = Number(import.meta.env.VITE_POLLING_MAX_BACKOFF_MS ?? 10000)


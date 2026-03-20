import { API_BASE_URL, REQUEST_TIMEOUT_MS } from './config'

function buildUrl(path) {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  return `${API_BASE_URL}${path}`
}

function toApiError(status, payload, fallbackMessage) {
  const error = new Error(payload?.message || fallbackMessage)
  error.status = status ?? 0
  error.code = payload?.code || 'network_error'
  error.stateVersion = payload?.stateVersion ?? null
  error.details = payload?.details ?? null
  return error
}

export async function request(path, options = {}) {
  const controller = new AbortController()
  const timeout = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)

  try {
    const response = await fetch(buildUrl(path), {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {}),
      },
      signal: controller.signal,
    })

    const text = await response.text()
    const payload = text ? JSON.parse(text) : null

    if (!response.ok) {
      throw toApiError(response.status, payload, `Request failed with status ${response.status}.`)
    }

    return payload
  } catch (error) {
    if (error.name === 'AbortError') {
      throw toApiError(0, null, 'Request timeout.')
    }
    if (error.code && error.message) {
      throw error
    }
    throw toApiError(0, null, error.message || 'Unexpected network error.')
  } finally {
    clearTimeout(timeout)
  }
}


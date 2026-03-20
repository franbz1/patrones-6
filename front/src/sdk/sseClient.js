import { API_BASE_URL } from './config'

function createSseUrl(path) {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  return `${API_BASE_URL}${path}`
}

export function createSseClient({
  path = '/game/events',
  onOpen,
  onEvent,
  onError,
} = {}) {
  let source = null

  return {
    connect() {
      if (source) return
      source = new EventSource(createSseUrl(path))

      source.onopen = () => {
        if (onOpen) onOpen()
      }

      source.onmessage = (messageEvent) => {
        if (!onEvent) return
        let parsed = null
        try {
          parsed = JSON.parse(messageEvent.data)
        } catch {
          parsed = { raw: messageEvent.data }
        }
        onEvent({ type: 'message', payload: parsed })
      }

      source.onerror = (event) => {
        if (onError) onError(event)
      }
    },

    disconnect() {
      if (!source) return
      source.close()
      source = null
    },

    isConnected() {
      return !!source && source.readyState === EventSource.OPEN
    },
  }
}


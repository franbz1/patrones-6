import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { POLLING_FALLBACK_MS, POLLING_MAX_BACKOFF_MS } from '../sdk/config'
import { gameSdk } from '../sdk/gameSdk'
import { createSseClient } from '../sdk/sseClient'

function nextBackoff(current) {
  return Math.min(current * 2, POLLING_MAX_BACKOFF_MS)
}

export function useGameConnection() {
  const [gameState, setGameState] = useState(null)
  const [connectionStatus, setConnectionStatus] = useState('idle')
  const [lastError, setLastError] = useState(null)

  const refreshInFlightRef = useRef(false)
  const refreshPendingRef = useRef(false)
  const pollingTimerRef = useRef(null)
  const pollingDelayRef = useRef(POLLING_FALLBACK_MS)
  const sseClientRef = useRef(null)

  const refreshState = useCallback(async () => {
    if (refreshInFlightRef.current) {
      refreshPendingRef.current = true
      return
    }

    refreshInFlightRef.current = true
    try {
      const state = await gameSdk.getGameState()
      setGameState(state)
      setLastError(null)
    } catch (error) {
      setLastError(error)
      throw error
    } finally {
      refreshInFlightRef.current = false
      if (refreshPendingRef.current) {
        refreshPendingRef.current = false
        void refreshState()
      }
    }
  }, [])

  const stopPolling = useCallback(() => {
    if (pollingTimerRef.current) {
      clearTimeout(pollingTimerRef.current)
      pollingTimerRef.current = null
    }
  }, [])

  const startPolling = useCallback(() => {
    stopPolling()

    const run = async () => {
      try {
        await refreshState()
        pollingDelayRef.current = POLLING_FALLBACK_MS
      } catch {
        pollingDelayRef.current = nextBackoff(pollingDelayRef.current)
      } finally {
        pollingTimerRef.current = setTimeout(run, pollingDelayRef.current)
      }
    }

    pollingTimerRef.current = setTimeout(run, pollingDelayRef.current)
  }, [refreshState, stopPolling])

  const reconnect = useCallback(() => {
    if (sseClientRef.current) {
      sseClientRef.current.disconnect()
      sseClientRef.current.connect()
      setConnectionStatus('reconnecting')
      return
    }
    setConnectionStatus('reconnecting')
    void refreshState()
  }, [refreshState])

  useEffect(() => {
    let mounted = true

    const sseClient = createSseClient({
      onOpen: () => {
        if (!mounted) return
        setConnectionStatus('connected')
        stopPolling()
      },
      onEvent: () => {
        if (!mounted) return
        void refreshState()
      },
      onError: () => {
        if (!mounted) return
        setConnectionStatus('disconnected')
        startPolling()
      },
    })

    sseClientRef.current = sseClient
    setConnectionStatus('connecting')
    sseClient.connect()
    void refreshState()

    return () => {
      mounted = false
      stopPolling()
      sseClient.disconnect()
    }
  }, [refreshState, startPolling, stopPolling])

  return useMemo(() => ({
    gameState,
    connectionStatus,
    lastError,
    reconnect,
    refreshState,
  }), [connectionStatus, gameState, lastError, reconnect, refreshState])
}


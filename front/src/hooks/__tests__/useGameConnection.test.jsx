import { renderHook, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useGameConnection } from '../useGameConnection'

vi.mock('../../sdk/config', () => ({
  POLLING_FALLBACK_MS: 20,
  POLLING_MAX_BACKOFF_MS: 100,
}))

vi.mock('../../sdk/gameSdk', () => ({
  gameSdk: {
    getGameState: vi.fn(async () => ({ stateVersion: 1 })),
  },
}))

vi.mock('../../sdk/sseClient', () => ({
  createSseClient: vi.fn(({ onError }) => ({
    connect: () => onError?.(new Event('error')),
    disconnect: vi.fn(),
    isConnected: vi.fn(() => false),
  })),
}))

import { gameSdk } from '../../sdk/gameSdk'

describe('useGameConnection', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('falls back to polling after SSE error', async () => {
    renderHook(() => useGameConnection())

    await waitFor(() => {
      expect(gameSdk.getGameState).toHaveBeenCalledTimes(1)
    })

    await waitFor(() => {
      expect(gameSdk.getGameState.mock.calls.length).toBeGreaterThan(1)
    }, { timeout: 500 })
  }, 3000)
})


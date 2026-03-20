import { renderHook } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useGameCommands } from '../useGameCommands'

vi.mock('../../sdk/gameSdk', () => ({
  gameSdk: {
    buildTower: vi.fn(),
  },
}))

import { gameSdk } from '../../sdk/gameSdk'

describe('useGameCommands', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('refreshes state on version conflict', async () => {
    const refreshState = vi.fn(async () => {})
    gameSdk.buildTower.mockRejectedValue({
      status: 409,
      code: 'version_conflict',
      message: 'Expected version mismatch.',
    })

    const { result } = renderHook(() =>
      useGameCommands({
        gameState: { stateVersion: 7 },
        refreshState,
      }),
    )

    const response = await result.current.buildTower('tower-9')

    expect(refreshState).toHaveBeenCalledTimes(1)
    expect(response.ok).toBe(false)
    expect(response.conflict).toBe(true)
  })
})


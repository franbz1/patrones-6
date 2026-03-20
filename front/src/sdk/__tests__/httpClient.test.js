import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { request } from '../httpClient'

describe('httpClient', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.clearAllMocks()
  })

  it('returns parsed json on success', async () => {
    fetch.mockResolvedValue({
      ok: true,
      text: async () => JSON.stringify({ status: 'ok' }),
    })

    await expect(request('/health')).resolves.toEqual({ status: 'ok' })
  })

  it('maps backend error payload to normalized error', async () => {
    fetch.mockResolvedValue({
      ok: false,
      status: 409,
      text: async () => JSON.stringify({
        code: 'version_conflict',
        message: 'Expected version mismatch.',
        stateVersion: 10,
        details: null,
      }),
    })

    await expect(request('/game/start')).rejects.toMatchObject({
      status: 409,
      code: 'version_conflict',
      message: 'Expected version mismatch.',
      stateVersion: 10,
    })
  })
})


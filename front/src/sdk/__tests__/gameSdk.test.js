import { describe, expect, it, vi, beforeEach } from 'vitest'

vi.mock('../httpClient', () => ({
  request: vi.fn(async () => ({ ok: true })),
}))

import { request } from '../httpClient'
import { gameSdk } from '../gameSdk'

describe('gameSdk', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('builds tower payload with command metadata', async () => {
    await gameSdk.buildTower({
      towerId: 'tower-1',
      commandId: 'cmd-1',
      expectedVersion: 4,
    })

    const [, options] = request.mock.calls[0]
    expect(request.mock.calls[0][0]).toBe('/towers')
    expect(options.method).toBe('POST')
    expect(JSON.parse(options.body)).toEqual({
      towerId: 'tower-1',
      commandId: 'cmd-1',
      expectedVersion: 4,
    })
  })

  it('builds upgrade payload with tower route param', async () => {
    await gameSdk.addUpgrade({
      towerId: 'tower-1',
      upgradeType: 'rapid_fire',
      commandId: 'cmd-2',
      expectedVersion: 5,
    })

    const [, options] = request.mock.calls[0]
    expect(request.mock.calls[0][0]).toBe('/towers/tower-1/upgrades')
    expect(options.method).toBe('POST')
    expect(JSON.parse(options.body)).toEqual({
      upgradeType: 'rapid_fire',
      commandId: 'cmd-2',
      expectedVersion: 5,
    })
  })
})


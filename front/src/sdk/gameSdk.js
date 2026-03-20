import { request } from './httpClient'

function compactCommandPayload(command = {}) {
  const payload = {}
  if (command.commandId) payload.commandId = command.commandId
  if (command.expectedVersion !== undefined && command.expectedVersion !== null) {
    payload.expectedVersion = command.expectedVersion
  }
  return payload
}

export const gameSdk = {
  health() {
    return request('/health')
  },

  getGameState() {
    return request('/game/state')
  },

  startGame(command = {}) {
    return request('/game/start', {
      method: 'POST',
      body: JSON.stringify(compactCommandPayload(command)),
    })
  },

  buildTower({ towerId, ...command }) {
    return request('/towers', {
      method: 'POST',
      body: JSON.stringify({
        ...compactCommandPayload(command),
        towerId,
      }),
    })
  },

  addUpgrade({ towerId, upgradeType, ...command }) {
    return request(`/towers/${towerId}/upgrades`, {
      method: 'POST',
      body: JSON.stringify({
        ...compactCommandPayload(command),
        upgradeType,
      }),
    })
  },

  startWave(command = {}) {
    return request('/wave/start', {
      method: 'POST',
      body: JSON.stringify(compactCommandPayload(command)),
    })
  },

  resolveWave(command = {}) {
    return request('/wave/resolve', {
      method: 'POST',
      body: JSON.stringify(compactCommandPayload(command)),
    })
  },
}


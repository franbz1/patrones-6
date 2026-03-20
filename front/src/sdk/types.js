/**
 * @typedef {Object} ApiError
 * @property {number} status
 * @property {string} code
 * @property {string} message
 * @property {number | null} stateVersion
 * @property {unknown} details
 */

/**
 * @typedef {Object} PlayerView
 * @property {number} coins
 * @property {number} baseHealth
 */

/**
 * @typedef {Object} TowerView
 * @property {string} id
 * @property {string} name
 * @property {number} damage
 * @property {number} attacksPerResolve
 * @property {number} freezeTurns
 * @property {number} durability
 * @property {string[]} upgrades
 */

/**
 * @typedef {Object} EnemyView
 * @property {string} id
 * @property {number} health
 * @property {number} distanceToBase
 * @property {number} frozenTurns
 * @property {string} status
 */

/**
 * @typedef {Object} WaveView
 * @property {number} index
 * @property {string} status
 * @property {EnemyView[]} enemies
 */

/**
 * @typedef {Object} GameStateResponse
 * @property {number} stateVersion
 * @property {boolean} gameStarted
 * @property {PlayerView} player
 * @property {TowerView[]} towers
 * @property {WaveView | null} wave
 */

export const API_EVENTS = {
  STATE_UPDATED: 'state_updated',
  SSE_CONNECTED: 'sse_connected',
  GAME_STARTED: 'game_started',
  WAVE_RESOLVED: 'wave_resolved',
}


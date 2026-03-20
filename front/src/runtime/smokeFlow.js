import { gameSdk } from '../sdk/gameSdk'

function command(stateVersion) {
  const commandId = typeof crypto !== 'undefined' && crypto.randomUUID
    ? crypto.randomUUID()
    : `cmd-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`

  return {
    commandId,
    expectedVersion: stateVersion ?? null,
  }
}

export async function runSmokeFlow() {
  const checkpoints = []

  let state = await gameSdk.startGame(command(null))
  checkpoints.push({ step: 'startGame', stateVersion: state.stateVersion })

  state = await gameSdk.buildTower({
    towerId: 'tower-smoke',
    ...command(state.stateVersion),
  })
  checkpoints.push({ step: 'buildTower', towers: state.towers.length, coins: state.player.coins })

  state = await gameSdk.addUpgrade({
    towerId: 'tower-smoke',
    upgradeType: 'rapid_fire',
    ...command(state.stateVersion),
  })
  checkpoints.push({ step: 'addUpgrade', upgrades: state.towers[0]?.upgrades ?? [] })

  state = await gameSdk.startWave(command(state.stateVersion))
  checkpoints.push({ step: 'startWave', waveStatus: state.wave?.status })

  state = await gameSdk.resolveWave(command(state.stateVersion))
  checkpoints.push({
    step: 'resolveWave',
    waveStatus: state.wave?.status,
    coins: state.player.coins,
    baseHealth: state.player.baseHealth,
  })

  return checkpoints
}


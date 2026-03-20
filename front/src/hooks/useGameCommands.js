import { useCallback } from 'react'
import { gameSdk } from '../sdk/gameSdk'

function generateCommandId() {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID()
  }
  return `cmd-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`
}

function commandMeta(gameState) {
  return {
    commandId: generateCommandId(),
    expectedVersion: gameState?.stateVersion ?? null,
  }
}

export function useGameCommands({ gameState, refreshState }) {
  const execute = useCallback(async (action) => {
    try {
      const result = await action()
      return { ok: true, data: result, conflict: false }
    } catch (error) {
      if (error?.status === 409 && error?.code === 'version_conflict') {
        await refreshState()
        return { ok: false, data: null, conflict: true, error }
      }
      return { ok: false, data: null, conflict: false, error }
    }
  }, [refreshState])

  const startGame = useCallback(() => execute(() => gameSdk.startGame(commandMeta(gameState))), [execute, gameState])
  const startWave = useCallback(() => execute(() => gameSdk.startWave(commandMeta(gameState))), [execute, gameState])
  const resolveWave = useCallback(() => execute(() => gameSdk.resolveWave(commandMeta(gameState))), [execute, gameState])

  const buildTower = useCallback((towerId) => execute(
    () => gameSdk.buildTower({ towerId, ...commandMeta(gameState) }),
  ), [execute, gameState])

  const addUpgrade = useCallback((towerId, upgradeType) => execute(
    () => gameSdk.addUpgrade({ towerId, upgradeType, ...commandMeta(gameState) }),
  ), [execute, gameState])

  return {
    startGame,
    startWave,
    resolveWave,
    buildTower,
    addUpgrade,
  }
}


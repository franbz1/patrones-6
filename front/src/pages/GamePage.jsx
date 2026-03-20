import { useEffect, useMemo, useState } from 'react'
import { useGameConnection } from '../hooks/useGameConnection'
import { useGameCommands } from '../hooks/useGameCommands'
import { TopHud } from '../components/layout/TopHud'
import { GameBoard } from '../components/board/GameBoard'
import { BuildPanel } from '../components/panels/BuildPanel'
import { UpgradePanel } from '../components/panels/UpgradePanel'
import { WaveControls } from '../components/controls/WaveControls'
import { EventFeed } from '../components/feedback/EventFeed'
import { ErrorToastStack } from '../components/feedback/ErrorToastStack'
import { runSmokeFlow } from '../runtime/smokeFlow'

const LANE_COUNT = 5
const CELL_COUNT = 6

function initialSelection() {
  return { lane: 0, cell: 0 }
}

function towerIdForCell(lane, cell) {
  return `tower-l${lane}-c${cell}`
}

function mapTowerToLane(towerId) {
  const match = /^tower-l(\d+)-c(\d+)$/.exec(towerId)
  if (!match) return null
  return {
    lane: Number(match[1]),
    cell: Number(match[2]),
  }
}

export function GamePage() {
  const connection = useGameConnection()
  const commands = useGameCommands({
    gameState: connection.gameState,
    refreshState: connection.refreshState,
  })

  const [selectedCell, setSelectedCell] = useState(initialSelection)
  const [selectedTowerId, setSelectedTowerId] = useState(null)
  const [events, setEvents] = useState([])
  const [errors, setErrors] = useState([])
  const [autoResolve, setAutoResolve] = useState(false)

  const gameState = connection.gameState
  const towers = useMemo(() => gameState?.towers ?? [], [gameState?.towers])
  const enemies = useMemo(() => gameState?.wave?.enemies ?? [], [gameState?.wave?.enemies])
  const waveStatus = gameState?.wave?.status ?? 'NOT_STARTED'

  const boardTowers = useMemo(() => {
    const byLaneCell = new Map()
    for (const tower of towers) {
      const mapped = mapTowerToLane(tower.id)
      if (mapped) {
        byLaneCell.set(`${mapped.lane}-${mapped.cell}`, tower)
      }
    }
    return byLaneCell
  }, [towers])

  const appendEvent = (text, level = 'info') => {
    setEvents((prev) => [
      { id: `${Date.now()}-${Math.random()}`, level, text },
      ...prev.slice(0, 19),
    ])
  }

  const pushError = (error) => {
    if (!error) return
    const entry = {
      id: `${Date.now()}-${Math.random()}`,
      code: error.code || 'error',
      message: error.message || 'Unexpected error.',
    }
    setErrors((prev) => [entry, ...prev.slice(0, 3)])
    appendEvent(`[${entry.code}] ${entry.message}`, 'error')
  }

  const dismissError = (id) => {
    setErrors((prev) => prev.filter((error) => error.id !== id))
  }

  const handleResult = (actionName, result) => {
    if (result?.ok) {
      appendEvent(`${actionName} completed.`, 'success')
      return true
    }
    if (result?.conflict) {
      appendEvent(`${actionName} version conflict; state refreshed.`, 'warn')
      return false
    }
    pushError(result?.error)
    return false
  }

  const handleBuildTower = async () => {
    const towerId = towerIdForCell(selectedCell.lane, selectedCell.cell)
    const result = await commands.buildTower(towerId)
    if (handleResult(`Build ${towerId}`, result)) {
      setSelectedTowerId(towerId)
    }
  }

  const handleUpgrade = async (upgradeType) => {
    if (!selectedTowerId) {
      pushError({ code: 'tower_not_selected', message: 'Select a tower before upgrading.' })
      return
    }
    const result = await commands.addUpgrade(selectedTowerId, upgradeType)
    handleResult(`Upgrade ${selectedTowerId} with ${upgradeType}`, result)
  }

  const handleStartGame = async () => {
    const result = await commands.startGame()
    if (handleResult('Start game', result)) {
      setSelectedTowerId(null)
      setSelectedCell(initialSelection())
    }
  }

  const handleStartWave = async () => {
    const result = await commands.startWave()
    handleResult('Start wave', result)
  }

  const handleResolveWave = async () => {
    const result = await commands.resolveWave()
    handleResult('Resolve wave', result)
  }

  useEffect(() => {
    window.gameRuntime = {
      connection,
      commands,
      runSmokeFlow,
    }
  }, [commands, connection])

  useEffect(() => {
    if (gameState?.stateVersion !== undefined) {
      appendEvent(`State updated to v${gameState.stateVersion}.`)
    }
  }, [gameState?.stateVersion])

  useEffect(() => {
    if (!autoResolve || waveStatus !== 'IN_PROGRESS') return undefined
    const timer = setInterval(() => {
      void handleResolveWave()
    }, 700)
    return () => clearInterval(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [autoResolve, waveStatus, commands])

  return (
    <div className="game-page">
      <TopHud
        gameState={gameState}
        connectionStatus={connection.connectionStatus}
        onStartGame={handleStartGame}
        onReconnect={connection.reconnect}
      />

      <div className="game-main">
        <BuildPanel selectedCell={selectedCell} onBuildTower={handleBuildTower} />

        <GameBoard
          laneCount={LANE_COUNT}
          cellCount={CELL_COUNT}
          selectedCell={selectedCell}
          selectedTowerId={selectedTowerId}
          towersByLaneCell={boardTowers}
          enemies={enemies}
          onSelectCell={setSelectedCell}
          onSelectTower={setSelectedTowerId}
        />

        <div className="game-side">
          <UpgradePanel
            selectedTowerId={selectedTowerId}
            selectedTower={towers.find((tower) => tower.id === selectedTowerId) ?? null}
            onUpgrade={handleUpgrade}
          />
          <WaveControls
            waveStatus={waveStatus}
            autoResolve={autoResolve}
            onToggleAutoResolve={setAutoResolve}
            onStartWave={handleStartWave}
            onResolveWave={handleResolveWave}
          />
        </div>
      </div>

      <EventFeed events={events} />
      <ErrorToastStack errors={errors} onDismiss={dismissError} />
    </div>
  )
}


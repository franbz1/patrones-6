import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { GamePage } from '../GamePage'

const mockConnection = vi.fn()
const mockCommands = vi.fn()

vi.mock('../../hooks/useGameConnection', () => ({
  useGameConnection: () => mockConnection(),
}))

vi.mock('../../hooks/useGameCommands', () => ({
  useGameCommands: () => mockCommands(),
}))

vi.mock('../../runtime/smokeFlow', () => ({
  runSmokeFlow: vi.fn(async () => []),
}))

function createDefaultConnection() {
  return {
    gameState: {
      stateVersion: 2,
      player: { coins: 120, baseHealth: 9 },
      towers: [
        { id: 'tower-l0-c0', damage: 15, attacksPerResolve: 1, freezeTurns: 0, durability: 100, upgrades: [] },
      ],
      wave: { status: 'NOT_STARTED', enemies: [] },
    },
    connectionStatus: 'connected',
    lastError: null,
    reconnect: vi.fn(),
    refreshState: vi.fn(async () => {}),
  }
}

function createDefaultCommands() {
  return {
    startGame: vi.fn(async () => ({ ok: true })),
    startWave: vi.fn(async () => ({ ok: true })),
    resolveWave: vi.fn(async () => ({ ok: true })),
    buildTower: vi.fn(async () => ({ ok: true })),
    addUpgrade: vi.fn(async () => ({ ok: true })),
  }
}

describe('GamePage', () => {
  beforeEach(() => {
    mockConnection.mockReturnValue(createDefaultConnection())
    mockCommands.mockReturnValue(createDefaultCommands())
  })

  it('renders connection-driven HUD values', () => {
    render(<GamePage />)

    expect(screen.getByText('Coins: 120')).toBeInTheDocument()
    expect(screen.getByText('Base HP: 9')).toBeInTheDocument()
    expect(screen.getByText('Status: connected')).toBeInTheDocument()
  })

  it('triggers start game command from button', async () => {
    const commands = createDefaultCommands()
    mockCommands.mockReturnValue(commands)

    render(<GamePage />)
    fireEvent.click(screen.getByRole('button', { name: 'Start Game' }))

    await waitFor(() => {
      expect(commands.startGame).toHaveBeenCalledTimes(1)
    })
  })

  it('shows error toast when build command fails', async () => {
    const commands = createDefaultCommands()
    commands.buildTower = vi.fn(async () => ({ ok: false, conflict: false, error: { code: 'insufficient_coins', message: 'Need more coins.' } }))
    mockCommands.mockReturnValue(commands)

    render(<GamePage />)
    fireEvent.click(screen.getByRole('button', { name: 'Build Tower (40)' }))

    expect(await screen.findByText('insufficient_coins')).toBeInTheDocument()
    expect(screen.getByText('Need more coins.')).toBeInTheDocument()
  })

  it('updates selected tower when tower cell is clicked', () => {
    render(<GamePage />)

    fireEvent.click(screen.getByText('tower-l0-c0'))
    expect(screen.getByText('Selected tower: tower-l0-c0')).toBeInTheDocument()
  })
})


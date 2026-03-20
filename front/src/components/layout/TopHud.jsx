import { SvgIcon } from '../common/SvgIcon'

export function TopHud({ gameState, connectionStatus, onStartGame, onReconnect }) {
  const coins = gameState?.player?.coins ?? 0
  const baseHealth = gameState?.player?.baseHealth ?? 0
  const version = gameState?.stateVersion ?? 0

  return (
    <header className="top-hud">
      <div className="hud-block">
        <SvgIcon name="coin" className="icon" />
        <span>Coins: {coins}</span>
      </div>
      <div className="hud-block">
        <SvgIcon name="baseHealth" className="icon" />
        <span>Base HP: {baseHealth}</span>
      </div>
      <div className="hud-block">
        <SvgIcon name={`connection${connectionStatus === 'connected' ? 'Ok' : connectionStatus === 'connecting' ? 'Warn' : 'Off'}`} className="icon" />
        <span>Status: {connectionStatus}</span>
      </div>
      <div className="hud-block">
        <span>Version: v{version}</span>
      </div>
      <div className="hud-actions">
        <button onClick={onStartGame} type="button">Start Game</button>
        <button onClick={onReconnect} type="button">Reconnect</button>
      </div>
    </header>
  )
}


import { SvgIcon } from '../common/SvgIcon'

export function TowerCard({ tower }) {
  if (!tower) {
    return <p>No tower selected.</p>
  }

  return (
    <div className="tower-card">
      <div className="tower-title">
        <SvgIcon name="towerBase" className="icon" />
        <strong>{tower.id}</strong>
      </div>
      <div className="tower-stats">
        <span>Damage: {tower.damage}</span>
        <span>Attacks: {tower.attacksPerResolve}</span>
        <span>Freeze: {tower.freezeTurns}</span>
        <span>Durability: {tower.durability}</span>
      </div>
      <p>Upgrades: {(tower.upgrades || []).join(', ') || 'none'}</p>
    </div>
  )
}


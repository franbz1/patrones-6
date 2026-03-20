import { SvgIcon } from '../common/SvgIcon'
import { TowerCard } from '../entities/TowerCard'

const UPGRADE_ACTIONS = [
  { type: 'RAPID_FIRE', label: 'Rapid Fire', icon: 'upgradeRapid' },
  { type: 'FREEZE_SHOT', label: 'Freeze Shot', icon: 'upgradeFreeze' },
  { type: 'REINFORCED', label: 'Reinforced', icon: 'upgradeReinforced' },
]

export function UpgradePanel({ selectedTowerId, selectedTower, onUpgrade }) {
  return (
    <section className="side-panel">
      <h3>Upgrades</h3>
      <p>Selected tower: {selectedTowerId ?? 'none'}</p>
      <TowerCard tower={selectedTower} />
      <div className="upgrade-buttons">
        {UPGRADE_ACTIONS.map((upgrade) => (
          <button
            key={upgrade.type}
            type="button"
            disabled={!selectedTowerId}
            onClick={() => onUpgrade(upgrade.type)}
          >
            <SvgIcon name={upgrade.icon} className="icon" />
            {upgrade.label}
          </button>
        ))}
      </div>
    </section>
  )
}


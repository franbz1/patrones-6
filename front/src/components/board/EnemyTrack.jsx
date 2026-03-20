import { SvgIcon } from '../common/SvgIcon'

function laneForEnemy(enemyId, laneCount) {
  let hash = 0
  for (let i = 0; i < enemyId.length; i += 1) hash += enemyId.charCodeAt(i)
  return hash % laneCount
}

export function EnemyTrack({ laneIndex, laneCount, enemies }) {
  const activeEnemies = enemies.filter((enemy) => laneForEnemy(enemy.id, laneCount) === laneIndex)
  const maxDistance = Math.max(1, ...enemies.map((enemy) => enemy.distanceToBase || 1))

  return (
    <div className="enemy-track">
      {activeEnemies.map((enemy) => {
        const progress = 100 - Math.min(100, (enemy.distanceToBase / maxDistance) * 100)
        return (
          <div key={enemy.id} className={`enemy ${enemy.status.toLowerCase()}`} style={{ left: `${progress}%` }}>
            <SvgIcon name="enemyBasic" className="enemy-icon" />
            <span className="enemy-hp">{Math.round(enemy.health)}</span>
          </div>
        )
      })}
    </div>
  )
}


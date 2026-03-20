import { BoardCell } from './BoardCell'
import { EnemyTrack } from './EnemyTrack'

export function LaneRow({
  laneIndex,
  cellCount,
  selectedCell,
  selectedTowerId,
  towersByLaneCell,
  laneCount,
  enemies,
  onSelectCell,
  onSelectTower,
}) {
  const cells = Array.from({ length: cellCount }, (_, cell) => cell)

  return (
    <div className="lane-row">
      <EnemyTrack laneIndex={laneIndex} laneCount={laneCount} enemies={enemies} />
      <div className="lane-grid">
        {cells.map((cell) => {
          const tower = towersByLaneCell.get(`${laneIndex}-${cell}`)
          return (
            <BoardCell
              key={`${laneIndex}-${cell}`}
              lane={laneIndex}
              cell={cell}
              tower={tower}
              isSelected={selectedCell.lane === laneIndex && selectedCell.cell === cell}
              isTowerSelected={tower?.id === selectedTowerId}
              onSelectCell={onSelectCell}
              onSelectTower={onSelectTower}
            />
          )
        })}
      </div>
    </div>
  )
}


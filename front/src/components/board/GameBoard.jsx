import { LaneRow } from './LaneRow'

export function GameBoard({
  laneCount,
  cellCount,
  selectedCell,
  selectedTowerId,
  towersByLaneCell,
  enemies,
  onSelectCell,
  onSelectTower,
}) {
  const lanes = Array.from({ length: laneCount }, (_, index) => index)

  return (
    <section className="game-board">
      {lanes.map((laneIndex) => (
        <LaneRow
          key={laneIndex}
          laneIndex={laneIndex}
          laneCount={laneCount}
          cellCount={cellCount}
          selectedCell={selectedCell}
          selectedTowerId={selectedTowerId}
          towersByLaneCell={towersByLaneCell}
          enemies={enemies}
          onSelectCell={onSelectCell}
          onSelectTower={onSelectTower}
        />
      ))}
    </section>
  )
}


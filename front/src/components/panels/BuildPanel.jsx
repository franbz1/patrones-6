export function BuildPanel({ selectedCell, onBuildTower }) {
  return (
    <section className="side-panel">
      <h3>Build</h3>
      <p>Selected lane: {selectedCell.lane + 1}</p>
      <p>Selected cell: {selectedCell.cell + 1}</p>
      <button type="button" onClick={onBuildTower}>Build Tower (40)</button>
    </section>
  )
}


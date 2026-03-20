import { SvgIcon } from '../common/SvgIcon'

export function BoardCell({
  lane,
  cell,
  isSelected,
  tower,
  isTowerSelected,
  onSelectCell,
  onSelectTower,
}) {
  const className = [
    'board-cell',
    isSelected ? 'is-selected' : '',
    tower ? 'has-tower' : '',
    isTowerSelected ? 'tower-selected' : '',
  ].join(' ')

  const handleClick = () => {
    if (tower) {
      onSelectTower(tower.id)
      return
    }
    onSelectCell({ lane, cell })
  }

  return (
    <button className={className} type="button" onClick={handleClick}>
      <SvgIcon name={isSelected ? 'gridCellSelected' : 'gridCellEmpty'} className="cell-bg" />
      {tower && (
        <span className="tower-chip">
          <SvgIcon name="towerBase" className="tower-icon" />
          <span className="tower-id">{tower.id}</span>
        </span>
      )}
    </button>
  )
}


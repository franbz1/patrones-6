export function WaveControls({
  waveStatus,
  autoResolve,
  onToggleAutoResolve,
  onStartWave,
  onResolveWave,
}) {
  return (
    <section className="side-panel">
      <h3>Wave</h3>
      <p>Status: {waveStatus}</p>
      <div className="wave-actions">
        <button type="button" onClick={onStartWave}>Start Wave</button>
        <button type="button" onClick={onResolveWave}>Resolve Step</button>
      </div>
      <label className="auto-resolve">
        <input
          type="checkbox"
          checked={autoResolve}
          onChange={(event) => onToggleAutoResolve(event.target.checked)}
        />
        Auto Resolve
      </label>
    </section>
  )
}


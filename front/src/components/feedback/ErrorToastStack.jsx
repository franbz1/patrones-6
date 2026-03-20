export function ErrorToastStack({ errors, onDismiss }) {
  return (
    <div className="toast-stack">
      {errors.map((error) => (
        <div key={error.id} className="toast">
          <strong>{error.code}</strong>
          <span>{error.message}</span>
          <button type="button" onClick={() => onDismiss(error.id)}>x</button>
        </div>
      ))}
    </div>
  )
}


export function EventFeed({ events }) {
  return (
    <section className="event-feed">
      <h3>Event Feed</h3>
      <ul>
        {events.map((event) => (
          <li key={event.id} className={`event-${event.level}`}>{event.text}</li>
        ))}
      </ul>
    </section>
  )
}


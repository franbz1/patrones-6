import * as icons from '../../assets/svg'

export function SvgIcon({ name, className }) {
  const src = icons[name]
  if (!src) {
    return <span className={className} aria-hidden="true">?</span>
  }
  return <img src={src} className={className} alt="" aria-hidden="true" />
}


import { APP_NAME } from '@/config'
import { cn } from '@/lib/utils'

// Same mark as public/favicon.svg: stacked bars for plan tiers.
export function Logo({ className }: { className?: string }) {
  return (
    <span className={cn('flex items-center gap-2.5 font-semibold tracking-tight', className)}>
      <svg viewBox="0 0 32 32" className="size-7 shrink-0" aria-hidden="true">
        <rect width="32" height="32" rx="8" className="fill-primary" />
        <rect x="8" y="8.5" width="16" height="4" rx="2" fill="#fff" />
        <rect x="8" y="14.5" width="12" height="4" rx="2" fill="#fff" fillOpacity=".8" />
        <rect x="8" y="20.5" width="8" height="4" rx="2" fill="#fff" fillOpacity=".6" />
      </svg>
      {APP_NAME}
    </span>
  )
}

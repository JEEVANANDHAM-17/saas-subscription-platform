import type { ReactNode } from 'react'

export function PageHeader({ title, description, actions, eyebrow }: {
  title: ReactNode
  description?: ReactNode
  actions?: ReactNode
  /** Small line above the title, e.g. a back link. */
  eyebrow?: ReactNode
}) {
  return (
    <div className="mb-6">
      {eyebrow && <div className="mb-3">{eyebrow}</div>}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div className="grid min-w-0 gap-1">
          <h1 className="text-2xl font-semibold tracking-tight">{title}</h1>
          {description && <div className="text-sm text-muted-foreground">{description}</div>}
        </div>
        {actions && <div className="flex shrink-0 gap-2">{actions}</div>}
      </div>
    </div>
  )
}

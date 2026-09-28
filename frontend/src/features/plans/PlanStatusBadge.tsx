import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import type { PlanStatus } from './api'
import { statusLabels } from './labels'

const styles: Record<PlanStatus, { badge: string; dot: string }> = {
  ACTIVE: { badge: 'border-success/25 bg-success/10 text-success', dot: 'bg-success' },
  DRAFT: { badge: 'border-warning/35 bg-warning/12 text-[oklch(0.5_0.12_65)]', dot: 'bg-warning' },
  ARCHIVED: { badge: 'border-border bg-muted text-muted-foreground', dot: 'bg-muted-foreground/60' },
}

export function PlanStatusBadge({ status, className }: { status: PlanStatus; className?: string }) {
  return (
    <Badge className={cn(styles[status].badge, className)}>
      <span className={cn('size-1.5 rounded-full', styles[status].dot)} aria-hidden="true" />
      {statusLabels[status]}
    </Badge>
  )
}

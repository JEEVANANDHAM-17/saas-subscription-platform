import { Layers, Plus, RotateCw, Search } from 'lucide-react'
import { useMemo, type MouseEvent } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router'
import { PageHeader } from '@/components/PageHeader'
import { Alert } from '@/components/ui/alert'
import { Button, buttonVariants } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { APP_NAME } from '@/config'
import { formatDateTime, formatPrice } from '@/lib/format'
import { cn } from '@/lib/utils'
import { PLAN_STATUSES, usePlans, type Plan, type PlanStatus } from './api'
import { intervalLabels, intervalSuffix, statusLabels } from './labels'
import { PlanStatusBadge } from './PlanStatusBadge'

type StatusFilter = PlanStatus | 'ALL'

export function PlansPage() {
  const { data: plans, isPending, error, refetch, isRefetching } = usePlans()
  const navigate = useNavigate()

  // Filters live in the URL (?status=ACTIVE&q=pro) so they survive going to a plan and back.
  const [searchParams, setSearchParams] = useSearchParams()
  const statusParam = searchParams.get('status')
  const status: StatusFilter = PLAN_STATUSES.includes(statusParam as PlanStatus) ? (statusParam as PlanStatus) : 'ALL'
  const query = searchParams.get('q') ?? ''

  const updateParam = (key: string, value: string) =>
    setSearchParams(
      (params) => {
        if (value) params.set(key, value)
        else params.delete(key)
        return params
      },
      { replace: true },
    )

  const counts = useMemo(() => countByStatus(plans ?? []), [plans])

  const visiblePlans = useMemo(() => {
    const needle = query.trim().toLowerCase()
    return (plans ?? [])
      .filter((plan) => status === 'ALL' || plan.plan_status === status)
      .filter((plan) => !needle || `${plan.plan_name} ${plan.plan_code}`.toLowerCase().includes(needle))
      .sort((a, b) => b.plan_id - a.plan_id)
  }, [plans, status, query])

  const openPlan = (event: MouseEvent, plan: Plan) => {
    // The plan name is already a link; let it handle its own clicks.
    if ((event.target as HTMLElement).closest('a')) return
    navigate(`/plans/${plan.plan_id}`)
  }

  return (
    <>
      <title>{`Plans · ${APP_NAME}`}</title>
      <PageHeader
        title="Plans"
        description="The subscription plans you can put customers on."
        actions={
          <Link to="/plans/new" className={buttonVariants()}>
            <Plus />
            New plan
          </Link>
        }
      />

      {error ? (
        <Alert variant="destructive">
          <p>Couldn't load plans. {error.message}</p>
          <Button variant="outline" size="sm" className="mt-2 w-fit" onClick={() => refetch()} disabled={isRefetching}>
            <RotateCw className={cn(isRefetching && 'animate-spin')} />
            Try again
          </Button>
        </Alert>
      ) : (
        <Card className="overflow-hidden">
          <div className="flex flex-col gap-3 border-b p-3 sm:flex-row sm:items-center sm:justify-between">
            <div role="group" aria-label="Filter by status" className="flex flex-wrap gap-1">
              {(['ALL', ...PLAN_STATUSES] as const).map((value) => (
                <button
                  key={value}
                  type="button"
                  aria-pressed={status === value}
                  onClick={() => updateParam('status', value === 'ALL' ? '' : value)}
                  className={cn(
                    'inline-flex h-8 items-center gap-1.5 rounded-md px-3 text-[13px] font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground',
                    status === value && 'bg-accent text-accent-foreground hover:bg-accent hover:text-accent-foreground',
                  )}
                >
                  {value === 'ALL' ? 'All' : statusLabels[value]}
                  <span className="text-xs tabular-nums opacity-70">{plans ? counts[value] : '–'}</span>
                </button>
              ))}
            </div>
            <div className="relative sm:w-64">
              <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="search"
                aria-label="Search plans by name or code"
                placeholder="Search name or code"
                className="pl-9"
                value={query}
                onChange={(event) => updateParam('q', event.target.value)}
              />
            </div>
          </div>

          {isPending ? (
            <PlansTableSkeleton />
          ) : !plans?.length ? (
            <EmptyState />
          ) : visiblePlans.length === 0 ? (
            <div className="px-6 py-14 text-center text-sm text-muted-foreground">
              No plans match these filters.{' '}
              <button type="button" className="font-medium text-primary hover:underline" onClick={() => setSearchParams({}, { replace: true })}>
                Clear filters
              </button>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Plan</TableHead>
                  <TableHead className="text-right">Price</TableHead>
                  <TableHead>Billing</TableHead>
                  <TableHead className="text-right">Trial</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Last updated</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {visiblePlans.map((plan) => (
                  <TableRow key={plan.plan_id} onClick={(event) => openPlan(event, plan)} className="cursor-pointer hover:bg-muted/50">
                    <TableCell className="max-w-80">
                      <Link to={`/plans/${plan.plan_id}`} className="font-medium hover:text-primary hover:underline">
                        {plan.plan_name}
                      </Link>
                      <div className="mt-0.5 truncate font-mono text-xs text-muted-foreground">{plan.plan_code}</div>
                    </TableCell>
                    <TableCell className="text-right whitespace-nowrap tabular-nums">
                      <span className="font-medium">{formatPrice(plan.plan_price)}</span>{' '}
                      <span className="text-xs text-muted-foreground">{intervalSuffix[plan.billing_interval]}</span>
                    </TableCell>
                    <TableCell>{intervalLabels[plan.billing_interval]}</TableCell>
                    <TableCell className="text-right tabular-nums text-muted-foreground">
                      {plan.trial_days ? `${plan.trial_days} days` : '—'}
                    </TableCell>
                    <TableCell>
                      <PlanStatusBadge status={plan.plan_status} />
                    </TableCell>
                    <TableCell className="whitespace-nowrap text-muted-foreground">{formatDateTime(plan.plan_last_updated_time)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </Card>
      )}
    </>
  )
}

function countByStatus(plans: Plan[]): Record<StatusFilter, number> {
  const counts: Record<StatusFilter, number> = { ALL: plans.length, DRAFT: 0, ACTIVE: 0, ARCHIVED: 0 }
  for (const plan of plans) counts[plan.plan_status] += 1
  return counts
}

function EmptyState() {
  return (
    <div className="flex flex-col items-center px-6 py-16 text-center">
      <div className="mb-4 flex size-12 items-center justify-center rounded-full bg-accent text-accent-foreground">
        <Layers className="size-5" />
      </div>
      <h2 className="font-semibold">No plans yet</h2>
      <p className="mt-1 max-w-sm text-sm text-muted-foreground">
        Create a plan with a price and billing interval. Customers can be subscribed to it once it's active.
      </p>
      <Link to="/plans/new" className={cn(buttonVariants(), 'mt-5')}>
        <Plus />
        Create your first plan
      </Link>
    </div>
  )
}

function PlansTableSkeleton() {
  return (
    <div aria-busy="true" aria-label="Loading plans" className="divide-y">
      {Array.from({ length: 4 }, (_, index) => (
        <div key={index} className="flex items-center gap-6 px-4 py-4">
          <div className="grid flex-1 gap-2">
            <Skeleton className="h-4 w-40" />
            <Skeleton className="h-3 w-24" />
          </div>
          <Skeleton className="h-4 w-20" />
          <Skeleton className="hidden h-4 w-16 sm:block" />
          <Skeleton className="h-5 w-16 rounded-full" />
        </div>
      ))}
    </div>
  )
}

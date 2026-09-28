import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2 } from 'lucide-react'
import type { ReactNode } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { Link, useParams } from 'react-router'
import { toast } from 'sonner'
import { PageHeader } from '@/components/PageHeader'
import { Alert } from '@/components/ui/alert'
import { Button, buttonVariants } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { APP_NAME } from '@/config'
import { ApiError } from '@/lib/api'
import { formatDateTime, formatPrice } from '@/lib/format'
import { usePlan, useUpdatePlan, type Plan } from './api'
import { intervalLabels, intervalSuffix } from './labels'
import { BackToPlans } from './BackToPlans'
import { planEditSchema, planToEditValues, PlanDescriptionField, PlanNameField, PlanPriceField, PlanStatusField } from './plan-form'
import { PlanStatusBadge } from './PlanStatusBadge'

export function PlanDetailPage() {
  const planId = Number(useParams().planId)
  const validId = Number.isInteger(planId) && planId > 0

  if (!validId) return <PlanNotFound />
  return <PlanDetail planId={planId} />
}

function PlanDetail({ planId }: { planId: number }) {
  const { data: plan, error } = usePlan(planId)

  if (error instanceof ApiError && error.status === 404) return <PlanNotFound />
  if (error) {
    return (
      <>
        <PageHeader eyebrow={<BackToPlans />} title="Plan" />
        <Alert variant="destructive">Couldn't load this plan. {error.message}</Alert>
      </>
    )
  }
  if (!plan) return <PlanDetailSkeleton />

  return (
    <>
      <title>{`${plan.plan_name} · Plans · ${APP_NAME}`}</title>
      <PageHeader
        eyebrow={<BackToPlans />}
        title={
          <span className="flex flex-wrap items-center gap-3">
            {plan.plan_name}
            <PlanStatusBadge status={plan.plan_status} />
          </span>
        }
        description={
          <span className="tabular-nums">
            <span className="font-mono">{plan.plan_code}</span> · {formatPrice(plan.plan_price)} {intervalSuffix[plan.billing_interval]}
          </span>
        }
      />

      <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
        <PlanEditForm key={plan.plan_id} plan={plan} />
        <PlanFacts plan={plan} />
      </div>
    </>
  )
}

function PlanEditForm({ plan }: { plan: Plan }) {
  const updatePlan = useUpdatePlan(plan.plan_id)
  const form = useForm({
    resolver: zodResolver(planEditSchema),
    defaultValues: planToEditValues(plan),
  })

  // Sends every editable field; an empty description clears it.
  const onSubmit = form.handleSubmit((values) =>
    updatePlan.mutate(values, {
      onSuccess: (updated) => {
        form.reset(planToEditValues(updated))
        toast.success('Plan saved')
      },
    }),
  )

  const { isDirty } = form.formState

  return (
    <FormProvider {...form}>
      <form noValidate onSubmit={onSubmit}>
        <Card>
          <CardHeader>
            <CardTitle>Edit plan</CardTitle>
            <CardDescription>Changes apply as soon as you save.</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-5 sm:grid-cols-2">
            {updatePlan.error && (
              <Alert variant="destructive" className="sm:col-span-2">
                Couldn't save the plan. {updatePlan.error.message}
              </Alert>
            )}
            <PlanNameField />
            <PlanPriceField />
            <PlanDescriptionField className="sm:col-span-2" />
            <PlanStatusField />
          </CardContent>
          <CardFooter className="justify-end">
            <Button variant="outline" disabled={!isDirty || updatePlan.isPending} onClick={() => form.reset()}>
              Discard changes
            </Button>
            <Button type="submit" disabled={!isDirty || updatePlan.isPending}>
              {updatePlan.isPending && <Loader2 className="animate-spin" />}
              Save changes
            </Button>
          </CardFooter>
        </Card>
      </form>
    </FormProvider>
  )
}

function PlanFacts({ plan }: { plan: Plan }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Details</CardTitle>
      </CardHeader>
      <CardContent>
        <dl className="grid gap-4 text-sm">
          <Fact label="Plan ID">{plan.plan_id}</Fact>
          <Fact label="Code">
            <span className="font-mono">{plan.plan_code}</span>
          </Fact>
          <Fact label="Billing interval">{intervalLabels[plan.billing_interval]}</Fact>
          <Fact label="Trial">{plan.trial_days ? `${plan.trial_days} days` : 'No trial'}</Fact>
          <Fact label="Created">{formatDateTime(plan.plan_created_time)}</Fact>
          <Fact label="Last updated">{formatDateTime(plan.plan_last_updated_time)}</Fact>
        </dl>
        <p className="mt-5 border-t pt-4 text-[13px] text-muted-foreground">
          Code and billing interval are fixed once a plan exists.
        </p>
      </CardContent>
    </Card>
  )
}

function Fact({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="flex items-baseline justify-between gap-4">
      <dt className="text-muted-foreground">{label}</dt>
      <dd className="text-right font-medium tabular-nums">{children}</dd>
    </div>
  )
}

function PlanNotFound() {
  return (
    <>
      <title>{`Plan not found · ${APP_NAME}`}</title>
      <Card className="mx-auto mt-10 max-w-md text-center">
        <CardContent className="py-10">
          <h1 className="text-lg font-semibold">Plan not found</h1>
          <p className="mt-1 text-sm text-muted-foreground">It may have been removed, or the link is wrong.</p>
          <Link to="/plans" className={buttonVariants({ variant: 'outline', className: 'mt-5' })}>
            Back to plans
          </Link>
        </CardContent>
      </Card>
    </>
  )
}

function PlanDetailSkeleton() {
  return (
    <div aria-busy="true" aria-label="Loading plan">
      <Skeleton className="mb-4 h-4 w-20" />
      <Skeleton className="mb-2 h-8 w-64" />
      <Skeleton className="mb-6 h-4 w-48" />
      <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
        <Skeleton className="h-80 rounded-xl" />
        <Skeleton className="h-64 rounded-xl" />
      </div>
    </div>
  )
}

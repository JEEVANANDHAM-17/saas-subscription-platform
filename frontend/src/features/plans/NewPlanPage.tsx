import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2 } from 'lucide-react'
import { FormProvider, useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router'
import { toast } from 'sonner'
import { PageHeader } from '@/components/PageHeader'
import { Alert } from '@/components/ui/alert'
import { Button, buttonVariants } from '@/components/ui/button'
import { Card, CardContent, CardFooter } from '@/components/ui/card'
import { Field } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { NativeSelect } from '@/components/ui/native-select'
import { APP_NAME } from '@/config'
import { BILLING_INTERVALS, useCreatePlan, usePlans } from './api'
import { BackToPlans } from './BackToPlans'
import { intervalLabels } from './labels'
import {
  planCreateSchema,
  PlanDescriptionField,
  PlanNameField,
  PlanPriceField,
  PlanStatusField,
  type PlanCreateInput,
  type PlanCreateOutput,
} from './plan-form'

export function NewPlanPage() {
  const navigate = useNavigate()
  const { data: existingPlans } = usePlans()
  const createPlan = useCreatePlan()

  const form = useForm<PlanCreateInput, unknown, PlanCreateOutput>({
    resolver: zodResolver(planCreateSchema),
    defaultValues: {
      plan_code: '',
      plan_name: '',
      plan_description: '',
      plan_price: '',
      billing_interval: 'MONTHLY',
      plan_status: 'DRAFT',
    },
  })
  const { errors } = form.formState

  const onSubmit = form.handleSubmit((values) => {
    // plan_code is unique in the database (case-insensitive in MySQL). Catch it here for a clear message.
    const code = values.plan_code.toLowerCase()
    if (existingPlans?.some((plan) => plan.plan_code.toLowerCase() === code)) {
      form.setError('plan_code', { message: 'A plan with this code already exists' }, { shouldFocus: true })
      return
    }

    createPlan.mutate(
      { ...values, plan_description: values.plan_description || undefined },
      {
        onSuccess: (plan) => {
          toast.success(`Plan "${plan.plan_name}" created`)
          navigate(`/plans/${plan.plan_id}`)
        },
      },
    )
  })

  return (
    <>
      <title>{`New plan · ${APP_NAME}`}</title>
      <PageHeader
        eyebrow={<BackToPlans />}
        title="New plan"
        description="Code and billing interval can't be changed after the plan is created."
      />

      <FormProvider {...form}>
        <form noValidate onSubmit={onSubmit} className="max-w-3xl">
          <Card>
            <CardContent className="grid gap-5 sm:grid-cols-2">
              {createPlan.error && (
                <Alert variant="destructive" className="sm:col-span-2">
                  Couldn't create the plan. {createPlan.error.message}
                </Alert>
              )}

              <Field
                label="Plan code"
                htmlFor="plan_code"
                required
                hint="A unique ID, e.g. PRO_MONTHLY."
                error={errors.plan_code?.message}
              >
                <Input
                  id="plan_code"
                  placeholder="PRO_MONTHLY"
                  autoFocus
                  className="font-mono"
                  aria-invalid={!!errors.plan_code}
                  aria-describedby="plan_code-message"
                  {...form.register('plan_code')}
                />
              </Field>
              <PlanNameField />
              <PlanDescriptionField className="sm:col-span-2" />
              <PlanPriceField />
              <Field label="Billing interval" htmlFor="billing_interval" required>
                <NativeSelect id="billing_interval" {...form.register('billing_interval')}>
                  {BILLING_INTERVALS.map((value) => (
                    <option key={value} value={value}>
                      {intervalLabels[value]}
                    </option>
                  ))}
                </NativeSelect>
              </Field>
              <PlanStatusField />
            </CardContent>
            <CardFooter className="justify-end">
              <Link to="/plans" className={buttonVariants({ variant: 'outline' })}>
                Cancel
              </Link>
              <Button type="submit" disabled={createPlan.isPending}>
                {createPlan.isPending && <Loader2 className="animate-spin" />}
                Create plan
              </Button>
            </CardFooter>
          </Card>
        </form>
      </FormProvider>
    </>
  )
}

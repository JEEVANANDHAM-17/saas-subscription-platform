import { useFormContext } from 'react-hook-form'
import { z } from 'zod'
import { Field } from '@/components/ui/field'
import { Input, Textarea } from '@/components/ui/input'
import { NativeSelect } from '@/components/ui/native-select'
import { BILLING_INTERVALS, PLAN_STATUSES, type Plan } from './api'
import { statusDescriptions, statusLabels } from './labels'

// Limits follow the PlansTable columns (code 50, name 100, description 500, DECIMAL(19,4) price).

const priceSchema = z
  .string()
  .trim()
  .min(1, 'Enter a price')
  .regex(/^\d{1,15}(\.\d{1,4})?$/, 'Enter a number like 499 or 12.50 (up to 4 decimals)')
  .transform(Number)

/** Fields that can be set on create and changed later. */
export const planEditSchema = z.object({
  plan_name: z.string().trim().min(1, 'Enter a plan name').max(100, 'Keep it under 100 characters'),
  plan_description: z.string().trim().max(500, 'Keep it under 500 characters'),
  plan_price: priceSchema,
  plan_status: z.enum(PLAN_STATUSES),
})

export const planCreateSchema = planEditSchema.extend({
  plan_code: z
    .string()
    .trim()
    .min(1, 'Enter a plan code')
    .max(50, 'Keep it under 50 characters')
    .regex(/^[A-Za-z0-9_-]+$/, 'Use letters, numbers, dashes and underscores only'),
  billing_interval: z.enum(BILLING_INTERVALS),
})

export type PlanEditInput = z.input<typeof planEditSchema>
export type PlanCreateInput = z.input<typeof planCreateSchema>
export type PlanCreateOutput = z.output<typeof planCreateSchema>

export function planToEditValues(plan: Plan): PlanEditInput {
  return {
    plan_name: plan.plan_name,
    plan_description: plan.plan_description ?? '',
    plan_price: String(plan.plan_price),
    plan_status: plan.plan_status,
  }
}

// The fields below are shared by the create and edit forms and must be rendered inside a <FormProvider>.

export function PlanNameField() {
  const { register, formState: { errors } } = useFormContext<PlanEditInput>()
  return (
    <Field label="Plan name" htmlFor="plan_name" required error={errors.plan_name?.message}>
      <Input
        id="plan_name"
        placeholder="Pro"
        aria-invalid={!!errors.plan_name}
        aria-describedby="plan_name-message"
        {...register('plan_name')}
      />
    </Field>
  )
}

export function PlanDescriptionField({ className }: { className?: string }) {
  const { register, formState: { errors } } = useFormContext<PlanEditInput>()
  return (
    <Field
      label="Description"
      htmlFor="plan_description"
      hint="Optional. Up to 500 characters."
      error={errors.plan_description?.message}
      className={className}
    >
      <Textarea
        id="plan_description"
        rows={3}
        placeholder="What customers get on this plan"
        aria-invalid={!!errors.plan_description}
        aria-describedby="plan_description-message"
        {...register('plan_description')}
      />
    </Field>
  )
}

export function PlanPriceField() {
  const { register, formState: { errors } } = useFormContext<PlanEditInput>()
  return (
    <Field label="Price" htmlFor="plan_price" required error={errors.plan_price?.message}>
      <Input
        id="plan_price"
        inputMode="decimal"
        placeholder="499.00"
        className="tabular-nums"
        aria-invalid={!!errors.plan_price}
        aria-describedby="plan_price-message"
        {...register('plan_price')}
      />
    </Field>
  )
}

export function PlanStatusField() {
  const { register, watch } = useFormContext<PlanEditInput>()
  const status = watch('plan_status')
  return (
    <Field label="Status" htmlFor="plan_status" required hint={statusDescriptions[status]}>
      <NativeSelect id="plan_status" aria-describedby="plan_status-message" {...register('plan_status')}>
        {PLAN_STATUSES.map((value) => (
          <option key={value} value={value}>
            {statusLabels[value]}
          </option>
        ))}
      </NativeSelect>
    </Field>
  )
}

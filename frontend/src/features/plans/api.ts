import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '@/lib/api'

// Mirrors PlansTable.BillingIntervalEnum / PlanStatusEnum and the snake_case plan JSON.

export const BILLING_INTERVALS = ['MONTHLY', 'YEARLY'] as const
export type BillingInterval = (typeof BILLING_INTERVALS)[number]

export const PLAN_STATUSES = ['DRAFT', 'ACTIVE', 'ARCHIVED'] as const
export type PlanStatus = (typeof PLAN_STATUSES)[number]

export interface Plan {
  plan_id: number
  plan_code: string
  plan_name: string
  plan_description: string | null
  plan_price: number
  billing_interval: BillingInterval
  trial_days: number
  plan_status: PlanStatus
  plan_created_time: string
  plan_last_updated_time: string
}

export interface PlanCreateRequest {
  plan_code: string
  plan_name: string
  plan_description?: string
  plan_price: number
  billing_interval: BillingInterval
  plan_status: PlanStatus
}

/** PUT /plan/{id} ignores missing fields. Code and billing interval can't be changed. */
export interface PlanUpdateRequest {
  plan_name?: string
  plan_description?: string
  plan_price?: number
  plan_status?: PlanStatus
}

export const planKeys = {
  list: ['plans'] as const,
  detail: (id: number) => ['plans', id] as const,
}

export function usePlans() {
  return useQuery({
    queryKey: planKeys.list,
    queryFn: () => api<Plan[]>('/plan'),
  })
}

export function usePlan(id: number) {
  return useQuery({
    queryKey: planKeys.detail(id),
    queryFn: () => api<Plan>(`/plan/${id}`),
  })
}

export function useCreatePlan() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (body: PlanCreateRequest) => api<Plan>('/plan', { method: 'POST', body }),
    onSuccess: (plan) => {
      queryClient.setQueryData(planKeys.detail(plan.plan_id), plan)
      return queryClient.invalidateQueries({ queryKey: planKeys.list, exact: true })
    },
  })
}

export function useUpdatePlan(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (body: PlanUpdateRequest) => api<Plan>(`/plan/${id}`, { method: 'PUT', body }),
    onSuccess: (plan) => {
      queryClient.setQueryData(planKeys.detail(id), plan)
      return queryClient.invalidateQueries({ queryKey: planKeys.list, exact: true })
    },
  })
}

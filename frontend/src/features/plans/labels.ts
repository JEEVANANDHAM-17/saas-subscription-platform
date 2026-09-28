import type { BillingInterval, PlanStatus } from './api'

export const statusLabels: Record<PlanStatus, string> = {
  DRAFT: 'Draft',
  ACTIVE: 'Active',
  ARCHIVED: 'Archived',
}

export const statusDescriptions: Record<PlanStatus, string> = {
  DRAFT: 'Being set up. Not offered to customers yet.',
  ACTIVE: 'Available for new subscriptions.',
  ARCHIVED: 'Kept for history. Not offered to customers.',
}

export const intervalLabels: Record<BillingInterval, string> = {
  MONTHLY: 'Monthly',
  YEARLY: 'Yearly',
}

export const intervalSuffix: Record<BillingInterval, string> = {
  MONTHLY: '/ month',
  YEARLY: '/ year',
}

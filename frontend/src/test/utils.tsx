import { render } from '@testing-library/react'
import { MemoryRouter, useLocation, useRoutes, type Location } from 'react-router'
import { AppProviders, createQueryClient } from '@/AppProviders'
import type { Plan } from '@/features/plans/api'
import { routes } from '@/routes'

/**
 * Renders the app's routes at `path`. Uses MemoryRouter rather than a data router: data routers
 * build fetch Requests with jsdom's AbortSignal, which Node's Request rejects.
 */
export function renderApp(path: string) {
  let current: Location | undefined
  function Routes() {
    current = useLocation()
    return useRoutes(routes)
  }

  const queryClient = createQueryClient()
  queryClient.setDefaultOptions({ queries: { retry: false } })
  render(
    <AppProviders queryClient={queryClient}>
      <MemoryRouter initialEntries={[path]}>
        <Routes />
      </MemoryRouter>
    </AppProviders>,
  )
  return {
    /** The current path and query string, e.g. "/plans?status=ACTIVE". */
    currentPath: () => (current ? current.pathname + current.search : ''),
  }
}

/** An unsigned JWT with the claims the backend puts in its tokens. */
export function fakeToken({ expiresInSeconds = 3600, email = 'staff@example.com', name = 'Asha' } = {}) {
  const encode = (value: object) => btoa(JSON.stringify(value)).replace(/=+$/, '').replace(/\+/g, '-').replace(/\//g, '_')
  const exp = Math.floor(Date.now() / 1000) + expiresInSeconds
  return `${encode({ alg: 'HS256', typ: 'JWT' })}.${encode({ sub: '1', email, given_name: name, exp })}.signature`
}

export function logInAs(token = fakeToken()) {
  localStorage.setItem('subscription.access_token', token)
}

export function makePlan(overrides: Partial<Plan> = {}): Plan {
  return {
    plan_id: 1,
    plan_code: 'PRO_MONTHLY',
    plan_name: 'Pro',
    plan_description: 'Everything in Starter, plus more',
    plan_price: 49.99,
    billing_interval: 'MONTHLY',
    trial_days: 0,
    plan_status: 'ACTIVE',
    plan_created_time: '2026-09-28T12:43:22.797449',
    plan_last_updated_time: '2026-09-28T12:43:22.79845',
    ...overrides,
  }
}

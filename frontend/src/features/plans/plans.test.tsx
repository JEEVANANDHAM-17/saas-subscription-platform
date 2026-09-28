import { screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { server } from '@/test/server'
import { logInAs, makePlan, renderApp } from '@/test/utils'

const pro = makePlan()
const starter = makePlan({ plan_id: 2, plan_code: 'STARTER', plan_name: 'Starter', plan_price: 9, plan_status: 'DRAFT' })

describe('plans', () => {
  beforeEach(() => {
    logInAs()
    server.use(http.get('/api/plan', () => HttpResponse.json([pro, starter])))
  })

  it('lists plans and filters them by status', async () => {
    renderApp('/plans')
    const user = userEvent.setup()

    expect(await screen.findByRole('link', { name: 'Pro' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Starter' })).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /Draft/ }))

    expect(screen.queryByRole('link', { name: 'Pro' })).not.toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Starter' })).toBeInTheDocument()
  })

  it('validates the new plan form before sending anything', async () => {
    let posted = false
    server.use(
      http.post('/api/plan', () => {
        posted = true
        return HttpResponse.json(pro)
      }),
    )
    renderApp('/plans/new')
    const user = userEvent.setup()

    await user.click(await screen.findByRole('button', { name: 'Create plan' }))

    expect(await screen.findByText('Enter a plan code')).toBeInTheDocument()
    expect(screen.getByText('Enter a plan name')).toBeInTheDocument()
    expect(screen.getByText('Enter a price')).toBeInTheDocument()

    // plan_code is unique regardless of case in MySQL.
    await user.type(screen.getByLabelText(/Plan code/), 'pro_monthly')
    await user.type(screen.getByLabelText(/Plan name/), 'Another Pro')
    await user.type(screen.getByLabelText(/Price/), '12.5')
    await user.click(screen.getByRole('button', { name: 'Create plan' }))

    expect(await screen.findByText('A plan with this code already exists')).toBeInTheDocument()
    expect(posted).toBe(false)
  })

  it('creates a plan with the backend field names and opens it', async () => {
    let createBody: unknown
    const created = makePlan({ plan_id: 3, plan_code: 'TEAM_YEARLY', plan_name: 'Team', plan_price: 1200, billing_interval: 'YEARLY' })
    server.use(
      http.post('/api/plan', async ({ request }) => {
        createBody = await request.json()
        return HttpResponse.json(created)
      }),
      http.get('/api/plan/3', () => HttpResponse.json(created)),
    )
    const app = renderApp('/plans/new')
    const user = userEvent.setup()

    await user.type(await screen.findByLabelText(/Plan code/), 'TEAM_YEARLY')
    await user.type(screen.getByLabelText(/Plan name/), 'Team')
    await user.type(screen.getByLabelText(/Price/), '1200')
    await user.selectOptions(screen.getByLabelText(/Billing interval/), 'YEARLY')
    await user.selectOptions(screen.getByLabelText(/Status/), 'ACTIVE')
    await user.click(screen.getByRole('button', { name: 'Create plan' }))

    expect(await screen.findByRole('heading', { name: /Team/ })).toBeInTheDocument()
    expect(app.currentPath()).toBe('/plans/3')
    expect(createBody).toEqual({
      plan_code: 'TEAM_YEARLY',
      plan_name: 'Team',
      plan_price: 1200,
      billing_interval: 'YEARLY',
      plan_status: 'ACTIVE',
    })
  })

  it('saves edits to an existing plan', async () => {
    let updateBody: unknown
    server.use(
      http.get('/api/plan/1', () => HttpResponse.json(pro)),
      http.put('/api/plan/1', async ({ request }) => {
        updateBody = await request.json()
        return HttpResponse.json({ ...pro, plan_price: 59, plan_status: 'ARCHIVED' })
      }),
    )
    renderApp('/plans/1')
    const user = userEvent.setup()

    const save = await screen.findByRole('button', { name: 'Save changes' })
    expect(save).toBeDisabled()

    const price = screen.getByLabelText(/Price/)
    await user.clear(price)
    await user.type(price, '59')
    await user.selectOptions(screen.getByLabelText(/Status/), 'ARCHIVED')
    await user.click(save)

    expect(await screen.findByText('Plan saved')).toBeInTheDocument()
    expect(updateBody).toEqual({
      plan_name: 'Pro',
      plan_description: 'Everything in Starter, plus more',
      plan_price: 59,
      plan_status: 'ARCHIVED',
    })
    expect(within(screen.getByRole('heading', { level: 1 })).getByText('Archived')).toBeInTheDocument()
  })

  it('shows "not found" for a plan that does not exist', async () => {
    server.use(http.get('/api/plan/99', () => new HttpResponse(null, { status: 404 })))
    renderApp('/plans/99')

    expect(await screen.findByRole('heading', { name: 'Plan not found' })).toBeInTheDocument()
  })
})

import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/server'
import { fakeToken, logInAs, makePlan, renderApp } from '@/test/utils'

describe('auth', () => {
  it('sends visitors who are not logged in to the login page', async () => {
    const app = renderApp('/plans')

    expect(await screen.findByRole('heading', { name: 'Log in' })).toBeInTheDocument()
    expect(app.currentPath()).toBe('/login')
  })

  it('treats an expired stored token as logged out', async () => {
    logInAs(fakeToken({ expiresInSeconds: -60 }))
    renderApp('/plans')

    expect(await screen.findByRole('heading', { name: 'Log in' })).toBeInTheDocument()
    expect(localStorage.getItem('subscription.access_token')).toBeNull()
  })

  it('shows one message for a wrong email or password', async () => {
    server.use(http.post('/api/login', () => new HttpResponse(null, { status: 401 })))
    renderApp('/login')
    const user = userEvent.setup()

    await user.type(await screen.findByLabelText('Email'), 'staff@example.com')
    await user.type(screen.getByLabelText('Password'), 'wrong')
    await user.click(screen.getByRole('button', { name: 'Log in' }))

    expect(await screen.findByText('Invalid email or password.')).toBeInTheDocument()
    expect(localStorage.getItem('subscription.access_token')).toBeNull()
  })

  it('logs in with snake_case fields, stores the token and opens the page asked for', async () => {
    const token = fakeToken({ name: 'Asha' })
    let loginBody: unknown
    server.use(
      http.post('/api/login', async ({ request }) => {
        loginBody = await request.json()
        return HttpResponse.json({ message: 'Login successful', access_token: token, token_type: 'Bearer', expires_in: 3600 })
      }),
      http.get('/api/plan', ({ request }) =>
        request.headers.get('Authorization') === `Bearer ${token}`
          ? HttpResponse.json([makePlan()])
          : new HttpResponse(null, { status: 401 }),
      ),
    )
    const app = renderApp('/plans?status=ACTIVE')
    const user = userEvent.setup()

    await user.type(await screen.findByLabelText('Email'), 'staff@example.com')
    await user.type(screen.getByLabelText('Password'), 'secret-password')
    await user.click(screen.getByRole('button', { name: 'Log in' }))

    expect(await screen.findByRole('link', { name: 'Pro' })).toBeInTheDocument()
    expect(loginBody).toEqual({ user_email: 'staff@example.com', user_password: 'secret-password' })
    expect(localStorage.getItem('subscription.access_token')).toBe(token)
    expect(app.currentPath()).toBe('/plans?status=ACTIVE')
    expect(screen.getAllByText('Asha')[0]).toBeInTheDocument()
  })

  it('after signup, goes to the login page with the email filled in instead of logging in', async () => {
    let signupBody: unknown
    server.use(
      http.post('/api/signup', async ({ request }) => {
        signupBody = await request.json()
        return HttpResponse.json({})
      }),
    )
    const app = renderApp('/signup')
    const user = userEvent.setup()

    await user.type(await screen.findByLabelText(/First name/), 'Asha')
    await user.type(screen.getByLabelText(/Email/), 'asha@example.com')
    await user.type(screen.getByLabelText(/^Password/), 'long-enough')
    await user.type(screen.getByLabelText(/Confirm password/), 'long-enough')
    await user.click(screen.getByRole('button', { name: 'Create account' }))

    expect(await screen.findByText('Account created. Log in to continue.')).toBeInTheDocument()
    expect(app.currentPath()).toBe('/login')
    expect(screen.getByLabelText('Email')).toHaveValue('asha@example.com')
    expect(signupBody).toEqual({ first_name: 'Asha', user_email: 'asha@example.com', user_password: 'long-enough' })
    expect(localStorage.getItem('subscription.access_token')).toBeNull()
  })

  it('explains when the email is already registered', async () => {
    server.use(http.post('/api/signup', () => new HttpResponse(null, { status: 409 })))
    renderApp('/signup')
    const user = userEvent.setup()

    await user.type(await screen.findByLabelText(/First name/), 'Asha')
    await user.type(screen.getByLabelText(/Email/), 'asha@example.com')
    await user.type(screen.getByLabelText(/^Password/), 'long-enough')
    await user.type(screen.getByLabelText(/Confirm password/), 'long-enough')
    await user.click(screen.getByRole('button', { name: 'Create account' }))

    expect(await screen.findByText(/An account with this email already exists/)).toBeInTheDocument()
  })

  it('ends the session when the backend rejects the token', async () => {
    logInAs()
    server.use(http.get('/api/plan', () => new HttpResponse(null, { status: 401 })))
    const app = renderApp('/plans')

    expect(await screen.findByText('Your session expired. Log in again to continue.')).toBeInTheDocument()
    await waitFor(() => expect(app.currentPath()).toBe('/login'))
    expect(localStorage.getItem('subscription.access_token')).toBeNull()
  })
})

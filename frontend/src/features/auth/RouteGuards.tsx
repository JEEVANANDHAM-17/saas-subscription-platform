import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from './auth-context'

export interface LoginLocationState {
  /** Where to go after logging in. */
  from?: string
  /** Set by the signup page so the login page can confirm the account and prefill the email. */
  signedUpEmail?: string
}

/** Pages that need a logged-in user. Others go to /login and come back afterwards. */
export function RequireAuth() {
  const { session } = useAuth()
  const location = useLocation()

  if (!session) {
    const state: LoginLocationState = { from: location.pathname + location.search }
    return <Navigate to="/login" replace state={state} />
  }
  return <Outlet />
}

/** Login and signup. A logged-in user is sent on to the app. */
export function GuestOnly() {
  const { session } = useAuth()
  const location = useLocation()

  if (session) {
    const from = (location.state as LoginLocationState | null)?.from
    return <Navigate to={from ?? '/plans'} replace />
  }
  return <Outlet />
}

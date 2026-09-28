import { useQueryClient } from '@tanstack/react-query'
import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { setUnauthorizedHandler } from '@/lib/api'
import { clearSession, readSession, saveSession, type Session } from '@/lib/session'
import { AuthContext, type AuthValue } from './auth-context'

// setTimeout can't wait longer than this; longer-lived tokens rely on the 401 handler instead.
const MAX_TIMER_MS = 2 ** 31 - 1

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient()
  const [session, setSession] = useState<Session | null>(() => readSession())
  const [expired, setExpired] = useState(false)

  const endSession = useCallback(
    (becauseExpired: boolean) => {
      clearSession()
      queryClient.clear()
      setSession(null)
      setExpired(becauseExpired)
    },
    [queryClient],
  )

  const login = useCallback((accessToken: string) => {
    const next = saveSession(accessToken)
    setSession(next)
    setExpired(false)
    return next !== null
  }, [])

  const logout = useCallback(() => endSession(false), [endSession])

  // The token lives for a fixed hour (app.jwt.access-token-ttl); end the session when it runs out.
  useEffect(() => {
    if (!session) return
    const remaining = session.expiresAt - Date.now()
    if (remaining > MAX_TIMER_MS) return
    const timer = window.setTimeout(() => endSession(true), Math.max(remaining, 0))
    return () => window.clearTimeout(timer)
  }, [session, endSession])

  useEffect(() => {
    setUnauthorizedHandler(() => endSession(true))
    return () => setUnauthorizedHandler(() => {})
  }, [endSession])

  const value = useMemo<AuthValue>(() => ({ session, expired, login, logout }), [session, expired, login, logout])

  return <AuthContext value={value}>{children}</AuthContext>
}

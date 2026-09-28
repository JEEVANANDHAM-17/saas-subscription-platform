import { createContext, useContext } from 'react'
import type { Session } from '@/lib/session'

export interface AuthValue {
  session: Session | null
  /** True when the last session ended because the token expired or the backend rejected it. */
  expired: boolean
  /** Stores the access token from POST /login. Returns false if the token can't be read. */
  login: (accessToken: string) => boolean
  logout: () => void
}

export const AuthContext = createContext<AuthValue | null>(null)

export function useAuth() {
  const value = useContext(AuthContext)
  if (!value) throw new Error('useAuth must be used inside <AuthProvider>')
  return value
}

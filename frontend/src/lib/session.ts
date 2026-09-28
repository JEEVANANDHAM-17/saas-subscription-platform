const TOKEN_KEY = 'subscription.access_token'

export interface Session {
  token: string
  email: string
  name: string | null
  /** Epoch milliseconds, from the token's `exp` claim. */
  expiresAt: number
}

interface TokenClaims {
  exp?: number
  email?: string
  given_name?: string
}

// Reads the JWT payload for display and expiry only. The backend verifies the signature.
export function sessionFromToken(token: string): Session | null {
  const payload = token.split('.')[1]
  if (!payload) return null

  try {
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const bytes = Uint8Array.from(atob(base64), (char) => char.charCodeAt(0))
    const claims = JSON.parse(new TextDecoder().decode(bytes)) as TokenClaims
    if (typeof claims.exp !== 'number') return null

    return {
      token,
      email: claims.email ?? '',
      name: claims.given_name ?? null,
      expiresAt: claims.exp * 1000,
    }
  } catch {
    return null
  }
}

/** The stored session, or null when there is none or its token has expired. */
export function readSession(): Session | null {
  const token = storage(() => localStorage.getItem(TOKEN_KEY))
  if (!token) return null

  const session = sessionFromToken(token)
  if (!session || session.expiresAt <= Date.now()) {
    clearSession()
    return null
  }
  return session
}

export function saveSession(token: string): Session | null {
  const session = sessionFromToken(token)
  if (session) storage(() => localStorage.setItem(TOKEN_KEY, token))
  return session
}

export function clearSession() {
  storage(() => localStorage.removeItem(TOKEN_KEY))
}

// localStorage can throw (private mode, blocked storage); treat that as "no session".
function storage<T>(action: () => T): T | null {
  try {
    return action()
  } catch {
    return null
  }
}

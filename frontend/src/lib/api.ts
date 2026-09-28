import { readSession } from '@/lib/session'

// Vite proxies /api/* to the Spring Boot backend (see vite.config.ts).
const API_BASE = `${window.location.origin}/api`

export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

let onUnauthorized = () => {}

/** Called when an authenticated request gets 401, i.e. the token expired or was rejected. */
export function setUnauthorizedHandler(handler: () => void) {
  onUnauthorized = handler
}

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT'
  body?: unknown
  /** Send the Bearer token. False for /login and /signup. */
  auth?: boolean
}

export async function api<T>(path: string, { method = 'GET', body, auth = true }: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  const token = auth ? readSession()?.token : undefined
  if (token) headers.Authorization = `Bearer ${token}`

  let response: Response
  try {
    response = await fetch(`${API_BASE}${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new ApiError(0, 'Could not reach the server. Check that the backend is running.')
  }

  if (!response.ok) {
    if (response.status === 401 && auth) onUnauthorized()
    throw new ApiError(response.status, await errorMessage(response))
  }

  const text = await response.text()
  return (text ? JSON.parse(text) : undefined) as T
}

// Spring Boot hides exception messages by default, so fall back to a message per status code.
async function errorMessage(response: Response) {
  try {
    const body = (await response.json()) as { message?: unknown }
    if (typeof body.message === 'string' && body.message.trim()) return body.message
  } catch {
    // Not JSON; use the fallback below.
  }

  switch (response.status) {
    case 400:
      return 'Some fields are invalid. Check the form and try again.'
    case 401:
      return 'Your session has expired. Log in again.'
    case 403:
      return "You don't have permission to do that."
    case 404:
      return 'Not found.'
    case 409:
      return 'That conflicts with existing data.'
    default:
      return 'Something went wrong on the server. Try again.'
  }
}

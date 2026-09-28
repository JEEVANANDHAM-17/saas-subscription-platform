import { z } from 'zod'
import { api } from '@/lib/api'

// Field names match the backend JSON (spring.jackson.property-naming-strategy=SNAKE_CASE).

export interface LoginRequest {
  user_email: string
  user_password: string
}

export interface LoginResponse {
  message: string
  access_token: string
  token_type: string
  /** Seconds. */
  expires_in: number
}

export interface SignupRequest {
  user_email: string
  user_password: string
  first_name: string
  last_name?: string
}

export function login(body: LoginRequest) {
  return api<LoginResponse>('/login', { method: 'POST', body, auth: false })
}

// The response body isn't used: after signing up, the user logs in on the login page.
export function signup(body: SignupRequest) {
  return api<unknown>('/signup', { method: 'POST', body, auth: false })
}

export const emailSchema = z
  .string()
  .trim()
  .min(1, 'Enter your email')
  .max(255, 'Keep it under 255 characters')
  .pipe(z.email('Enter a valid email address'))

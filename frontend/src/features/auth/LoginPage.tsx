import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { Loader2 } from 'lucide-react'
import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useLocation } from 'react-router'
import { z } from 'zod'
import { Alert } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Field } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { APP_NAME } from '@/config'
import { ApiError } from '@/lib/api'
import { emailSchema, login } from './api'
import { useAuth } from './auth-context'
import { AuthLayout } from './AuthLayout'
import type { LoginLocationState } from './RouteGuards'

const loginSchema = z.object({
  user_email: emailSchema,
  user_password: z.string().min(1, 'Enter your password'),
})

export function LoginPage() {
  const auth = useAuth()
  const state = useLocation().state as LoginLocationState | null
  const signedUpEmail = state?.signedUpEmail

  const form = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: { user_email: signedUpEmail ?? '', user_password: '' },
  })
  const { errors } = form.formState

  // GuestOnly redirects once the session is set, so there's no navigate() here.
  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (response) => {
      if (!auth.login(response.access_token)) {
        form.setError('root', { message: 'The server sent a token this app could not read.' })
      }
    },
  })

  useEffect(() => {
    form.setFocus(signedUpEmail ? 'user_password' : 'user_email')
  }, [form, signedUpEmail])

  const loginError = mutation.error ? describeLoginError(mutation.error) : errors.root?.message

  return (
    <AuthLayout
      title="Log in"
      description="Use the email and password you signed up with."
      footer={
        <>
          No account yet?{' '}
          <Link to="/signup" className="font-medium text-primary hover:underline">
            Sign up
          </Link>
        </>
      }
    >
      <title>{`Log in · ${APP_NAME}`}</title>

      {signedUpEmail && !mutation.error && (
        <Alert variant="success" className="mb-5">
          Account created. Log in to continue.
        </Alert>
      )}
      {auth.expired && !signedUpEmail && !mutation.error && (
        <Alert className="mb-5">Your session expired. Log in again to continue.</Alert>
      )}
      {loginError && (
        <Alert variant="destructive" className="mb-5">
          {loginError}
        </Alert>
      )}

      <form noValidate onSubmit={form.handleSubmit((values) => mutation.mutate(values))} className="grid gap-4">
        <Field label="Email" htmlFor="user_email" error={errors.user_email?.message}>
          <Input
            id="user_email"
            type="email"
            autoComplete="username"
            aria-invalid={!!errors.user_email}
            aria-describedby="user_email-message"
            {...form.register('user_email')}
          />
        </Field>
        <Field label="Password" htmlFor="user_password" error={errors.user_password?.message}>
          <Input
            id="user_password"
            type="password"
            autoComplete="current-password"
            aria-invalid={!!errors.user_password}
            aria-describedby="user_password-message"
            {...form.register('user_password')}
          />
        </Field>
        <Button type="submit" className="mt-2 w-full" disabled={mutation.isPending}>
          {mutation.isPending && <Loader2 className="animate-spin" />}
          Log in
        </Button>
      </form>
    </AuthLayout>
  )
}

function describeLoginError(error: Error) {
  // The backend answers 401 for both an unknown email and a wrong password.
  if (error instanceof ApiError && (error.status === 401 || error.status === 403)) {
    return 'Invalid email or password.'
  }
  return error.message
}

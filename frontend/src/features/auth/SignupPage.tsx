import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { Loader2 } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router'
import { z } from 'zod'
import { Alert } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Field } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { APP_NAME } from '@/config'
import { ApiError } from '@/lib/api'
import { emailSchema, signup } from './api'
import { AuthLayout } from './AuthLayout'
import type { LoginLocationState } from './RouteGuards'

const signupSchema = z
  .object({
    first_name: z.string().trim().min(1, 'Enter your first name').max(100, 'Keep it under 100 characters'),
    last_name: z.string().trim().max(100, 'Keep it under 100 characters'),
    user_email: emailSchema,
    user_password: z.string().min(8, 'Use at least 8 characters'),
    confirm_password: z.string().min(1, 'Enter the password again'),
  })
  .refine((values) => values.user_password === values.confirm_password, {
    path: ['confirm_password'],
    message: "Passwords don't match",
  })

export function SignupPage() {
  const navigate = useNavigate()
  const form = useForm({
    resolver: zodResolver(signupSchema),
    defaultValues: { first_name: '', last_name: '', user_email: '', user_password: '', confirm_password: '' },
  })
  const { errors } = form.formState

  const mutation = useMutation({
    mutationFn: signup,
    // Signing up doesn't log you in; the login page confirms the account and asks for the password.
    onSuccess: (_, body) => {
      const state: LoginLocationState = { signedUpEmail: body.user_email }
      navigate('/login', { state })
    },
  })

  const onSubmit = form.handleSubmit((values) =>
    mutation.mutate({
      first_name: values.first_name,
      last_name: values.last_name || undefined,
      user_email: values.user_email,
      user_password: values.user_password,
    }),
  )

  const alreadyRegistered = mutation.error instanceof ApiError && mutation.error.status === 409

  return (
    <AuthLayout
      title="Create an account"
      description="Staff accounts manage plans, customers and billing."
      footer={
        <>
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-primary hover:underline">
            Log in
          </Link>
        </>
      }
    >
      <title>{`Sign up · ${APP_NAME}`}</title>

      {mutation.error && (
        <Alert variant="destructive" className="mb-5">
          {alreadyRegistered ? (
            <span>
              An account with this email already exists.{' '}
              <Link to="/login" className="font-medium underline">
                Log in instead
              </Link>
            </span>
          ) : (
            mutation.error.message
          )}
        </Alert>
      )}

      <form noValidate onSubmit={onSubmit} className="grid gap-4">
        <div className="grid grid-cols-2 gap-3">
          <Field label="First name" htmlFor="first_name" required error={errors.first_name?.message}>
            <Input
              id="first_name"
              autoComplete="given-name"
              autoFocus
              aria-invalid={!!errors.first_name}
              aria-describedby="first_name-message"
              {...form.register('first_name')}
            />
          </Field>
          <Field label="Last name" htmlFor="last_name" error={errors.last_name?.message}>
            <Input
              id="last_name"
              autoComplete="family-name"
              aria-invalid={!!errors.last_name}
              aria-describedby="last_name-message"
              {...form.register('last_name')}
            />
          </Field>
        </div>
        <Field label="Email" htmlFor="user_email" required error={errors.user_email?.message}>
          <Input
            id="user_email"
            type="email"
            autoComplete="email"
            aria-invalid={!!errors.user_email}
            aria-describedby="user_email-message"
            {...form.register('user_email')}
          />
        </Field>
        <Field
          label="Password"
          htmlFor="user_password"
          required
          hint="At least 8 characters."
          error={errors.user_password?.message}
        >
          <Input
            id="user_password"
            type="password"
            autoComplete="new-password"
            aria-invalid={!!errors.user_password}
            aria-describedby="user_password-message"
            {...form.register('user_password')}
          />
        </Field>
        <Field label="Confirm password" htmlFor="confirm_password" required error={errors.confirm_password?.message}>
          <Input
            id="confirm_password"
            type="password"
            autoComplete="new-password"
            aria-invalid={!!errors.confirm_password}
            aria-describedby="confirm_password-message"
            {...form.register('confirm_password')}
          />
        </Field>
        <Button type="submit" className="mt-2 w-full" disabled={mutation.isPending}>
          {mutation.isPending && <Loader2 className="animate-spin" />}
          Create account
        </Button>
      </form>
    </AuthLayout>
  )
}

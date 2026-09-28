import { Link, Navigate, type RouteObject } from 'react-router'
import { AppLayout } from '@/components/AppLayout'
import { buttonVariants } from '@/components/ui/button'
import { LoginPage } from '@/features/auth/LoginPage'
import { GuestOnly, RequireAuth } from '@/features/auth/RouteGuards'
import { SignupPage } from '@/features/auth/SignupPage'
import { NewPlanPage } from '@/features/plans/NewPlanPage'
import { PlanDetailPage } from '@/features/plans/PlanDetailPage'
import { PlansPage } from '@/features/plans/PlansPage'

export const routes: RouteObject[] = [
  {
    element: <GuestOnly />,
    children: [
      { path: '/login', element: <LoginPage /> },
      { path: '/signup', element: <SignupPage /> },
    ],
  },
  {
    element: <RequireAuth />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <Navigate to="/plans" replace /> },
          { path: '/plans', element: <PlansPage /> },
          { path: '/plans/new', element: <NewPlanPage /> },
          { path: '/plans/:planId', element: <PlanDetailPage /> },
        ],
      },
    ],
  },
  { path: '*', element: <NotFoundPage /> },
]

function NotFoundPage() {
  return (
    <div className="flex min-h-dvh flex-col items-center justify-center gap-2 px-4 text-center">
      <p className="text-sm font-medium text-primary">404</p>
      <h1 className="text-2xl font-semibold">Page not found</h1>
      <Link to="/" className={buttonVariants({ variant: 'outline', className: 'mt-4' })}>
        Go home
      </Link>
    </div>
  )
}

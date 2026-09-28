import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import type { ReactNode } from 'react'
import { Toaster } from 'sonner'
import { AuthProvider } from '@/features/auth/AuthProvider'
import { ApiError } from '@/lib/api'

export function createQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: 30_000,
        // Retry network and server errors, but not 4xx answers like 401 or 404.
        retry: (failureCount, error) =>
          failureCount < 2 && !(error instanceof ApiError && error.status >= 400 && error.status < 500),
      },
    },
  })
}

export function AppProviders({ queryClient, children }: { queryClient: QueryClient; children: ReactNode }) {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>{children}</AuthProvider>
      <Toaster position="top-right" richColors closeButton />
    </QueryClientProvider>
  )
}

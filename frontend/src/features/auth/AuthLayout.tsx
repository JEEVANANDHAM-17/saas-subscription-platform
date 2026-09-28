import type { ReactNode } from 'react'
import { Logo } from '@/components/Logo'
import { Card } from '@/components/ui/card'

export function AuthLayout({ title, description, children, footer }: {
  title: string
  description: string
  children: ReactNode
  footer: ReactNode
}) {
  return (
    <div className="flex min-h-dvh flex-col items-center justify-center bg-[radial-gradient(ellipse_at_top,var(--accent),transparent_60%)] px-4 py-10">
      <div className="w-full max-w-sm">
        <Logo className="mb-8 justify-center text-lg" />
        <Card className="px-6 py-7 shadow-sm">
          <div className="mb-6 grid gap-1.5">
            <h1 className="text-xl font-semibold tracking-tight">{title}</h1>
            <p className="text-sm text-muted-foreground">{description}</p>
          </div>
          {children}
        </Card>
        <p className="mt-6 text-center text-sm text-muted-foreground">{footer}</p>
      </div>
    </div>
  )
}

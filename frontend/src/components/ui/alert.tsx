import { cva, type VariantProps } from 'class-variance-authority'
import { CircleAlert, CircleCheck, Info } from 'lucide-react'
import type { ComponentProps } from 'react'
import { cn } from '@/lib/utils'

const alertVariants = cva('flex gap-3 rounded-lg border px-4 py-3 text-sm [&>svg]:mt-0.5 [&>svg]:size-4 [&>svg]:shrink-0', {
  variants: {
    variant: {
      info: 'border-primary/20 bg-accent text-accent-foreground',
      success: 'border-success/25 bg-success/8 text-success',
      destructive: 'border-destructive/25 bg-destructive/6 text-destructive',
    },
  },
  defaultVariants: { variant: 'info' },
})

const icons = { info: Info, success: CircleCheck, destructive: CircleAlert }

export function Alert({ className, variant, children, ...props }: ComponentProps<'div'> & VariantProps<typeof alertVariants>) {
  const Icon = icons[variant ?? 'info']
  return (
    <div role={variant === 'destructive' ? 'alert' : 'status'} className={cn(alertVariants({ variant }), className)} {...props}>
      <Icon aria-hidden="true" />
      <div className="grid gap-1">{children}</div>
    </div>
  )
}

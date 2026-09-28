import type { ReactNode } from 'react'
import { Label } from '@/components/ui/input'
import { cn } from '@/lib/utils'

interface FieldProps {
  label: string
  htmlFor: string
  required?: boolean
  hint?: ReactNode
  error?: string
  className?: string
  children: ReactNode
}

// Label + control + hint/error. Give the control id={htmlFor} and aria-describedby={`${htmlFor}-message`}.
export function Field({ label, htmlFor, required, hint, error, className, children }: FieldProps) {
  const message = error ?? hint
  return (
    // content-start: when a neighbour in the same form row shows an error, don't stretch to match its height.
    <div className={cn('grid content-start gap-2', className)}>
      <Label htmlFor={htmlFor}>
        {label}
        {required && <span className="ml-0.5 text-destructive" aria-hidden="true">*</span>}
      </Label>
      {children}
      {message && (
        <p
          id={`${htmlFor}-message`}
          role={error ? 'alert' : undefined}
          className={cn('text-[13px]', error ? 'text-destructive' : 'text-muted-foreground')}
        >
          {message}
        </p>
      )}
    </div>
  )
}

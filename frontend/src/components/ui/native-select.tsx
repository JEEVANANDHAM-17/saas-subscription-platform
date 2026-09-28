import { ChevronDown } from 'lucide-react'
import type { ComponentProps } from 'react'
import { controlClasses } from '@/components/ui/input'
import { cn } from '@/lib/utils'

// A styled <select>. Native so it works with react-hook-form's register() and on mobile.
export function NativeSelect({ className, children, ...props }: ComponentProps<'select'>) {
  return (
    <div className="relative">
      <select className={cn(controlClasses, 'h-9 appearance-none pr-9', className)} {...props}>
        {children}
      </select>
      <ChevronDown className="pointer-events-none absolute top-1/2 right-3 size-4 -translate-y-1/2 text-muted-foreground" />
    </div>
  )
}

import { Layers, LogOut } from 'lucide-react'
import { NavLink, Outlet } from 'react-router'
import { Logo } from '@/components/Logo'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/features/auth/auth-context'
import { cn } from '@/lib/utils'

// Add Customers, Subscriptions, Invoices, Payments here as their APIs land.
const navItems = [{ to: '/plans', label: 'Plans', icon: Layers }]

export function AppLayout() {
  return (
    <div className="min-h-dvh md:grid md:grid-cols-[240px_minmax(0,1fr)]">
      <aside className="hidden border-r bg-card md:sticky md:top-0 md:flex md:h-dvh md:flex-col">
        <div className="flex h-16 items-center px-5">
          <Logo className="text-[15px]" />
        </div>
        <nav aria-label="Main" className="grid gap-0.5 px-3 py-2">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  'flex h-9 items-center gap-2.5 rounded-md px-3 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground',
                  isActive && 'bg-accent text-accent-foreground hover:bg-accent hover:text-accent-foreground',
                )
              }
            >
              <item.icon className="size-4" />
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="mt-auto border-t p-3">
          <UserMenu />
        </div>
      </aside>

      <header className="sticky top-0 z-10 border-b bg-card/95 backdrop-blur md:hidden">
        <div className="flex h-14 items-center justify-between px-4">
          <Logo className="text-[15px]" />
          <UserMenu compact />
        </div>
        <nav aria-label="Main" className="flex gap-1 px-2 pb-2">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  'rounded-md px-3 py-1.5 text-sm font-medium text-muted-foreground',
                  isActive && 'bg-accent text-accent-foreground',
                )
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </header>

      <main className="px-4 py-6 md:px-8 md:py-8">
        <div className="mx-auto max-w-6xl">
          <Outlet />
        </div>
      </main>
    </div>
  )
}

function UserMenu({ compact = false }: { compact?: boolean }) {
  const { session, logout } = useAuth()
  if (!session) return null

  const displayName = session.name ?? session.email
  const initial = displayName.charAt(0).toUpperCase() || '?'

  if (compact) {
    return (
      <Button variant="ghost" size="sm" onClick={logout}>
        <LogOut />
        Log out
      </Button>
    )
  }

  return (
    <div className="flex items-center gap-3 rounded-md px-2 py-1.5">
      <span className="flex size-8 shrink-0 items-center justify-center rounded-full bg-accent text-sm font-semibold text-accent-foreground">
        {initial}
      </span>
      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-medium">{displayName}</p>
        {session.name && <p className="truncate text-xs text-muted-foreground">{session.email}</p>}
      </div>
      <Button variant="ghost" size="icon" className="size-8 text-muted-foreground" onClick={logout} aria-label="Log out" title="Log out">
        <LogOut />
      </Button>
    </div>
  )
}

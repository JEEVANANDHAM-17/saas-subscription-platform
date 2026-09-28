import { ArrowLeft } from 'lucide-react'
import { Link } from 'react-router'

export function BackToPlans() {
  return (
    <Link to="/plans" className="inline-flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground">
      <ArrowLeft className="size-4" />
      All plans
    </Link>
  )
}

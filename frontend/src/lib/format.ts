const priceFormat = new Intl.NumberFormat(undefined, {
  minimumFractionDigits: 2,
  maximumFractionDigits: 4,
})

const dateTimeFormat = new Intl.DateTimeFormat(undefined, {
  dateStyle: 'medium',
  timeStyle: 'short',
})

// The API has no currency yet, so prices are plain numbers.
export function formatPrice(price: number) {
  return priceFormat.format(price)
}

// The backend sends LocalDateTime without a zone ("2026-09-28T13:06:47.123"), in server time.
export function formatDateTime(value: string | null | undefined) {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : dateTimeFormat.format(date)
}

// The same three values as the Priority enum in the backend
export type Priority = 'HIGH' | 'MEDIUM' | 'LOW'

// A wishlist entry as the backend returns it (WishlistEntryResponse in the backend)
export type WishlistEntry = {
    id: string
    destination: string
    reason: string
    priority: Priority
    bestTravelTime: string | null // optional, e.g. "Februar"
}

// The data the frontend sends when an entry is created or changed
export type WishlistEntryRequest = {
    destination: string
    reason: string
    priority: Priority
    bestTravelTime: string // an empty text means "no value"
}

// German labels for the priorities, in the order they are shown
export const PRIORITY_LABELS: Record<Priority, string> = {
    HIGH: 'Hoch',
    MEDIUM: 'Mittel',
    LOW: 'Niedrig',
}

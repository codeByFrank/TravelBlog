// The same three values as the Priority enum in the backend
export type Priority = 'HIGH' | 'MEDIUM' | 'LOW'

// A wishlist entry as the backend returns it
export type WishlistEntry = {
    id: string
    userId: string
    destination: string
    reason: string
    priority: Priority
}

// The data the frontend sends when an entry is created or changed
export type WishlistEntryRequest = {
    destination: string
    reason: string
    priority: Priority
}

// German labels for the priorities, in the order they are shown
export const PRIORITY_LABELS: Record<Priority, string> = {
    HIGH: 'Hoch',
    MEDIUM: 'Mittel',
    LOW: 'Niedrig',
}

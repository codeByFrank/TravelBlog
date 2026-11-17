// The same four values as the TripTag enum in the backend
export type TripTag = 'MOUNTAINS' | 'SEA' | 'CITY_TRIP' | 'FOOD'

// All tags in display order, with their German labels
export const TRIP_TAGS: TripTag[] = ['MOUNTAINS', 'SEA', 'CITY_TRIP', 'FOOD']

export const TRIP_TAG_LABELS: Record<TripTag, string> = {
    MOUNTAINS: 'Berge',
    SEA: 'Meer',
    CITY_TRIP: 'Städtetrip',
    FOOD: 'Essen',
}

// A trip as returned by GET /api/trips (TripResponse in the backend)
export type Trip = {
    id: string
    title: string
    description: string | null
    startDate: string // ISO date, e.g. "2024-05-12"
    endDate: string
    coverColor: string | null
    tags: TripTag[] // empty if the trip has no tags
}

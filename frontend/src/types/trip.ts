// The same values as the TripTag enum in the backend
export type TripTag = 'MOUNTAINS' | 'SEA' | 'CITY_TRIP' | 'FOOD' | 'FRIENDS_AND_FAMILY'

// All tags in display order, with their German labels
export const TRIP_TAGS: TripTag[] = ['MOUNTAINS', 'SEA', 'CITY_TRIP', 'FOOD', 'FRIENDS_AND_FAMILY']

export const TRIP_TAG_LABELS: Record<TripTag, string> = {
    MOUNTAINS: 'Berge',
    SEA: 'Meer',
    CITY_TRIP: 'Städtetrip',
    FOOD: 'Essen',
    FRIENDS_AND_FAMILY: 'Freunde & Familie',
}

// A place visited during a trip (StopResponse in the backend)
export type Stop = {
    id: string
    name: string
    latitude: number
    longitude: number
    countryCode: string // ISO 3166-1 alpha-2, e.g. "IT"
    date: string // arrival date, e.g. "2024-05-15"
    note: string | null
    overnight: boolean // stayed overnight here, not just a day visit or passing through
    distanceFromPreviousKm: number | null // straight line; null for the first stop
}

// A trip as returned by GET /api/trips (TripResponse in the backend)
export type Trip = {
    id: string
    title: string // the user's own title or, without one, built from the places, e.g. "Rom – Florenz"
    customTitle: string | null // the user's own title; null if the title is built from the places
    description: string | null
    startDate: string // ISO date, e.g. "2024-05-12"
    endDate: string
    coverColor: string | null
    tags: TripTag[] // empty if the trip has no tags
    stops: Stop[] // in the order of the visit (by date)
    routeLengthKm: number // sum of the straight-line distances between the stops
}

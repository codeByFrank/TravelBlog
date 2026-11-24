// The same values as the TripTag enum in the backend
export type TripTag = 'MOUNTAINS' | 'SEA' | 'CITY_TRIP' | 'FOOD' | 'FRIENDS_AND_FAMILY'

// All tags in display order; the labels are in the texts (i18n/de.ts → tags)
export const TRIP_TAGS: TripTag[] = ['MOUNTAINS', 'SEA', 'CITY_TRIP', 'FOOD', 'FRIENDS_AND_FAMILY']

// How the user spent the time at a stop; the same values as the StopType enum in the backend.
// Overnight stops and stops passed on the way are the route of the trip; a day trip goes there and back
// from the last overnight stop (its base) and is not part of the route.
export type StopType = 'OVERNIGHT' | 'PASSING_THROUGH' | 'DAY_TRIP'

// All types in display order; the labels and hints for the stop dialog are in the texts (i18n/de.ts → stopTypes)
export const STOP_TYPES: StopType[] = ['OVERNIGHT', 'PASSING_THROUGH', 'DAY_TRIP']

// A place visited during a trip (StopResponse in the backend)
export type Stop = {
    id: string
    name: string
    latitude: number
    longitude: number
    countryCode: string // ISO 3166-1 alpha-2, e.g. "IT"
    date: string // arrival date, e.g. "2024-05-15"
    // last day of the stay: for an overnight stop the arrival at the next one or the end of the trip,
    // for the only stop of a trip the end of the trip, otherwise the same as date
    departureDate: string
    note: string | null
    type: StopType
    onRoute: boolean // the route line of the trip connects this stop
    // straight line in km: for a stop on the route from the previous one on the route, for a day trip from its base;
    // null if there is none
    distanceFromPreviousKm: number | null
    baseStopId: string | null // for a day trip: the overnight stop the user started from
    photoIds: string[]
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
    routeLengthKm: number // sum of the straight-line distances between the stops on the route
    coverPhotoId: string | null
}

// Values for a new trip that is planned from a wishlist entry ("Reise planen" on the wishlist).
// The trip dialog starts with them instead of empty fields; the user can still change everything.
export type TripDraft = {
    title: string // e.g. "Patagonien"
    description: string // the reason of the wish, e.g. "Wandern im Torres del Paine"
    placeQuery: string // the text the place search starts with, e.g. "Patagonien"
}
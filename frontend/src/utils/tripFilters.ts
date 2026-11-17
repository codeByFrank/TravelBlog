import {TRIP_TAG_LABELS, type TripTag} from '../types/trip'

// Search, year and tag filter and small calculations for the trip overview.
// They only need these fields, so they work with every trip type that has them.
export type FilterableTrip = {
    title: string
    description?: string | null
    startDate: string // ISO date, e.g. "2024-05-12"
    endDate: string
    tags?: TripTag[]
}

// Value of the year filter that shows all trips
export const ALL_YEARS = 'Alle'

// A trip belongs to the year in which it starts
export function getTripYear(trip: FilterableTrip): string {
    return trip.startDate.slice(0, 4)
}

// Years that have trips, newest first, e.g. ["2025", "2024"]
export function getYears(trips: FilterableTrip[]): string[] {
    return [...new Set(trips.map(getTripYear))].sort((a, b) => b.localeCompare(a))
}

// Trips that match all filters at once:
// - the search text in title, description or tag names (case-insensitive),
// - the selected year,
// - every selected tag (AND): with "Berge" and "Essen" only trips that have both are shown.
export function filterTrips<T extends FilterableTrip>(
    trips: T[],
    query: string,
    year: string,
    selectedTags: TripTag[] = [],
): T[] {
    const search = query.trim().toLocaleLowerCase('de')
    return trips.filter((trip) => {
        const tags = trip.tags ?? []
        const matchesYear = year === ALL_YEARS || getTripYear(trip) === year
        const matchesTags = selectedTags.every((tag) => tags.includes(tag))
        const tagNames = tags.map((tag) => TRIP_TAG_LABELS[tag]).join(' ')
        const text = `${trip.title} ${trip.description ?? ''} ${tagNames}`.toLocaleLowerCase('de')
        return matchesYear && matchesTags && text.includes(search)
    })
}

const DAY_IN_MS = 24 * 60 * 60 * 1000

// Number of travel days, counting the first and the last day: 12.–20. May is 9 days
export function getTripDays(trip: FilterableTrip): number {
    const start = Date.parse(trip.startDate)
    const end = Date.parse(trip.endDate)
    return Math.round((end - start) / DAY_IN_MS) + 1
}

// "1 Reise", "6 Reisen"
export function formatTripCount(count: number): string {
    return count === 1 ? '1 Reise' : `${count} Reisen`
}

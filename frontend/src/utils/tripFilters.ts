import {TRIP_TAG_LABELS, type Stop, type TripTag} from '../types/trip'
import {getCountryName} from './countries'

// Search, year and tag filter and small calculations for the trip overview.
// They only need these fields, so they work with every trip type that has them.
export type FilterableTrip = {
    title: string
    description?: string | null
    startDate: string // ISO date, e.g. "2024-05-12"
    endDate: string
    tags?: TripTag[]
    stops?: Pick<Stop, 'name' | 'countryCode' | 'note'>[] // in the order of the visit
    routeLengthKm?: number
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

// Everything the free-text search looks at, e.g. "Italien 2024 … Berge Rom Italien Bestes Eis der Reise"
function getSearchText(trip: FilterableTrip): string {
    const tagNames = (trip.tags ?? []).map((tag) => TRIP_TAG_LABELS[tag])
    const stopTexts = (trip.stops ?? []).flatMap((stop) => [stop.name, getCountryName(stop.countryCode), stop.note ?? ''])
    return [trip.title, trip.description ?? '', ...tagNames, ...stopTexts].join(' ')
}

// Trips that match all filters at once:
// - the search text in title, description, tag names or the stops' names, countries and notes (case-insensitive),
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
        const text = getSearchText(trip).toLocaleLowerCase('de')
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

// Countries of a trip in the order of the route, each only once, e.g. ["IT", "FR"]
export function getCountryCodes(trip: FilterableTrip): string[] {
    return [...new Set((trip.stops ?? []).map((stop) => stop.countryCode))]
}

export type TripTotals = {
    days: number
    countries: number // distinct countries over all trips
    places: number // all stops of all trips
    km: number // sum of the route lengths
}

// Totals for the tiles above the trip overview
export function getTotals(trips: FilterableTrip[]): TripTotals {
    return {
        days: trips.reduce((sum, trip) => sum + getTripDays(trip), 0),
        countries: new Set(trips.flatMap(getCountryCodes)).size,
        places: trips.reduce((sum, trip) => sum + (trip.stops?.length ?? 0), 0),
        km: trips.reduce((sum, trip) => sum + (trip.routeLengthKm ?? 0), 0),
    }
}

export type YearTotals = TripTotals & {
    year: string // e.g. "2024"
    trips: number
}

// The totals of each year that has trips, newest first, e.g. "2024: 3 Reisen, 5 Länder, 4.200 km".
// A trip counts in the year in which it starts; a country visited on two trips of a year counts once.
export function getTotalsByYear(trips: FilterableTrip[]): YearTotals[] {
    return getYears(trips).map((year) => {
        const tripsOfYear = trips.filter((trip) => getTripYear(trip) === year)
        return {year, trips: tripsOfYear.length, ...getTotals(tripsOfYear)}
    })
}

// "1 Tag", "12 Tage"
export function formatDays(days: number): string {
    return days === 1 ? '1 Tag' : `${days} Tage`
}

// "1 Land", "5 Länder"
export function formatCountryCount(count: number): string {
    return count === 1 ? '1 Land' : `${count} Länder`
}

// "1.234 km"
export function formatKm(km: number): string {
    return `${km.toLocaleString('de-DE')} km`
}

import type {Trip} from '../types/trip'

// Dates come from the API as "YYYY-MM-DD"; they are read and formatted in UTC
// so that the day does not shift with the browser's time zone.
const dateRangeFormat = new Intl.DateTimeFormat('de-DE', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    timeZone: 'UTC',
})

const dayMonthFormat = new Intl.DateTimeFormat('de-DE', {
    day: 'numeric',
    month: 'long',
    timeZone: 'UTC',
})

// "15. Mai"
export function formatDayMonth(date: string): string {
    return dayMonthFormat.format(new Date(date))
}

// "12.–20. Mai 2024", "28. Juni – 3. Juli 2024", "30. Dezember 2024 – 2. Januar 2025"
export function formatDateRange(start: string, end: string): string {
    return dateRangeFormat.formatRange(new Date(start), new Date(end))
}

// Suggested arrival date for a new stop: the day after the last stop, but not after the end of the trip;
// the first stop gets the start of the trip. Dates are "YYYY-MM-DD" and compared as text.
export function suggestStopDate(trip: Trip): string {
    const lastStop = trip.stops.at(-1)
    if (!lastStop) return trip.startDate
    const nextDay = new Date(lastStop.date)
    nextDay.setUTCDate(nextDay.getUTCDate() + 1)
    const suggested = nextDay.toISOString().slice(0, 10)
    return suggested > trip.endDate ? trip.endDate : suggested
}

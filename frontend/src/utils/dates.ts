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

// Today in the browser's time zone as "YYYY-MM-DD", the format of <input type="date">
export function todayIso(): string {
    const today = new Date()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    return `${today.getFullYear()}-${month}-${day}`
}

// "15. Mai"
export function formatDayMonth(date: string): string {
    return dayMonthFormat.format(new Date(date))
}

// "12.–20. Mai 2024", "28. Juni – 3. Juli 2024", "30. Dezember 2024 – 2. Januar 2025"
export function formatDateRange(start: string, end: string): string {
    return dateRangeFormat.formatRange(new Date(start), new Date(end))
}

// The days at a stop: "15. Mai" for a single day, "12.–15. Mai · 3 Nächte" for a stay
export function formatStay(arrival: string, departure: string): string {
    if (departure <= arrival) {
        return formatDayMonth(arrival)
    }
    const nights = Math.round((new Date(departure).getTime() - new Date(arrival).getTime()) / 86_400_000)
    const days = dayMonthFormat.formatRange(new Date(arrival), new Date(departure))
    return `${days} · ${nights} ${nights === 1 ? 'Nacht' : 'Nächte'}`
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

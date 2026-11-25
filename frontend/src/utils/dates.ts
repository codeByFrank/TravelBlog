import {LOCALES, type Language} from '../i18n/language'
import {texts} from '../i18n/texts'
import type {Trip} from '../types/trip'

// Dates come from the API as "YYYY-MM-DD"; they are read and formatted in UTC
// so that the day does not shift with the browser's time zone.
// One format per language, made once: "12. Mai 2024" in German, "12 May 2024" in English.
const dateRangeFormats: Record<Language, Intl.DateTimeFormat> = {
    de: new Intl.DateTimeFormat(LOCALES.de, {day: 'numeric', month: 'long', year: 'numeric', timeZone: 'UTC'}),
    en: new Intl.DateTimeFormat(LOCALES.en, {day: 'numeric', month: 'long', year: 'numeric', timeZone: 'UTC'}),
}

const dayMonthFormats: Record<Language, Intl.DateTimeFormat> = {
    de: new Intl.DateTimeFormat(LOCALES.de, {day: 'numeric', month: 'long', timeZone: 'UTC'}),
    en: new Intl.DateTimeFormat(LOCALES.en, {day: 'numeric', month: 'long', timeZone: 'UTC'}),
}

// Today in the browser's time zone as "YYYY-MM-DD", the format of <input type="date">
export function todayIso(): string {
    const today = new Date()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    return `${today.getFullYear()}-${month}-${day}`
}

// "15. Mai" / "15 May"
export function formatDayMonth(date: string, language: Language): string {
    return dayMonthFormats[language].format(new Date(date))
}

// "12.–20. Mai 2024", "28. Juni – 3. Juli 2024", "30. Dezember 2024 – 2. Januar 2025";
// in English "12–20 May 2024", "28 June – 3 July 2024"
export function formatDateRange(start: string, end: string, language: Language): string {
    return dateRangeFormats[language].formatRange(new Date(start), new Date(end))
}

// The days at a stop: "15. Mai" for a single day, "12.–15. Mai · 3 Nächte" for a stay
export function formatStay(arrival: string, departure: string, language: Language): string {
    if (departure <= arrival) {
        return formatDayMonth(arrival, language)
    }
    const nights = Math.round((new Date(departure).getTime() - new Date(arrival).getTime()) / 86_400_000)
    const days = dayMonthFormats[language].formatRange(new Date(arrival), new Date(departure))
    return `${days} · ${texts[language].common.nights(nights)}`
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

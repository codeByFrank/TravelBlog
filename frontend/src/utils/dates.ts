// Dates come from the API as "YYYY-MM-DD"; they are read and formatted in UTC
// so that the day does not shift with the browser's time zone.
const dateRangeFormat = new Intl.DateTimeFormat('de-DE', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    timeZone: 'UTC',
})

// "12.–20. Mai 2024", "28. Juni – 3. Juli 2024", "30. Dezember 2024 – 2. Januar 2025"
export function formatDateRange(start: string, end: string): string {
    return dateRangeFormat.formatRange(new Date(start), new Date(end))
}

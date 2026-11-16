function parseIsoDate(value: string): Date {
    const [year, month, day] = value.split('-').map(Number)
    return new Date(year, month - 1, day)
}

function monthName(date: Date): string {
    return date.toLocaleDateString('de-DE', {month: 'long'})
}

export function formatDateRange(start: string, end: string): string {
    const from = parseIsoDate(start)
    const to = parseIsoDate(end)
    const sameYear = from.getFullYear() === to.getFullYear()
    const sameMonth = sameYear && from.getMonth() === to.getMonth()

    if (sameMonth && from.getDate() === to.getDate()) {
        return `${to.getDate()}. ${monthName(to)} ${to.getFullYear()}`
    }
    if (sameMonth) {
        return `${from.getDate()}.–${to.getDate()}. ${monthName(to)} ${to.getFullYear()}`
    }
    if (sameYear) {
        return `${from.getDate()}. ${monthName(from)} – ${to.getDate()}. ${monthName(to)} ${to.getFullYear()}`
    }
    return `${from.getDate()}. ${monthName(from)} ${from.getFullYear()} – ${to.getDate()}. ${monthName(to)} ${to.getFullYear()}`
}
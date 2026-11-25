import {LOCALES, type Language} from '../i18n/language'
import type {Trip} from '../types/trip'
import {getCountryName} from './countries'
import {getCountryCodes} from './tripFilters'

// Number of countries in the world as it is usually counted (193 UN members, the Vatican and Palestine).
// "Anteil der Welt" on the world map is measured against this number.
export const WORLD_COUNTRY_COUNT = 195

// One line of the list "Besuchte Länder", e.g. Italien · 2024 · 2 Reisen
export type VisitedCountry = {
    code: string // e.g. "IT"
    name: string // name in the language of the user interface, e.g. "Italien" / "Italy"
    lastYear: string // year of the latest stop in this country, e.g. "2024"
    tripCount: number // number of trips with at least one stop in this country
}

// All countries the user has visited, each only once, newest first.
// A country counts as visited when at least one stop lies in it (requirements, section 8).
export function collectVisitedCountries(trips: Trip[], language: Language): VisitedCountry[] {
    const visitedCountries: VisitedCountry[] = []

    for (const trip of trips) {
        // Each country only once per trip, so that one trip is not counted twice
        for (const code of getCountryCodes(trip)) {
            const lastYear = getLastYearInCountry(trip, code)
            const existingCountry = visitedCountries.find((country) => country.code === code)

            if (existingCountry) {
                existingCountry.tripCount = existingCountry.tripCount + 1
                if (lastYear > existingCountry.lastYear) {
                    existingCountry.lastYear = lastYear
                }
            } else {
                visitedCountries.push({code, name: getCountryName(code, language), lastYear, tripCount: 1})
            }
        }
    }

    // Newest year first; within the same year in alphabetical order
    return visitedCountries.toSorted((a, b) => {
        if (a.lastYear !== b.lastYear) {
            return b.lastYear.localeCompare(a.lastYear)
        }
        return a.name.localeCompare(b.name, language)
    })
}

// The trips with at least one stop in a country, newest first, e.g. all trips to "IT".
// Shown when the user clicks a visited country (requirements, section 8).
export function getTripsToCountry(trips: Trip[], code: string): Trip[] {
    const tripsToCountry = trips.filter((trip) => getCountryCodes(trip).includes(code))
    // ISO dates like "2024-05-12" can be compared as text
    return tripsToCountry.toSorted((a, b) => b.startDate.localeCompare(a.startDate))
}

// Year of the latest stop of a trip in one country, e.g. "2024".
// The dates are ISO dates like "2024-05-15", so the first four characters are the year.
function getLastYearInCountry(trip: Trip, code: string): string {
    let lastYear = ''
    for (const stop of trip.stops) {
        const year = stop.date.substring(0, 4)
        if (stop.countryCode === code && year > lastYear) {
            lastYear = year
        }
    }
    return lastYear
}

// Share of the world's countries, rounded to whole percent, e.g. 8 countries → "4 %" in German, "4%" in English
export function getWorldShare(countryCount: number, language: Language): string {
    const share = Math.round((countryCount / WORLD_COUNTRY_COUNT) * 100) / 100
    return share.toLocaleString(LOCALES[language], {style: 'percent'})
}

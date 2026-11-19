import type {Trip} from '../types/trip'
import {getCountryName} from './countries'
import {getCountryCodes} from './tripFilters'

// Number of countries in the world as it is usually counted (193 UN members, the Vatican and Palestine).
// "Anteil der Welt" on the world map is measured against this number.
export const WORLD_COUNTRY_COUNT = 195

// One line of the list "Besuchte Länder", e.g. Italien · 2024 · 2 Reisen
export type VisitedCountry = {
    code: string // e.g. "IT"
    name: string // German name, e.g. "Italien"
    lastYear: string // year of the latest stop in this country, e.g. "2024"
    tripCount: number // number of trips with at least one stop in this country
}

// All countries the user has visited, each only once, newest first.
// A country counts as visited when at least one stop lies in it (requirements, section 8).
export function collectVisitedCountries(trips: Trip[]): VisitedCountry[] {
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
                visitedCountries.push({code, name: getCountryName(code), lastYear, tripCount: 1})
            }
        }
    }

    // Newest year first; within the same year in alphabetical order
    return visitedCountries.toSorted((a, b) => {
        if (a.lastYear !== b.lastYear) {
            return b.lastYear.localeCompare(a.lastYear)
        }
        return a.name.localeCompare(b.name, 'de')
    })
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

// Share of the world's countries, rounded to whole percent, e.g. 8 countries → "4 %"
export function getWorldShare(countryCount: number): string {
    const percent = Math.round((countryCount / WORLD_COUNTRY_COUNT) * 100)
    return `${percent} %`
}

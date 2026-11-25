import type {Stop, Trip} from '../types/trip'
import {getCountryName} from './countries'
import {getTripDays} from './tripFilters'

// Calculations for the statistics page beyond the totals (see architecture.md → Search and Statistics).
// Dates are "YYYY-MM-DD" and are read in UTC, so that the day does not shift with the browser's time zone.

// Every day from start to end, both included, as "YYYY-MM-DD"; empty if end is before start
function eachDay(start: string, end: string): string[] {
    const days: string[] = []
    const date = new Date(start)
    const last = new Date(end)
    while (date <= last) {
        days.push(date.toISOString().slice(0, 10))
        date.setUTCDate(date.getUTCDate() + 1)
    }
    return days
}

export type TripRecord = {
    value: number
    trips: Trip[] // all trips with this value, in the order given (the API sends the newest first)
}

// The trips with the highest value; several if they share it. Null if no trip has a value above 0.
function findRecord(trips: Trip[], valueOf: (trip: Trip) => number): TripRecord | null {
    const max = Math.max(0, ...trips.map(valueOf))
    if (max === 0) {
        return null
    }
    return {value: max, trips: trips.filter((trip) => valueOf(trip) === max)}
}

export type TripRecords = {
    longest: TripRecord | null // most travel days
    farthest: TripRecord | null // longest route
    mostPlaces: TripRecord | null // most stops
}

export function getRecords(trips: Trip[]): TripRecords {
    return {
        longest: findRecord(trips, getTripDays),
        farthest: findRecord(trips, (trip) => trip.routeLengthKm),
        mostPlaces: findRecord(trips, (trip) => trip.stops.length),
    }
}

export type CountryTime = {
    code: string // e.g. "IT"
    trips: number
    days: number
}

// For each country the number of trips and the days spent there, most days first.
// A stop covers the days from its arrival to its departure (one day if the user did not stay overnight).
// The day of a move counts for both countries; within one trip each day counts once per country.
export function getCountryTimes(trips: Trip[]): CountryTime[] {
    const daysByCountry = new Map<string, Set<string>>() // "IT" → {"trip1|2024-05-12", …}
    const tripsByCountry = new Map<string, Set<string>>() // "IT" → {"trip1", …}
    for (const trip of trips) {
        for (const stop of trip.stops) {
            const days = daysByCountry.get(stop.countryCode) ?? new Set<string>()
            for (const day of eachDay(stop.date, stop.departureDate)) {
                days.add(`${trip.id}|${day}`)
            }
            daysByCountry.set(stop.countryCode, days)
            tripsByCountry.set(stop.countryCode, (tripsByCountry.get(stop.countryCode) ?? new Set()).add(trip.id))
        }
    }
    return [...daysByCountry.entries()]
        .map(([code, days]) => ({code, trips: tripsByCountry.get(code)?.size ?? 0, days: days.size}))
        .sort((a, b) => b.days - a.days
            || b.trips - a.trips
            // Ties in a fixed order by the German name, the same in both languages
            || getCountryName(a.code, 'de').localeCompare(getCountryName(b.code, 'de'), 'de'))
}

// Travel days in each month over all years: index 0 is January. A trip from 28 March to 10 April
// adds 4 days to March and 10 to April.
export function getDaysPerMonth(trips: Trip[]): number[] {
    const days = Array.from({length: 12}, () => 0)
    for (const trip of trips) {
        for (const day of eachDay(trip.startDate, trip.endDate)) {
            days[Number(day.slice(5, 7)) - 1] += 1
        }
    }
    return days
}

export type ExtremePoint = {
    stop: Stop
    trip: Trip
}

export type ExtremePoints = {
    north: ExtremePoint
    south: ExtremePoint
    west: ExtremePoint
    east: ExtremePoint
}

// The northernmost, southernmost, westernmost and easternmost stop of all trips; null without stops.
// West and east are compared by longitude, so a trip across the date line is not treated specially.
export function getExtremePoints(trips: Trip[]): ExtremePoints | null {
    const points: ExtremePoint[] = trips.flatMap((trip) => trip.stops.map((stop) => ({stop, trip})))
    if (points.length === 0) {
        return null
    }
    // Starts with the first stop, so that reduce has an initial value
    const pick = (better: (a: Stop, b: Stop) => boolean) =>
        points.reduce((best, point) => (better(point.stop, best.stop) ? point : best), points[0])
    return {
        north: pick((a, b) => a.latitude > b.latitude),
        south: pick((a, b) => a.latitude < b.latitude),
        west: pick((a, b) => a.longitude < b.longitude),
        east: pick((a, b) => a.longitude > b.longitude),
    }
}

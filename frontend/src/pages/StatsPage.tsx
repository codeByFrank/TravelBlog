import {useEffect, useState} from 'react'
import {getErrorMessage} from '../api/client'
import {getTrips} from '../api/trips'
import CountryStats from '../components/CountryStats'
import ExtremePoints from '../components/ExtremePoints'
import MonthStats from '../components/MonthStats'
import TripRecords from '../components/TripRecords'
import TripStats from '../components/TripStats'
import YearStats from '../components/YearStats'
import type {Trip} from '../types/trip'
import {formatKm, getTotals, getTotalsByYear} from '../utils/tripFilters'
import {getCountryTimes, getDaysPerMonth, getExtremePoints, getRecords} from '../utils/tripStats'

// "Statistik": the totals over all trips, records, the breakdown by year, countries and months,
// and the extreme points.
// On its own page, so that the trip overview starts with the trips.
export default function StatsPage() {
    const [trips, setTrips] = useState<Trip[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Load the trips once when the page opens
    useEffect(() => {
        let cancelled = false // ignore the answer if the page was left in the meantime
        getTrips()
            .then((data) => {
                if (!cancelled) setTrips(data)
            })
            .catch((err) => {
                if (!cancelled) setError(getErrorMessage(err, 'Die Statistik konnte nicht geladen werden.'))
            })
            .finally(() => {
                if (!cancelled) setLoading(false)
            })
        return () => {
            cancelled = true
        }
    }, [])

    const totals = getTotals(trips)
    const stats = [
        {label: 'Reisen', value: String(trips.length)},
        {label: 'Reisetage', value: String(totals.days)},
        {label: 'Länder', value: String(totals.countries)},
        {label: 'Orte', value: String(totals.places)},
        {label: 'Strecke gesamt', value: formatKm(totals.km)},
    ]
    const yearTotals = getTotalsByYear(trips)
    const countryTimes = getCountryTimes(trips)
    const extremePoints = getExtremePoints(trips)

    return (
        <div className="flex flex-col gap-7">
            <section className="flex flex-col gap-1.5">
                <h1 className="text-4xl font-bold tracking-tight">Statistik</h1>
                <p className="text-muted">Deine Reisen in Zahlen – Rekorde, Jahre, Länder und Monate.</p>
            </section>

            {loading && <p className="text-muted">Statistik wird geladen …</p>}

            {!loading && error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {!loading && !error && trips.length === 0 && (
                <section className="flex flex-col items-center gap-2 rounded-card bg-surface px-6 py-14 text-center shadow-card">
                    <h2 className="text-xl font-bold">Noch keine Reisen</h2>
                    <p className="max-w-md text-muted">Sobald du deine erste Reise anlegst, erscheinen hier ihre Zahlen.</p>
                </section>
            )}

            {!loading && !error && trips.length > 0 && (
                <>
                    <TripStats stats={stats}/>
                    <TripRecords records={getRecords(trips)}/>
                    <YearStats years={yearTotals}/>
                    {/* Side by side from lg on; trips saved before every trip needed a stop may have no countries */}
                    <div className="grid items-start gap-6 lg:grid-cols-2">
                        {countryTimes.length > 0 && <CountryStats countries={countryTimes}/>}
                        <MonthStats daysPerMonth={getDaysPerMonth(trips)}/>
                    </div>
                    {extremePoints && <ExtremePoints points={extremePoints}/>}
                </>
            )}
        </div>
    )
}

import {useEffect, useState} from 'react'
import {getErrorMessage} from '../api/client'
import {getTrips} from '../api/trips'
import TripCard from '../components/TripCard'
import type {Trip} from '../types/trip'

export default function TripsPage() {
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
                if (!cancelled) setError(getErrorMessage(err, 'Deine Reisen konnten nicht geladen werden.'))
            })
            .finally(() => {
                if (!cancelled) setLoading(false)
            })
        return () => {
            cancelled = true
        }
    }, [])

    return (
        <div className="flex flex-col gap-7">
            <section className="flex flex-col gap-1.5">
                <h1 className="text-4xl font-bold tracking-tight">Meine Reisen</h1>
                <p className="text-muted">Alle Reisen an einem Ort – mit Karte, Fotos und Eindrücken.</p>
            </section>

            {loading && <p className="text-muted">Reisen werden geladen …</p>}

            {!loading && error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {!loading && !error && trips.length === 0 && (
                <section className="flex flex-col items-center gap-2 rounded-card bg-surface px-6 py-14 text-center shadow-card">
                    <h2 className="text-xl font-bold">Noch keine Reisen</h2>
                    <p className="max-w-md text-muted">
                        Sobald du deine erste Reise anlegst, erscheint sie hier.
                    </p>
                </section>
            )}

            {!loading && !error && trips.length > 0 && (
                <div className="grid grid-cols-[repeat(auto-fill,minmax(min(300px,100%),1fr))] gap-6">
                    {trips.map((trip, index) => (
                        <TripCard key={trip.id} trip={trip} index={index}/>
                    ))}
                </div>
            )}
        </div>
    )
}

import {useEffect, useState} from 'react'
import {fetchTrips, type Trip} from '../api/trips'
import TripCard from '../components/TripCard'

function byStartDateDesc(a: Trip, b: Trip): number {
    return b.startDate.localeCompare(a.startDate)
}

export default function TripsPage() {
    const [trips, setTrips] = useState<Trip[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        fetchTrips()
            .then((loaded) => setTrips([...loaded].sort(byStartDateDesc)))
            .catch(() => setError('Die Reisen konnten nicht geladen werden.'))
            .finally(() => setLoading(false))
    }, [])

    return (
        <div className="flex flex-col gap-8">
            <section className="flex flex-col gap-1.5">
                <h1 className="text-4xl font-bold tracking-tight">Meine Reisen</h1>
                <p className="text-muted">Alle Reisen an einem Ort – mit Karte, Fotos und Eindrücken.</p>
            </section>

            {loading && <p className="text-muted">Reisen werden geladen …</p>}

            {error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {!loading && !error && trips.length === 0 && (
                <p className="rounded-card bg-surface p-8 text-center text-muted shadow-card">
                    Noch keine Reisen. Lege deine erste Reise an!
                </p>
            )}

            {trips.length > 0 && (
                <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                    {trips.map((trip) => (
                        <TripCard key={trip.id} trip={trip}/>
                    ))}
                </div>
            )}
        </div>
    )
}
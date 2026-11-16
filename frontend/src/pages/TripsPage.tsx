import {useEffect, useState} from 'react'
import {fetchTrips, type Trip} from '../api/trips'
import NewTripDialog from '../components/NewTripDialog'
import TripCard from '../components/TripCard'

// Newest trip first
function byStartDateDesc(a: Trip, b: Trip): number {
    return b.startDate.localeCompare(a.startDate)
}

export default function TripsPage() {
    const [trips, setTrips] = useState<Trip[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [dialogOpen, setDialogOpen] = useState(false)

    useEffect(() => {
        fetchTrips()
            .then((loaded) => setTrips([...loaded].sort(byStartDateDesc)))
            .catch(() => setError('Die Reisen konnten nicht geladen werden.'))
            .finally(() => setLoading(false))
    }, [])

    function handleCreated(trip: Trip) {
        setTrips((current) => [trip, ...current].sort(byStartDateDesc))
        setDialogOpen(false)
    }

    return (
        <div className="flex flex-col gap-8">
            <section className="flex flex-wrap items-end justify-between gap-4">
                <div className="flex flex-col gap-1.5">
                    <h1 className="text-4xl font-bold tracking-tight">Meine Reisen</h1>
                    <p className="text-muted">Alle Reisen an einem Ort – mit Karte, Fotos und Eindrücken.</p>
                </div>
                <button
                    type="button"
                    onClick={() => setDialogOpen(true)}
                    className="flex min-h-12 cursor-pointer items-center gap-2 rounded-full bg-accent px-6 text-[15px] font-semibold text-white shadow-[0_6px_18px_rgba(31,111,191,0.25)] hover:bg-accent-dark"
                >
                    <span aria-hidden="true" className="text-xl leading-none">+</span>
                    Neue Reise
                </button>
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

            <NewTripDialog open={dialogOpen} onClose={() => setDialogOpen(false)} onCreated={handleCreated}/>
        </div>
    )
}
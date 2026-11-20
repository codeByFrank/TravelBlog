import {Fragment, useState} from 'react'
import {getErrorMessage} from '../api/client'
import {deleteStop} from '../api/stops'
import type {Stop, Trip} from '../types/trip'
import {getCountryName} from '../utils/countries'
import {formatDayMonth} from '../utils/dates'
import {formatKm} from '../utils/tripFilters'
import ConfirmDialog from './ConfirmDialog'
import StopDialog from './StopDialog'
import TripMap from './TripMap'

type TripStopsProps = {
    trip: Trip
    onTripChange: (trip: Trip) => void // after a stop was added, changed or deleted
}

const smallButton = 'min-h-11 cursor-pointer rounded-full px-4 text-sm font-semibold'

// "Reiseverlauf" on the trip detail page, as in the mockup: the map on the left and the stops on the right
// (one below the other on narrow screens), in the order of the visit with the distances between them,
// and adding, changing and deleting stops
export default function TripStops({trip, onTripChange}: Readonly<TripStopsProps>) {
    const [dialogOpen, setDialogOpen] = useState(false)
    const [stopToEdit, setStopToEdit] = useState<Stop | null>(null)
    const [stopToDelete, setStopToDelete] = useState<Stop | null>(null)
    const [error, setError] = useState('')

    function openNew() {
        setStopToEdit(null)
        setDialogOpen(true)
    }

    function openEdit(stop: Stop) {
        setStopToEdit(stop)
        setDialogOpen(true)
    }

    async function handleDelete(stop: Stop) {
        setStopToDelete(null)
        setError('')
        try {
            onTripChange(await deleteStop(trip.id, stop.id))
        } catch (err) {
            setError(getErrorMessage(err, 'Der Ort konnte nicht gelöscht werden.'))
        }
    }

    return (
        <section aria-labelledby="stops-title" className="flex flex-col gap-5">
            <div className="flex flex-wrap items-center justify-between gap-3">
                <h2 id="stops-title" className="text-2xl font-bold tracking-tight">Reiseverlauf</h2>
                <button
                    type="button"
                    onClick={openNew}
                    className="flex min-h-12 cursor-pointer items-center gap-2 rounded-full bg-accent px-6 text-[15px] font-semibold text-white shadow-[0_6px_18px_rgba(31,111,191,0.25)] hover:bg-accent-dark"
                >
                    <span aria-hidden="true" className="text-xl leading-none">+</span>
                    <span>Ort hinzufügen</span>
                </button>
            </div>

            {error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>
            )}

            {trip.stops.length === 0 ? (
                <div className="flex flex-col items-center gap-2 rounded-card bg-surface px-6 py-12 text-center shadow-card">
                    <h3 className="text-xl font-bold">Noch keine Orte</h3>
                    <p className="max-w-md text-muted">
                        Füge die Orte deiner Reise hinzu – sie erscheinen hier in der Reihenfolge deines Besuchs.
                    </p>
                </div>
            ) : (
                <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1.1fr)_minmax(0,1fr)]">
                    {/* Sticky, so the map stays in view while the user scrolls through a long list of places */}
                    <TripMap stops={trip.stops} routeLengthKm={trip.routeLengthKm} className="lg:sticky lg:top-6"/>
                    <ol className="flex flex-col gap-3">
                        {trip.stops.map((stop, position) => (
                            <Fragment key={stop.id}>
                                {stop.distanceFromPreviousKm !== null && (
                                    <li aria-hidden="true" className="flex items-center gap-2 pl-6 text-sm font-semibold text-muted">
                                        <span>↓</span>
                                        <span>{formatKm(stop.distanceFromPreviousKm)}</span>
                                    </li>
                                )}
                                <li>
                                    <article className="flex flex-col gap-3 rounded-card bg-surface px-6 py-5 shadow-card">
                                        <div className="flex flex-wrap items-center gap-x-3 gap-y-1">
                                            {/* The number shows the order of the visit, so a single place has none */}
                                            {trip.stops.length > 1 && (
                                                <span className="flex size-8 items-center justify-center rounded-full bg-accent-soft text-sm font-bold text-accent">
                                                    {position + 1}
                                                </span>
                                            )}
                                            <h3 className="text-xl font-bold tracking-tight">{stop.name}</h3>
                                            <span className="text-sm font-semibold text-muted">
                                                {formatDayMonth(stop.date)} · {getCountryName(stop.countryCode)}
                                            </span>
                                            {stop.overnight && (
                                                <span className="rounded-full bg-chip px-3 py-1 text-[13px] font-semibold text-ink-soft">
                                                    Übernachtung
                                                </span>
                                            )}
                                        </div>
                                        {stop.note && <p className="text-ink-soft">{stop.note}</p>}
                                        <div className="flex flex-wrap gap-2">
                                            <button
                                                type="button"
                                                onClick={() => openEdit(stop)}
                                                className={`${smallButton} border border-line-strong text-ink hover:border-accent`}
                                            >
                                                Bearbeiten<span className="sr-only">: {stop.name}</span>
                                            </button>
                                            {/* A trip keeps at least one place; the last one can only be changed */}
                                            {trip.stops.length > 1 && (
                                                <button
                                                    type="button"
                                                    onClick={() => setStopToDelete(stop)}
                                                    className={`${smallButton} text-red-700 hover:bg-red-50`}
                                                >
                                                    Löschen<span className="sr-only">: {stop.name}</span>
                                                </button>
                                            )}
                                        </div>
                                    </article>
                                </li>
                            </Fragment>
                        ))}
                    </ol>
                </div>
            )}

            <StopDialog
                open={dialogOpen}
                trip={trip}
                stop={stopToEdit}
                onClose={() => setDialogOpen(false)}
                onSaved={(saved) => {
                    onTripChange(saved)
                    setDialogOpen(false)
                }}
            />

            {stopToDelete && (
                <ConfirmDialog
                    title="Ort löschen?"
                    message={`„${stopToDelete.name}“ wird aus dieser Reise entfernt.`}
                    confirmLabel="Löschen"
                    onConfirm={() => void handleDelete(stopToDelete)}
                    onCancel={() => setStopToDelete(null)}
                />
            )}
        </section>
    )
}

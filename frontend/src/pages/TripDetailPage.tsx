import axios from 'axios'
import {useEffect, useState} from 'react'
import {Link, useParams} from 'react-router-dom'
import {getErrorMessage} from '../api/client'
import {getTrip} from '../api/trips'
import type {Trip} from '../types/trip'
import {formatDateRange} from '../utils/dates'

function BackLink() {
    return (
        <Link to="/trips" className="flex w-fit items-center gap-1.5 text-[15px] font-semibold text-muted hover:text-ink">
            <span aria-hidden="true">‹</span>
            Meine Reisen
        </Link>
    )
}

export default function TripDetailPage() {
    const {id} = useParams()
    const [trip, setTrip] = useState<Trip | null>(null)
    const [loading, setLoading] = useState(true)
    const [notFound, setNotFound] = useState(false)
    const [error, setError] = useState('')

    useEffect(() => {
        if (!id) return
        let cancelled = false
        getTrip(id)
            .then((data) => {
                if (!cancelled) setTrip(data)
            })
            .catch((err) => {
                if (cancelled) return
                if (axios.isAxiosError(err) && err.response?.status === 404) {
                    setNotFound(true)
                } else {
                    setError(getErrorMessage(err, 'Die Reise konnte nicht geladen werden.'))
                }
            })
            .finally(() => {
                if (!cancelled) setLoading(false)
            })
        return () => {
            cancelled = true
        }
    }, [id])

    if (loading) {
        return <p className="text-muted">Reise wird geladen …</p>
    }

    if (notFound || !trip) {
        return (
            <div className="flex flex-col gap-6">
                <BackLink/>
                {error ? (
                    <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>
                ) : (
                    <section className="flex flex-col items-center gap-2 rounded-card bg-surface px-6 py-14 text-center shadow-card">
                        <h1 className="text-xl font-bold">Reise nicht gefunden</h1>
                        <p className="max-w-md text-muted">Diese Reise gibt es nicht (mehr).</p>
                    </section>
                )}
            </div>
        )
    }

    return (
        <div className="flex flex-col gap-7">
            <BackLink/>

            <section className="flex flex-wrap items-end justify-between gap-4">
                <div className="flex max-w-2xl flex-col gap-1.5">
                    <p className="text-[15px] font-semibold text-muted">{formatDateRange(trip.startDate, trip.endDate)}</p>
                    <h1 className="text-4xl font-bold tracking-tight">{trip.title}</h1>
                    {trip.description && <p className="text-ink-soft">{trip.description}</p>}
                </div>
            </section>
        </div>
    )
}
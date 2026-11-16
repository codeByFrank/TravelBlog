import {Link} from 'react-router-dom'
import type {Trip} from '../api/trips'
import {DEFAULT_COVER_COLOR} from '../coverColors'
import {formatDateRange} from '../utils/formatDateRange'

export default function TripCard({trip}: { trip: Trip }) {
    return (
        <Link
            to={`/trips/${trip.id}`}
            className="group flex flex-col overflow-hidden rounded-card bg-surface shadow-card transition hover:-translate-y-0.5"
        >
            {/* Cover: color for now, a photo later */}
            <div className="h-36" style={{backgroundColor: trip.coverColor ?? DEFAULT_COVER_COLOR}}/>

            <div className="flex flex-col gap-1.5 px-5 pt-4 pb-5">
                <p className="text-sm text-muted">{formatDateRange(trip.startDate, trip.endDate)}</p>
                <h2 className="text-lg font-bold text-ink group-hover:text-accent">{trip.title}</h2>
            </div>
        </Link>
    )
}
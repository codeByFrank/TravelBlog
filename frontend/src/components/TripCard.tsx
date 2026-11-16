import {Link} from 'react-router-dom'
import type {Trip} from '../types/trip'
import {formatDateRange} from '../utils/dates'

// Pastel placeholder covers from the mockup, used in turn until trips have a cover image
const coverColors = ['bg-cover-1', 'bg-cover-2', 'bg-cover-3', 'bg-cover-4']

type TripCardProps = {
    trip: Trip
    index: number
}

// One tile on the "Meine Reisen" page; the whole card links to the trip's detail page
export default function TripCard({trip, index}: TripCardProps) {
    return (
        <Link
            to={`/trips/${trip.id}`}
            className="group flex flex-col overflow-hidden rounded-card bg-surface shadow-card transition hover:-translate-y-0.5 focus-visible:outline-4 focus-visible:outline-accent-soft"
        >
            <div className={`h-37.5 ${coverColors[index % coverColors.length]}`}/>
            <div className="flex flex-col gap-2.5 px-5.5 pt-5 pb-5.5">
                <span className="text-[13px] font-semibold text-muted">
                    {formatDateRange(trip.startDate, trip.endDate)}
                </span>
                <h2 className="text-xl font-bold tracking-tight text-ink group-hover:text-accent">{trip.title}</h2>
                {trip.description && <p className="line-clamp-2 text-[15px] text-ink-soft">{trip.description}</p>}
            </div>
        </Link>
    )
}

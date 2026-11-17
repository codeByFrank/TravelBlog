import {Link} from 'react-router-dom'
import type {Trip} from '../types/trip'
import {formatDateRange} from '../utils/dates'
import {getTripDays} from '../utils/tripFilters'
import TagBadges from './TagBadges'

// Pastel placeholder covers from the mockup, used in turn for trips without a chosen color
const fallbackCoverColors = ['bg-cover-1', 'bg-cover-2', 'bg-cover-3', 'bg-cover-4']

type TripCardProps = {
    trip: Trip
    index: number
}

// "1 Tag", "9 Tage"
function formatDays(days: number): string {
    return days === 1 ? '1 Tag' : `${days} Tage`
}

// One tile on the "Meine Reisen" page; the whole card links to the trip's detail page
export default function TripCard({trip, index}: Readonly<TripCardProps>) {
    // The color chosen in "Neue Reise" wins; trips without a color get a pastel tone in turn
    const fallbackClass = trip.coverColor ? '' : fallbackCoverColors[index % fallbackCoverColors.length]

    return (
        <Link
            to={`/trips/${trip.id}`}
            className="group flex flex-col overflow-hidden rounded-card bg-surface shadow-card transition hover:-translate-y-0.5 focus-visible:outline-4 focus-visible:outline-accent-soft"
        >
            <div
                className={`h-37.5 ${fallbackClass}`}
                style={trip.coverColor ? {backgroundColor: trip.coverColor} : undefined}
            />
            <div className="flex flex-col gap-2.5 px-5.5 pt-5 pb-5.5">
                <span className="text-[13px] font-semibold text-muted">
                    {formatDateRange(trip.startDate, trip.endDate)}
                </span>
                <h2 className="text-xl font-bold tracking-tight text-ink group-hover:text-accent">{trip.title}</h2>
                {trip.description && <p className="line-clamp-2 text-[15px] text-ink-soft">{trip.description}</p>}
                {/* Badges; stops, distance and photos join here once trips have them */}
                <div className="mt-1 flex flex-wrap gap-2">
                    <span className="rounded-full bg-chip px-3 py-1.5 text-[13px] font-semibold text-ink-soft">
                        {formatDays(getTripDays(trip))}
                    </span>
                </div>
                <TagBadges tags={trip.tags}/>
            </div>
        </Link>
    )
}
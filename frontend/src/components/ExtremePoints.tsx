import {Link} from 'react-router-dom'
import {getCountryName} from '../utils/countries'
import type {ExtremePoint, ExtremePoints as Points} from '../utils/tripStats'

const DIRECTIONS: { key: keyof Points; label: string }[] = [
    {key: 'north', label: 'Nördlichster Ort'},
    {key: 'south', label: 'Südlichster Ort'},
    {key: 'west', label: 'Westlichster Ort'},
    {key: 'east', label: 'Östlichster Ort'},
]

function PointCard({label, point}: Readonly<{ label: string; point: ExtremePoint }>) {
    return (
        <div className="flex flex-col gap-1 rounded-card bg-surface px-6 py-5 shadow-card">
            <span className="text-[13px] font-semibold text-muted">{label}</span>
            <span className="text-xl font-bold">{point.stop.name}</span>
            <span className="text-sm text-ink-soft">{getCountryName(point.stop.countryCode)}</span>
            <Link to={`/trips/${point.trip.id}`} className="mt-1 text-sm font-semibold text-accent hover:text-accent-dark">
                {point.trip.title}
            </Link>
        </div>
    )
}

// "Extrempunkte" on the statistics page: the northernmost, southernmost, westernmost and easternmost place,
// each with the trip it belongs to
export default function ExtremePoints({points}: Readonly<{ points: Points }>) {
    return (
        <section aria-labelledby="extreme-points-title" className="flex flex-col gap-3">
            <h2 id="extreme-points-title" className="text-2xl font-bold tracking-tight">Extrempunkte</h2>
            <div className="grid grid-cols-[repeat(auto-fit,minmax(min(220px,100%),1fr))] gap-4">
                {DIRECTIONS.map((direction) => (
                    <PointCard key={direction.key} label={direction.label} point={points[direction.key]}/>
                ))}
            </div>
        </section>
    )
}

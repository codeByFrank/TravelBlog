import {Link} from 'react-router-dom'
import {formatDateRange} from '../utils/dates'
import {formatDays, formatKm} from '../utils/tripFilters'
import type {TripRecord, TripRecords as Records} from '../utils/tripStats'

type RecordCardProps = {
    label: string // e.g. "Längste Reise"
    record: TripRecord
    formatValue: (value: number) => string
}

// One record with its value and every trip that holds it (several if they share the value)
function RecordCard({label, record, formatValue}: Readonly<RecordCardProps>) {
    return (
        <div className="flex flex-col gap-3 rounded-card bg-surface px-6 py-5 shadow-card">
            <div className="flex flex-col gap-1">
                <span className="text-[13px] font-semibold text-muted">{label}</span>
                <span className="text-3xl font-bold">{formatValue(record.value)}</span>
            </div>
            <ul className="flex flex-col gap-1.5">
                {record.trips.map((trip) => (
                    <li key={trip.id} className="flex flex-col">
                        <Link to={`/trips/${trip.id}`} className="font-semibold text-accent hover:text-accent-dark">
                            {trip.title}
                        </Link>
                        <span className="text-sm text-muted">{formatDateRange(trip.startDate, trip.endDate)}</span>
                    </li>
                ))}
            </ul>
        </div>
    )
}

// "Rekorde" on the statistics page: the longest trip, the one with the longest route and the one with the most
// places. A record nobody holds (e.g. no trip has a route yet) is left out.
export default function TripRecords({records}: Readonly<{ records: Records }>) {
    return (
        <section aria-labelledby="records-title" className="flex flex-col gap-3">
            <h2 id="records-title" className="text-2xl font-bold tracking-tight">Rekorde</h2>
            <div className="grid grid-cols-[repeat(auto-fit,minmax(min(260px,100%),1fr))] gap-4">
                {records.longest && (
                    <RecordCard label="Längste Reise" record={records.longest} formatValue={formatDays}/>
                )}
                {records.farthest && (
                    <RecordCard label="Weiteste Strecke" record={records.farthest} formatValue={formatKm}/>
                )}
                {records.mostPlaces && (
                    <RecordCard
                        label="Meiste Orte"
                        record={records.mostPlaces}
                        formatValue={(places) => (places === 1 ? '1 Ort' : `${places} Orte`)}
                    />
                )}
            </div>
        </section>
    )
}

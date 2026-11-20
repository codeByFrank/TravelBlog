import {Link} from 'react-router-dom'
import {formatCountryCount, formatDays, formatKm, formatTripCount, type YearTotals} from '../utils/tripFilters'

type YearStatsProps = {
    years: YearTotals[] // newest first
}

// "Nach Jahren" on the statistics page: one row per year with its trips, travel days, countries and distance,
// and a bar whose length shows the distance compared with the year with the longest distance.
// A row opens the trip overview filtered by that year.
export default function YearStats({years}: Readonly<YearStatsProps>) {
    const maxKm = Math.max(...years.map((year) => year.km))

    return (
        <section aria-labelledby="year-stats-title" className="flex flex-col gap-3 rounded-card bg-surface px-6 py-5 shadow-card">
            <h2 id="year-stats-title" className="text-[13px] font-semibold text-muted">Nach Jahren</h2>
            <ul className="flex flex-col gap-1">
                {years.map((year) => {
                    // A year without distance (only single places) gets no bar
                    const barPercent = maxKm > 0 ? (year.km / maxKm) * 100 : 0
                    return (
                        <li key={year.year}>
                            <Link
                                // Not HOME_PATH from navigation.tsx: that file imports the pages, so it would be a circular import
                                to={`/trips?year=${year.year}`}
                                className="grid w-full grid-cols-[3.5rem_minmax(0,1fr)] items-center gap-x-4 gap-y-1.5 rounded-xl px-3 py-2.5 hover:bg-page sm:grid-cols-[3.5rem_20rem_minmax(0,1fr)]"
                            >
                                <span className="text-lg font-bold text-ink">{year.year}</span>
                                {/* Each number stays on one line with its unit, e.g. "1.500 km" */}
                                <span className="flex flex-wrap gap-x-1.5 text-[15px] text-ink-soft">
                                    <span className="whitespace-nowrap">{formatTripCount(year.trips)}</span>
                                    <span aria-hidden="true">·</span>
                                    <span className="whitespace-nowrap">{formatDays(year.days)}</span>
                                    <span aria-hidden="true">·</span>
                                    <span className="whitespace-nowrap">{formatCountryCount(year.countries)}</span>
                                    <span aria-hidden="true">·</span>
                                    <span className="whitespace-nowrap">{formatKm(year.km)}</span>
                                </span>
                                {/* The numbers are in the text; the bar only shows the proportions */}
                                <span aria-hidden="true" className="col-span-2 flex h-2 max-w-md sm:col-span-1">
                                    <span className="rounded-r-sm bg-accent" style={{width: `${barPercent}%`}}/>
                                </span>
                            </Link>
                        </li>
                    )
                })}
            </ul>
        </section>
    )
}

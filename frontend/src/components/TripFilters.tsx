import {ALL_YEARS, formatTripCount} from '../utils/tripFilters'

type TripFiltersProps = {
    query: string
    onQueryChange: (query: string) => void
    years: string[] // years that have trips, newest first
    selectedYear: string
    onYearChange: (year: string) => void
    resultCount: number // number of trips that match the filters
}

const pillBase = 'min-h-11 cursor-pointer rounded-full border px-4.5 text-sm font-semibold'

// Search field, year buttons and the number of matching trips, as in the mockup
export default function TripFilters({
                                        query,
                                        onQueryChange,
                                        years,
                                        selectedYear,
                                        onYearChange,
                                        resultCount,
                                    }: TripFiltersProps) {
    return (
        <div className="flex flex-wrap items-center gap-3">
            <div className="relative flex-[1_1_280px] md:max-w-105">
                <label htmlFor="trip-search" className="sr-only">
                    Reisen durchsuchen
                </label>
                <SearchIcon/>
                <input
                    id="trip-search"
                    type="search"
                    value={query}
                    onChange={(e) => onQueryChange(e.target.value)}
                    placeholder="Ort, Land oder Stichwort suchen"
                    className="h-11 w-full rounded-full border border-line-strong bg-surface pr-4 pl-11 text-[15px] text-ink outline-none focus:ring-4 focus:ring-accent-soft"
                />
            </div>

            <div role="group" aria-label="Nach Jahr filtern" className="flex flex-wrap gap-2">
                {[ALL_YEARS, ...years].map((year) => {
                    const active = year === selectedYear
                    return (
                        <button
                            key={year}
                            type="button"
                            aria-pressed={active}
                            onClick={() => onYearChange(year)}
                            className={
                                active
                                    ? `${pillBase} border-accent bg-accent text-white`
                                    : `${pillBase} border-line-strong bg-surface text-ink hover:border-accent`
                            }
                        >
                            {year}
                        </button>
                    )
                })}
            </div>

            <span aria-live="polite" className="ml-auto text-sm text-muted">
                {formatTripCount(resultCount)}
            </span>
        </div>
    )
}

function SearchIcon() {
    return (
        <svg
            aria-hidden="true"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="pointer-events-none absolute top-1/2 left-4 -translate-y-1/2 text-muted"
        >
            <circle cx="11" cy="11" r="7"/>
            <path d="m20 20-3.5-3.5"/>
        </svg>
    )
}

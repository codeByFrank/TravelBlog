import {useEffect, useState} from 'react'
import {Link} from 'react-router-dom'
import {getTrips} from '../api/trips'
import {getWishlistEntries} from '../api/wishlistApi'
import TripStats, {type TripStat} from '../components/TripStats'
import WorldMap from '../components/WorldMap'
import type {CountryCollection} from '../types/country'
import type {Trip} from '../types/trip'
import type {WishlistEntry} from '../types/WishlistEntry'
import {getContinents, getCountryName} from '../utils/countries'
import {loadCountryBorders} from '../utils/countryBorders'
import {formatDateRange} from '../utils/dates'
import {formatTripCount} from '../utils/tripFilters'
import {collectVisitedCountries, getTripsToCountry, getWorldShare} from '../utils/visitedCountries'

// All countries of all wishlist entries, each only once, e.g. ["IS", "AR", "CL"]
function collectWishlistCodes(entries: WishlistEntry[]): string[] {
    const codes: string[] = []
    for (const entry of entries) {
        for (const code of entry.countryCodes) {
            if (!codes.includes(code)) {
                codes.push(code)
            }
        }
    }
    return codes
}

export default function WorldMapPage() {
    const [countries, setCountries] = useState<CountryCollection | null>(null)
    const [mapError, setMapError] = useState('')
    const [trips, setTrips] = useState<Trip[]>([])
    const [tripsError, setTripsError] = useState('')
    const [wishlistEntries, setWishlistEntries] = useState<WishlistEntry[]>([])
    const [wishlistError, setWishlistError] = useState('')
    // The visited country whose trips are shown below the map; null while none is chosen
    const [selectedCode, setSelectedCode] = useState<string | null>(null)

    // Load the country borders, the trips and the wishlist once when the page opens.
    // They are loaded separately: if the trips or the wishlist fail, the map is still shown.
    useEffect(() => {
        loadCountryBorders()
            .then((loadedCountries) => setCountries(loadedCountries))
            .catch(() => setMapError('Die Weltkarte konnte nicht geladen werden.'))

        getTrips()
            .then((loadedTrips) => setTrips(loadedTrips))
            .catch(() => setTripsError('Die Reisen konnten nicht geladen werden.'))

        getWishlistEntries()
            .then((loadedEntries) => setWishlistEntries(loadedEntries))
            .catch(() => setWishlistError('Die Wunschziele konnten nicht geladen werden.'))
    }, [])

    const visitedCountries = collectVisitedCountries(trips)
    const visitedCodes = visitedCountries.map((country) => country.code)
    const wishlistCodes = collectWishlistCodes(wishlistEntries)
    const selectedTrips = selectedCode ? getTripsToCountry(trips, selectedCode) : []

    // A click on a country (on the map or in the list) shows its trips; a second click on the same country hides them.
    // The function form of setSelectedCode always gets the current value, also inside the Leaflet click handler.
    function toggleCountry(code: string) {
        setSelectedCode((currentCode) => (currentCode === code ? null : code))
    }

    // Countries that are too small for the world map (e.g. Malta) count as visited,
    // but they have no continent in our list and so do not count for "Kontinente".
    const continentCount = getContinents(visitedCodes).length

    // Tiles as in the mockup
    const stats: TripStat[] = [
        {label: 'Länder besucht', value: String(visitedCodes.length)},
        {label: 'Kontinente', value: String(continentCount)},
        {label: 'Anteil der Welt', value: getWorldShare(visitedCodes.length)},
        {label: 'Wunschziele', value: String(wishlistEntries.length)},
        {label: 'Wunschländer', value: String(wishlistCodes.length)},
    ]

    // The hint under the map is only needed while a color is still missing
    const hints: string[] = []
    if (visitedCodes.length === 0) {
        hints.push('Besuchte Länder erscheinen hier, sobald deine Reisen Stopps haben.')
    }
    if (wishlistCodes.length === 0) {
        hints.push('Wunschziele erscheinen, sobald sie ein Land haben.')
    }

    return (
        <section className="flex flex-col gap-8">
            <div className="flex flex-col gap-1.5">
                <h1 className="text-4xl font-bold tracking-tight">Weltkarte</h1>
                <p className="text-muted">Wo du schon warst – und wohin es noch gehen soll.</p>
            </div>

            <TripStats stats={stats}/>

            {mapError && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {mapError}
                </p>
            )}

            {!mapError && (
                <div className="flex flex-col gap-5 rounded-card bg-surface p-4 shadow-card md:p-6">
                    {countries ? (
                        <WorldMap
                            countries={countries}
                            visitedCodes={visitedCodes}
                            wishlistCodes={wishlistCodes}
                            selectedCode={selectedCode}
                            onVisitedCountryClick={toggleCountry}
                        />
                    ) : (
                        <p className="flex h-56 items-center justify-center text-muted md:h-[28rem]">
                            Weltkarte wird geladen …
                        </p>
                    )}

                    {/* Legend as in the mockup; the colored dots are only decoration for screen readers */}
                    <ul className="flex flex-wrap gap-x-6 gap-y-2 text-sm text-ink-soft">
                        <li className="flex items-center gap-2">
                            <span aria-hidden="true" className="size-3 rounded-full bg-accent"/>
                            <span>Besucht</span>
                        </li>
                        <li className="flex items-center gap-2">
                            <span aria-hidden="true" className="size-3 rounded-full bg-wish"/>
                            <span>Wunschziel</span>
                        </li>
                        <li className="flex items-center gap-2">
                            <span aria-hidden="true" className="size-3 rounded-full bg-land"/>
                            <span>Noch offen</span>
                        </li>
                    </ul>

                    {hints.length > 0 && <p className="text-sm text-muted">{hints.join(' ')}</p>}
                </div>
            )}

            {/* The trips to the chosen country, directly below the map so that the user sees them after the click */}
            {selectedCode && (
                <div className="flex flex-col gap-4 rounded-card bg-surface p-6 shadow-card">
                    <div className="flex items-center justify-between gap-4">
                        <h2 className="text-lg font-bold tracking-tight">
                            Deine Reisen nach {getCountryName(selectedCode)}
                        </h2>
                        <button
                            type="button"
                            onClick={() => setSelectedCode(null)}
                            className="min-h-10 cursor-pointer rounded-full border border-line-strong bg-surface px-4 text-sm font-semibold text-accent hover:bg-accent-soft"
                        >
                            Schließen
                        </button>
                    </div>

                    <ul className="flex flex-col">
                        {selectedTrips.map((trip) => (
                            <li key={trip.id} className="border-t border-line">
                                <Link
                                    to={`/trips/${trip.id}`}
                                    className="flex flex-wrap items-baseline justify-between gap-x-4 gap-y-1 py-3 text-[15px] hover:text-accent"
                                >
                                    <span className="font-semibold">{trip.title}</span>
                                    <span className="text-sm text-muted">
                                        {formatDateRange(trip.startDate, trip.endDate)}
                                    </span>
                                </Link>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            {/* As in the mockup: visited countries on the left, wishlist destinations on the right */}
            <div className="grid items-start gap-6 md:grid-cols-2">
                <div className="flex flex-col gap-4 rounded-card bg-surface p-6 shadow-card">
                    <h2 className="text-lg font-bold tracking-tight">Besuchte Länder</h2>

                    {tripsError && (
                        <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                            {tripsError}
                        </p>
                    )}

                    {!tripsError && visitedCountries.length === 0 && (
                        <p className="text-sm text-muted">Noch keine Reisen mit Stopps.</p>
                    )}

                    {visitedCountries.length > 0 && (
                        <ul className="flex flex-col">
                            {/* Buttons, so that the trips of a country can also be opened with the keyboard;
                                the countries on the map cannot be reached with the keyboard */}
                            {visitedCountries.map((country) => (
                                <li key={country.code} className="border-t border-line">
                                    <button
                                        type="button"
                                        onClick={() => toggleCountry(country.code)}
                                        aria-pressed={country.code === selectedCode}
                                        className={`flex w-full cursor-pointer items-baseline justify-between gap-4 py-3 text-left text-[15px] hover:text-accent ${
                                            country.code === selectedCode ? 'text-accent' : ''
                                        }`}
                                    >
                                        <span className="font-semibold">{country.name}</span>
                                        <span className="text-right text-sm text-muted">
                                            {`${country.lastYear} · ${formatTripCount(country.tripCount)}`}
                                        </span>
                                    </button>
                                </li>
                            ))}
                        </ul>
                    )}

                    <Link to="/trips" className="text-sm font-semibold text-accent underline hover:text-accent-dark">
                        Zu meinen Reisen
                    </Link>
                </div>

                <div className="flex flex-col gap-4 rounded-card bg-surface p-6 shadow-card">
                    <h2 className="text-lg font-bold tracking-tight">Wunschziele</h2>

                    {wishlistError && (
                        <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                            {wishlistError}
                        </p>
                    )}

                    {!wishlistError && wishlistEntries.length === 0 && (
                        <p className="text-sm text-muted">Noch keine Wunschziele.</p>
                    )}

                    {wishlistEntries.length > 0 && (
                        <ul className="flex flex-col">
                            {wishlistEntries.map((entry) => (
                                <li
                                    key={entry.id}
                                    className="flex items-baseline justify-between gap-4 border-t border-line py-3 text-[15px]"
                                >
                                    <span className="font-semibold">{entry.destination}</span>
                                    <span className="text-right text-sm text-muted">
                                        {getContinents(entry.countryCodes).join(', ')}
                                    </span>
                                </li>
                            ))}
                        </ul>
                    )}

                    <Link to="/wishlist" className="text-sm font-semibold text-accent underline hover:text-accent-dark">
                        Zur Wunschliste
                    </Link>
                </div>
            </div>
        </section>
    )
}

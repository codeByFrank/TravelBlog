import {useEffect, useState} from 'react'
import {Link} from 'react-router-dom'
import {getWishlistEntries} from '../api/wishlistApi'
import TripStats, {type TripStat} from '../components/TripStats'
import WorldMap from '../components/WorldMap'
import type {CountryCollection} from '../types/country'
import type {WishlistEntry} from '../types/WishlistEntry'
import {getContinents} from '../utils/countries'
import {loadCountryBorders} from '../utils/countryBorders'

// Not filled yet: visited countries follow once trips have stops with a country code
const VISITED_CODES: string[] = []

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
    const [wishlistEntries, setWishlistEntries] = useState<WishlistEntry[]>([])
    const [wishlistError, setWishlistError] = useState('')

    // Load the country borders and the wishlist once when the page opens.
    // They are loaded separately: if the wishlist fails, the map is still shown.
    useEffect(() => {
        loadCountryBorders()
            .then((loadedCountries) => setCountries(loadedCountries))
            .catch(() => setMapError('Die Weltkarte konnte nicht geladen werden.'))

        getWishlistEntries()
            .then((loadedEntries) => setWishlistEntries(loadedEntries))
            .catch(() => setWishlistError('Die Wunschziele konnten nicht geladen werden.'))
    }, [])

    const wishlistCodes = collectWishlistCodes(wishlistEntries)

    // Tiles as in the mockup; "Länder besucht", "Kontinente" and "Anteil der Welt" follow with the stops
    const stats: TripStat[] = [
        {label: 'Wunschziele', value: String(wishlistEntries.length)},
        {label: 'Wunschländer', value: String(wishlistCodes.length)},
    ]

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
                        <WorldMap countries={countries} visitedCodes={VISITED_CODES} wishlistCodes={wishlistCodes}/>
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

                    <p className="text-sm text-muted">
                        Besuchte Länder erscheinen hier, sobald deine Reisen Stopps haben.
                        {wishlistCodes.length === 0 && ' Wunschziele erscheinen, sobald sie ein Land haben.'}
                    </p>
                </div>
            )}

            {/* As in the mockup: visited countries on the left, wishlist destinations on the right */}
            <div className="grid items-start gap-6 md:grid-cols-2">
                <div className="flex flex-col gap-4 rounded-card bg-surface p-6 shadow-card">
                    <h2 className="text-lg font-bold tracking-tight">Besuchte Länder</h2>
                    <p className="text-sm text-muted">Erscheinen hier, sobald deine Reisen Stopps haben.</p>
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

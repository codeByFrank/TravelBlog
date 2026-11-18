import {useEffect, useState} from 'react'
import WorldMap from '../components/WorldMap'
import type {CountryCollection} from '../types/country'
import {loadCountryBorders} from '../utils/countryBorders'

// Not filled yet: the wishlist gets country codes in the next step,
// visited countries follow once trips have stops with a country code.
const VISITED_CODES: string[] = []
const WISHLIST_CODES: string[] = []

export default function WorldMapPage() {
    const [countries, setCountries] = useState<CountryCollection | null>(null)
    const [error, setError] = useState('')

    // Load the country borders once when the page opens
    useEffect(() => {
        loadCountryBorders()
            .then((loadedCountries) => setCountries(loadedCountries))
            .catch(() => setError('Die Weltkarte konnte nicht geladen werden.'))
    }, [])

    const nothingColored = VISITED_CODES.length === 0 && WISHLIST_CODES.length === 0

    return (
        <section className="flex flex-col gap-8">
            <div className="flex flex-col gap-1.5">
                <h1 className="text-4xl font-bold tracking-tight">Weltkarte</h1>
                <p className="text-muted">Wo du schon warst – und wohin es noch gehen soll.</p>
            </div>

            {error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {!error && (
                <div className="flex flex-col gap-5 rounded-card bg-surface p-4 shadow-card md:p-6">
                    {countries ? (
                        <WorldMap countries={countries} visitedCodes={VISITED_CODES} wishlistCodes={WISHLIST_CODES}/>
                    ) : (
                        <p className="flex h-56 items-center justify-center text-muted md:h-[28rem]">
                            Weltkarte wird geladen …
                        </p>
                    )}

                    {/* Legend as in the mockup */}
                    <ul className="flex flex-wrap gap-x-6 gap-y-2 text-sm text-ink-soft">
                        <li className="flex items-center gap-2">
                            <span className="size-3 rounded-full bg-accent"/>
                            Besucht
                        </li>
                        <li className="flex items-center gap-2">
                            <span className="size-3 rounded-full bg-wish"/>
                            Wunschziel
                        </li>
                        <li className="flex items-center gap-2">
                            <span className="size-3 rounded-full bg-land"/>
                            Noch offen
                        </li>
                    </ul>

                    {nothingColored && (
                        <p className="text-sm text-muted">
                            Noch ist nichts eingefärbt. Besuchte Länder erscheinen hier, sobald deine Reisen Stopps
                            haben; Wunschziele, sobald sie ein Land haben.
                        </p>
                    )}
                </div>
            )}
        </section>
    )
}

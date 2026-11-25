import {useState} from 'react'
import {useLanguage} from '../i18n/useLanguage'
import {getCountryName} from '../utils/countries'
import type {CountryTime} from '../utils/tripStats'

// Countries shown before "Alle anzeigen"
const FIRST_COUNTRIES = 8

// "Länder" on the statistics page: each country with its trips and days, most days first, and a bar for the days
// compared with the country with the most days. Long lists show the first countries and a button for the rest.
export default function CountryStats({countries}: Readonly<{ countries: CountryTime[] }>) {
    const {t, language} = useLanguage()
    const [showAll, setShowAll] = useState(false)
    const maxDays = Math.max(...countries.map((country) => country.days))
    const shown = showAll ? countries : countries.slice(0, FIRST_COUNTRIES)

    return (
        <section aria-labelledby="country-stats-title" className="flex flex-col gap-3 rounded-card bg-surface px-6 py-5 shadow-card">
            <h2 id="country-stats-title" className="text-[13px] font-semibold text-muted">{t.stats.countriesByDays}</h2>
            <ul className="flex flex-col gap-3">
                {shown.map((country) => (
                    <li key={country.code} className="flex flex-col gap-1.5">
                        <div className="flex flex-wrap items-baseline justify-between gap-x-3">
                            <span className="font-semibold text-ink">{getCountryName(country.code, language)}</span>
                            <span className="flex gap-x-1.5 text-sm text-ink-soft">
                                <span className="whitespace-nowrap">{t.common.trips(country.trips)}</span>
                                <span aria-hidden="true">·</span>
                                <span className="whitespace-nowrap">{t.common.days(country.days)}</span>
                            </span>
                        </div>
                        {/* The numbers are in the text; the bar only shows the proportions */}
                        <span aria-hidden="true" className="flex h-2">
                            <span className="rounded-r-sm bg-accent" style={{width: `${(country.days / maxDays) * 100}%`}}/>
                        </span>
                    </li>
                ))}
            </ul>
            {countries.length > FIRST_COUNTRIES && (
                <button
                    type="button"
                    onClick={() => setShowAll(!showAll)}
                    aria-expanded={showAll}
                    className="mt-1 min-h-11 w-fit cursor-pointer rounded-full border border-line-strong px-4 text-sm font-semibold text-ink hover:border-accent"
                >
                    {showAll ? t.stats.showLess : t.stats.showAllCountries(countries.length)}
                </button>
            )}
        </section>
    )
}

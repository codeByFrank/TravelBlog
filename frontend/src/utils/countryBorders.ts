import type {CountryCollection} from '../types/country'

// The country borders are a static file in frontend/public, not part of the backend API.
// The file is a smaller copy of the Natural Earth countries (1:110m, public domain).
const COUNTRY_BORDERS_URL = '/countries.geojson'

export async function loadCountryBorders(): Promise<CountryCollection> {
    const response = await fetch(COUNTRY_BORDERS_URL)
    if (!response.ok) {
        throw new Error(`Country borders could not be loaded: ${response.status}`)
    }
    return response.json()
}

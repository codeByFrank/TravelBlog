import type {Feature, FeatureCollection, Geometry} from 'geojson'

// What each country in public/countries.geojson knows about itself
export type CountryProperties = {
    // ISO 3166-1 alpha-2 code, e.g. "IS"; null for the few areas without an official code
    code: string | null
    // German name, e.g. "Island"
    name: string
    // Continent in English, e.g. "Europe"
    continent: string
}

export type Country = Feature<Geometry, CountryProperties>

export type CountryCollection = FeatureCollection<Geometry, CountryProperties>

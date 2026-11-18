// A result of the place search (PlaceResponse in the backend)
export type Place = {
    name: string // e.g. "Florenz"
    displayName: string // e.g. "Florenz, Toskana, Italien"
    latitude: number
    longitude: number
    countryCode: string // e.g. "IT"
}

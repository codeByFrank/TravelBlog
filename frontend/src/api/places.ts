import api from './client'
import type {Place} from '../types/place'

// Up to five places for a name; the backend asks Nominatim (OpenStreetMap)
export async function searchPlaces(query: string): Promise<Place[]> {
    const response = await api.get<Place[]>('/api/places', {params: {query}})
    return response.data
}

import api from './client'
import type {Trip} from '../types/trip'

// Trips of the logged-in user, newest first
export async function getTrips(): Promise<Trip[]> {
    const response = await api.get<Trip[]>('/api/trips')
    return response.data
}

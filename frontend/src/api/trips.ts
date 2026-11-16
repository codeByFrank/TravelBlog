import api from './client'
import type {Trip} from '../types/trip'

export type TripRequest = {
    title: string
    description: string
    startDate: string
    endDate: string
    coverColor: string | null
}

// Trips of the logged-in user, newest first
export async function getTrips(): Promise<Trip[]> {
    const response = await api.get<Trip[]>('/api/trips')
    return response.data
}

export async function createTrip(request: TripRequest): Promise<Trip> {
    const response = await api.post<Trip>('/api/trips', request)
    return response.data
}
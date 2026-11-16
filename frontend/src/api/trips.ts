import api from './client'

export type Trip = {
    id: string
    title: string
    description: string | null
    startDate: string // ISO date, e.g. "2024-05-12"
    endDate: string
    coverColor: string | null
}

export type TripRequest = {
    title: string
    description: string
    startDate: string
    endDate: string
    coverColor: string | null
}

export async function fetchTrips(): Promise<Trip[]> {
    const response = await api.get<Trip[]>('/api/trips')
    return response.data
}

export async function createTrip(request: TripRequest): Promise<Trip> {
    const response = await api.post<Trip>('/api/trips', request)
    return response.data
}
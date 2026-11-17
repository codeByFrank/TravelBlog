import api from './client'
import type {Trip, TripTag} from '../types/trip'

export type TripRequest = {
    title: string
    description: string
    startDate: string
    endDate: string
    coverColor: string | null
    tags: TripTag[]
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

export async function getTrip(id: string): Promise<Trip> {
    const response = await api.get<Trip>(`/api/trips/${id}`)
    return response.data
}

export async function updateTrip(id: string, request: TripRequest): Promise<Trip> {
    const response = await api.put<Trip>(`/api/trips/${id}`, request)
    return response.data
}

export async function deleteTrip(id: string): Promise<void> {
    await api.delete(`/api/trips/${id}`)
}
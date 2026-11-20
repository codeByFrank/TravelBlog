import api from './client'
import type {StopType, Trip} from '../types/trip'

export type StopRequest = {
    name: string
    latitude: number
    longitude: number
    countryCode: string
    date: string
    note: string
    type: StopType
}

// Every call returns the whole trip: the order of the stops and the distances can change

export async function addStop(tripId: string, request: StopRequest): Promise<Trip> {
    const response = await api.post<Trip>(`/api/trips/${tripId}/stops`, request)
    return response.data
}

export async function updateStop(tripId: string, stopId: string, request: StopRequest): Promise<Trip> {
    const response = await api.put<Trip>(`/api/trips/${tripId}/stops/${stopId}`, request)
    return response.data
}

export async function deleteStop(tripId: string, stopId: string): Promise<Trip> {
    const response = await api.delete<Trip>(`/api/trips/${tripId}/stops/${stopId}`)
    return response.data
}

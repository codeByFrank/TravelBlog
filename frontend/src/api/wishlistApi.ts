import api from './client'
import type {WishlistEntry, WishlistEntryRequest} from '../types/WishlistEntry'

// All HTTP calls for the wishlist. The JWT is added by the shared client.

export async function getWishlistEntries(): Promise<WishlistEntry[]> {
    const response = await api.get<WishlistEntry[]>('/api/wishlist')
    return response.data
}

export async function addWishlistEntry(request: WishlistEntryRequest): Promise<WishlistEntry> {
    const response = await api.post<WishlistEntry>('/api/wishlist', request)
    return response.data
}

export async function updateWishlistEntry(id: string, request: WishlistEntryRequest): Promise<WishlistEntry> {
    const response = await api.put<WishlistEntry>(`/api/wishlist/${id}`, request)
    return response.data
}

export async function deleteWishlistEntry(id: string): Promise<void> {
    await api.delete(`/api/wishlist/${id}`)
}

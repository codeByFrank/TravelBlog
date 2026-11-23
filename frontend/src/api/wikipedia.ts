import api from './client'

// The beginning of the Wikipedia article about a place (WikipediaResponse in the backend)
export type WikipediaArticle = {
    title: string // e.g. "Florenz"
    extract: string // the first sentences as plain text
    url: string // link to the whole article
}

// The article about a stop's place, or null if there is none (the backend answers 204 No Content)
export async function getWikipediaArticle(
    name: string,
    latitude: number,
    longitude: number,
): Promise<WikipediaArticle | null> {
    const response = await api.get<WikipediaArticle>('/api/wikipedia', {params: {name, latitude, longitude}})
    return response.status === 204 ? null : response.data
}

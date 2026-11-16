// A trip as returned by GET /api/trips (TripResponse in the backend)
export type Trip = {
    id: string
    title: string
    description: string | null
    startDate: string // ISO date, e.g. "2024-05-12"
    endDate: string
    coverColor: string | null
}

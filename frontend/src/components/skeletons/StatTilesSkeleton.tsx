import Skeleton from './Skeleton'

type StatTilesSkeletonProps = {
    // How many tiles the real row has, e.g. 5
    count: number
}

// Placeholder for a row of number tiles (TripStats), same grid and tile size
export default function StatTilesSkeleton({count}: Readonly<StatTilesSkeletonProps>) {
    const tiles = Array.from({length: count}, (_, index) => index)

    return (
        <div aria-hidden="true" className="grid grid-cols-[repeat(auto-fit,minmax(min(200px,100%),1fr))] gap-4">
            {tiles.map((tile) => (
                <div key={tile} className="flex flex-col gap-3 rounded-card bg-surface px-6 py-5 shadow-card">
                    <Skeleton className="h-3.5 w-24"/>
                    <Skeleton className="h-8 w-16"/>
                </div>
            ))}
        </div>
    )
}

import Skeleton from './Skeleton'

const ROWS = [1, 2, 3, 4]

// Placeholder for a short list with a name on the left and a detail on the right,
// e.g. "Besuchte Länder" and "Wunschziele" below the world map
export default function ListRowsSkeleton() {
    return (
        <div aria-hidden="true" className="flex flex-col">
            {ROWS.map((row) => (
                <div key={row} className="flex items-center justify-between gap-4 border-t border-line py-3.5">
                    <Skeleton className="h-4 w-28"/>
                    <Skeleton className="h-3.5 w-20"/>
                </div>
            ))}
        </div>
    )
}

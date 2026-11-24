import LoadingText from './LoadingText'
import Skeleton from './Skeleton'

const ENTRIES = [1, 2, 3]

// Placeholder for the wishlist entries while they load, in the same size as the real entries
// (the form next to the list is shown at once, it needs no data)
export default function WishlistSkeleton() {
    return (
        <div className="flex flex-col gap-4">
            <LoadingText text="Wunschliste wird geladen …"/>

            {ENTRIES.map((entry) => (
                <div key={entry} aria-hidden="true" className="flex flex-col gap-4 rounded-card bg-surface p-6 shadow-card">
                    <div className="flex items-start justify-between gap-3">
                        <div className="flex items-center gap-3">
                            <Skeleton className="h-6 w-36"/>
                            <Skeleton className="h-4 w-28"/>
                        </div>
                        <Skeleton className="h-6 w-28 rounded-full"/>
                    </div>
                    <Skeleton className="h-4 w-3/4"/>
                    <div className="flex items-center justify-between gap-3">
                        <Skeleton className="h-4 w-44"/>
                        <div className="flex gap-3">
                            <Skeleton className="h-10 w-28 rounded-full"/>
                            <Skeleton className="h-10 w-24 rounded-full"/>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    )
}

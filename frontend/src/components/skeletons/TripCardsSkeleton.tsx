import {useLanguage} from '../../i18n/useLanguage'
import LoadingText from './LoadingText'
import Skeleton from './Skeleton'

// Three cards are enough to fill the first row on a wide screen
const CARDS = [1, 2, 3]

// Placeholder for "Meine Reisen" while the trips load: the filter bar and trip cards in the same sizes as
// TripFilters and TripCard, so the page does not jump when the trips arrive
export default function TripCardsSkeleton() {
    const {t} = useLanguage()
    return (
        <div className="flex flex-col gap-7">
            <LoadingText text={t.trips.loading}/>

            {/* Search field and year buttons, then the tag buttons */}
            <div aria-hidden="true" className="flex flex-col gap-3">
                <div className="flex flex-wrap gap-3">
                    <Skeleton className="h-11 w-full rounded-full sm:w-105"/>
                    <Skeleton className="h-11 w-16 rounded-full"/>
                    <Skeleton className="h-11 w-18 rounded-full"/>
                    <Skeleton className="h-11 w-18 rounded-full"/>
                </div>
                <div className="flex flex-wrap gap-2">
                    <Skeleton className="h-11 w-16 rounded-full"/>
                    <Skeleton className="h-11 w-20 rounded-full"/>
                    <Skeleton className="h-11 w-24 rounded-full"/>
                </div>
            </div>

            <div aria-hidden="true" className="grid grid-cols-[repeat(auto-fill,minmax(min(300px,100%),1fr))] gap-6">
                {CARDS.map((card) => (
                    <div key={card} className="flex flex-col overflow-hidden rounded-card bg-surface shadow-card">
                        <Skeleton className="h-37.5 w-full rounded-none"/>
                        <div className="flex flex-col gap-3 px-5.5 pt-5 pb-5.5">
                            <Skeleton className="h-3.5 w-28"/>
                            <Skeleton className="h-6 w-3/4"/>
                            <Skeleton className="h-4 w-20"/>
                            <Skeleton className="h-4 w-full"/>
                            <Skeleton className="h-4 w-2/3"/>
                            <div className="mt-1 flex gap-2">
                                <Skeleton className="h-7 w-16 rounded-full"/>
                                <Skeleton className="h-7 w-16 rounded-full"/>
                                <Skeleton className="h-7 w-16 rounded-full"/>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

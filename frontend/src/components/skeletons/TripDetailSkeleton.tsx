import {useLanguage} from '../../i18n/useLanguage'
import LoadingText from './LoadingText'
import Skeleton from './Skeleton'

const STOPS = [1, 2, 3]

// Placeholder for the trip detail page while the trip loads: back link, title and the "Reiseverlauf" with the map
// on the left and the stops on the right, in the same grid as TripStops
export default function TripDetailSkeleton() {
    const {t} = useLanguage()
    return (
        <div className="flex flex-col gap-7">
            <LoadingText text={t.tripDetail.loading}/>

            <div aria-hidden="true" className="flex flex-col gap-7">
                <Skeleton className="h-5 w-28"/>

                {/* Dates, title, description and tags */}
                <div className="flex max-w-2xl flex-col gap-3">
                    <Skeleton className="h-4 w-32"/>
                    <Skeleton className="h-10 w-96 max-w-full"/>
                    <Skeleton className="h-4 w-80 max-w-full"/>
                    <div className="mt-1 flex gap-2">
                        <Skeleton className="h-7 w-20 rounded-full"/>
                        <Skeleton className="h-7 w-16 rounded-full"/>
                    </div>
                </div>

                <div className="flex items-center justify-between gap-4">
                    <Skeleton className="h-8 w-44"/>
                    <Skeleton className="h-12 w-44 rounded-full"/>
                </div>

                <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1.1fr)_minmax(0,1fr)]">
                    {/* The map card */}
                    <div className="rounded-card bg-surface p-4 shadow-card">
                        <Skeleton className="h-80 w-full rounded-xl lg:h-[30rem]"/>
                    </div>

                    {/* The stops */}
                    <div className="flex flex-col gap-4">
                        {STOPS.map((stop) => (
                            <div key={stop} className="flex flex-col gap-4 rounded-card bg-surface p-6 shadow-card">
                                <div className="flex items-center gap-3">
                                    <Skeleton className="size-8 rounded-full"/>
                                    <Skeleton className="h-6 w-28"/>
                                    <Skeleton className="h-4 w-36"/>
                                </div>
                                <Skeleton className="h-4 w-full"/>
                                <Skeleton className="h-20 w-20 rounded-xl"/>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}

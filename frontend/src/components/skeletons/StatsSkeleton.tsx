import {useLanguage} from '../../i18n/useLanguage'
import LoadingText from './LoadingText'
import Skeleton from './Skeleton'
import StatTilesSkeleton from './StatTilesSkeleton'

const RECORDS = [1, 2, 3]
const YEARS = [1, 2, 3, 4]

// Placeholder for the statistics page while the trips load: number tiles, records and the bars per year,
// in the same sizes as TripStats, TripRecords and YearStats
export default function StatsSkeleton() {
    const {t} = useLanguage()
    return (
        <div className="flex flex-col gap-7">
            <LoadingText text={t.stats.loading}/>

            <StatTilesSkeleton count={5}/>

            <div aria-hidden="true" className="flex flex-col gap-4">
                <Skeleton className="h-7 w-28"/>
                <div className="grid gap-4 md:grid-cols-3">
                    {RECORDS.map((record) => (
                        <div key={record} className="flex flex-col gap-3 rounded-card bg-surface px-6 py-5 shadow-card">
                            <Skeleton className="h-3.5 w-24"/>
                            <Skeleton className="h-8 w-28"/>
                            <Skeleton className="h-4 w-40"/>
                        </div>
                    ))}
                </div>
            </div>

            <div aria-hidden="true" className="flex flex-col gap-5 rounded-card bg-surface p-6 shadow-card">
                <Skeleton className="h-3.5 w-24"/>
                {YEARS.map((year) => (
                    <div key={year} className="flex items-center gap-6">
                        <Skeleton className="h-5 w-12"/>
                        <Skeleton className="h-4 w-56"/>
                        <Skeleton className="h-2 flex-1 rounded-full"/>
                    </div>
                ))}
            </div>
        </div>
    )
}

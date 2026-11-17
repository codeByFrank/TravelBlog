export type TripStat = {
    label: string
    value: string
}

// Row of white tiles above the trip list, e.g. "Reisen 6"; the page decides which numbers it shows
export default function TripStats({stats}: { stats: TripStat[] }) {
    return (
        <dl className="grid grid-cols-[repeat(auto-fit,minmax(min(200px,100%),1fr))] gap-4">
            {stats.map((stat) => (
                <div key={stat.label} className="flex flex-col gap-1 rounded-card bg-surface px-6 py-5 shadow-card">
                    <dt className="text-[13px] font-semibold text-muted">{stat.label}</dt>
                    <dd className="text-3xl font-bold">{stat.value}</dd>
                </div>
            ))}
        </dl>
    )
}

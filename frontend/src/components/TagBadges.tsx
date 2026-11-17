import {TRIP_TAG_LABELS, type TripTag} from '../types/trip'

// The tags of a trip as small light-blue badges; shows nothing if there are none
export default function TagBadges({tags}: Readonly<{ tags: TripTag[] }>) {
    if (tags.length === 0) return null
    return (
        <ul aria-label="Tags" className="flex flex-wrap gap-2">
            {tags.map((tag) => (
                <li key={tag} className="rounded-full bg-accent-soft px-3 py-1.5 text-[13px] font-semibold text-accent">
                    {TRIP_TAG_LABELS[tag]}
                </li>
            ))}
        </ul>
    )
}

type SkeletonProps = {
    // Size and shape, e.g. "h-6 w-40" or "h-37.5 w-full rounded-none"
    className?: string
}

// A grey block that stands in for content while it loads ("skeleton loading").
// It pulses gently, but only if the user has not asked the system for less motion (motion-safe).
// It is hidden from screen readers: each page skeleton says once what is loading.
export default function Skeleton({className = ''}: Readonly<SkeletonProps>) {
    // Slightly rounded corners, unless the caller chooses its own (e.g. rounded-full for a button)
    const rounding = className.includes('rounded-') ? '' : 'rounded-lg'
    return <div aria-hidden="true" className={`bg-line motion-safe:animate-pulse ${rounding} ${className}`}/>
}

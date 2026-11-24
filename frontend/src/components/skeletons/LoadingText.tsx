type LoadingTextProps = {
    // e.g. "Reisen werden geladen …"
    text: string
}

// Tells screen readers what is loading; sighted users see the skeleton instead.
// aria-live makes screen readers announce it without moving the focus.
export default function LoadingText({text}: Readonly<LoadingTextProps>) {
    return <p aria-live="polite" className="sr-only">{text}</p>
}

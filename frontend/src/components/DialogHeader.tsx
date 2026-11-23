type DialogHeaderProps = {
    titleId: string // id of the heading, for aria-labelledby of the <dialog>
    title: string
    onClose: () => void
}

// Title and close button of a long form dialog. It stays at the top while the dialog scrolls,
// so the dialog can be closed without scrolling down to "Abbrechen".
export default function DialogHeader({titleId, title, onClose}: Readonly<DialogHeaderProps>) {
    return (
        <div className="sticky top-0 z-10 flex items-center justify-between gap-4 bg-surface px-6 pt-6 pb-4 sm:px-8 sm:pt-8">
            <h2 id={titleId} className="text-2xl font-bold tracking-tight">{title}</h2>
            <button
                type="button"
                onClick={onClose}
                aria-label="Schließen"
                className="-mr-2 flex size-11 shrink-0 cursor-pointer items-center justify-center rounded-full text-muted outline-none hover:bg-page hover:text-ink focus-visible:ring-4 focus-visible:ring-accent-soft"
            >
                <svg
                    aria-hidden="true"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.5"
                    strokeLinecap="round"
                >
                    <path d="M6 6l12 12M18 6L6 18"/>
                </svg>
            </button>
        </div>
    )
}

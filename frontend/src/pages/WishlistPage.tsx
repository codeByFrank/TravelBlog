import {useEffect, useState} from 'react'
import {getErrorMessage} from '../api/client'
import {addWishlistEntry, deleteWishlistEntry, getWishlistEntries, updateWishlistEntry} from '../api/wishlistApi'
import ConfirmDialog from '../components/ConfirmDialog'
import WishlistForm from '../components/WishlistForm'
import {PRIORITY_LABELS, type Priority, type WishlistEntry, type WishlistEntryRequest} from '../types/WishlistEntry'

// Colors of the priority badge
const PRIORITY_STYLES: Record<Priority, string> = {
    HIGH: 'bg-accent text-white',
    MEDIUM: 'bg-accent-soft text-accent-dark',
    LOW: 'bg-page text-ink-soft',
}

// Sort order: the most wanted destinations come first
const PRIORITY_ORDER: Record<Priority, number> = {
    HIGH: 0,
    MEDIUM: 1,
    LOW: 2,
}

export default function WishlistPage() {
    const [entries, setEntries] = useState<WishlistEntry[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // The entry shown in the form; while this is null the form creates a new entry
    const [entryToEdit, setEntryToEdit] = useState<WishlistEntry | null>(null)
    // The entry the user wants to delete; the confirmation dialog is open while this is set
    const [entryToDelete, setEntryToDelete] = useState<WishlistEntry | null>(null)

    // Load the wishlist once when the page opens
    useEffect(() => {
        getWishlistEntries()
            .then((loadedEntries) => setEntries(loadedEntries))
            .catch((err) => setError(getErrorMessage(err, 'Die Wunschliste konnte nicht geladen werden.')))
            .finally(() => setLoading(false))
    }, [])

    async function handleAdd(request: WishlistEntryRequest) {
        const newEntry = await addWishlistEntry(request)
        setEntries([...entries, newEntry])
    }

    async function handleUpdate(id: string, request: WishlistEntryRequest) {
        const updatedEntry = await updateWishlistEntry(id, request)
        setEntries(entries.map((entry) => (entry.id === id ? updatedEntry : entry)))
        setEntryToEdit(null)
    }

    async function handleDelete(entry: WishlistEntry) {
        setEntryToDelete(null)
        setError('')
        try {
            await deleteWishlistEntry(entry.id)
            setEntries(entries.filter((existingEntry) => existingEntry.id !== entry.id))
            // The deleted entry may be the one that is open in the form
            if (entryToEdit?.id === entry.id) {
                setEntryToEdit(null)
            }
        } catch (err) {
            setError(getErrorMessage(err, 'Der Eintrag konnte nicht gelöscht werden.'))
        }
    }

    const sortedEntries = entries.toSorted(
        (a, b) => PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority],
    )

    return (
        <section className="flex flex-col gap-8">
            <div className="flex flex-col gap-1.5">
                <h1 className="text-4xl font-bold tracking-tight">Wunschliste</h1>
                <p className="text-muted">Orte, die du noch sehen möchtest – und warum.</p>
            </div>

            {error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {/* As in the mockup: the list on the left, the form next to it on the right */}
            <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1fr)_22rem]">
                <div className="flex flex-col gap-4">
                    {loading && <p className="text-muted">Wunschliste wird geladen …</p>}

                    {!loading && !error && entries.length === 0 && (
                        <p className="rounded-card bg-surface p-8 text-center text-muted shadow-card">
                            Deine Wunschliste ist noch leer. Füge dein erstes Wunschziel hinzu.
                        </p>
                    )}

                    <ul className="flex flex-col gap-4">
                        {sortedEntries.map((entry) => (
                            <li
                                key={entry.id}
                                className={`flex flex-col gap-3 rounded-card bg-surface p-6 shadow-card ${
                                    entryToEdit?.id === entry.id ? 'ring-2 ring-accent' : ''
                                }`}
                            >
                                <div className="flex items-start justify-between gap-3">
                                    <h2 className="text-xl font-bold tracking-tight">{entry.destination}</h2>
                                    <span
                                        className={`shrink-0 rounded-full px-3 py-1 text-xs font-semibold ${PRIORITY_STYLES[entry.priority]}`}
                                    >
                                        Priorität: {PRIORITY_LABELS[entry.priority].toLowerCase()}
                                    </span>
                                </div>
                                <p className="whitespace-pre-line text-ink-soft">{entry.reason}</p>
                                {/* As in the mockup: the best travel time on the left, the buttons on the right */}
                                <div className="flex flex-wrap items-center justify-between gap-3 pt-1">
                                    <p className="text-sm text-muted">
                                        {entry.bestTravelTime && `Beste Reisezeit: ${entry.bestTravelTime}`}
                                    </p>
                                    <div className="flex gap-3">
                                        <button
                                            type="button"
                                            onClick={() => setEntryToEdit(entry)}
                                            className="min-h-10 cursor-pointer rounded-full border border-line-strong bg-surface px-4 text-sm font-semibold text-accent hover:bg-accent-soft"
                                        >
                                            Bearbeiten
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => setEntryToDelete(entry)}
                                            className="min-h-10 cursor-pointer rounded-full border border-line-strong bg-surface px-4 text-sm font-semibold text-red-700 hover:bg-red-50"
                                        >
                                            Löschen
                                        </button>
                                    </div>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* The key makes React build a fresh form when another entry is chosen */}
                {entryToEdit ? (
                    <WishlistForm
                        key={entryToEdit.id}
                        entry={entryToEdit}
                        onSave={(request) => handleUpdate(entryToEdit.id, request)}
                        onCancel={() => setEntryToEdit(null)}
                    />
                ) : (
                    <WishlistForm key="new" onSave={handleAdd}/>
                )}
            </div>

            {entryToDelete && (
                <ConfirmDialog
                    title="Wunschziel löschen?"
                    message={`„${entryToDelete.destination}“ wird endgültig von deiner Wunschliste entfernt.`}
                    confirmLabel="Löschen"
                    onConfirm={() => void handleDelete(entryToDelete)}
                    onCancel={() => setEntryToDelete(null)}
                />
            )}
        </section>
    )
}

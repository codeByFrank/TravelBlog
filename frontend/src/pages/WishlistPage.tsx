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
    LOW: 'bg-page text-muted',
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

    // Which form is open: none, the form for a new entry, or the form for one existing entry
    const [showAddForm, setShowAddForm] = useState(false)
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
        setShowAddForm(false)
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
        } catch (err) {
            setError(getErrorMessage(err, 'Der Eintrag konnte nicht gelöscht werden.'))
        }
    }

    function openAddForm() {
        setEntryToEdit(null)
        setShowAddForm(true)
    }

    function openEditForm(entry: WishlistEntry) {
        setShowAddForm(false)
        setEntryToEdit(entry)
    }

    const sortedEntries = entries.toSorted(
        (a, b) => PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority],
    )

    return (
        <section className="flex flex-col gap-8">
            <div className="flex flex-wrap items-end justify-between gap-4">
                <div className="flex flex-col gap-1.5">
                    <h1 className="text-4xl font-bold tracking-tight">Wunschliste</h1>
                    <p className="text-muted">Orte, die du noch sehen möchtest – und warum.</p>
                </div>
                {!showAddForm && (
                    <button
                        type="button"
                        onClick={openAddForm}
                        className="min-h-12 cursor-pointer rounded-full bg-accent px-6 text-[15px] font-semibold text-white shadow-[0_6px_18px_rgba(31,111,191,0.25)] hover:bg-accent-dark"
                    >
                        Wunsch hinzufügen
                    </button>
                )}
            </div>

            {error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            {showAddForm && (
                <WishlistForm onSave={handleAdd} onCancel={() => setShowAddForm(false)}/>
            )}

            {loading && <p className="text-muted">Wunschliste wird geladen …</p>}

            {!loading && !error && entries.length === 0 && !showAddForm && (
                <p className="rounded-card bg-surface p-8 text-center text-muted shadow-card">
                    Deine Wunschliste ist noch leer. Füge dein erstes Reiseziel hinzu.
                </p>
            )}

            <ul className="grid gap-5 md:grid-cols-2">
                {sortedEntries.map((entry) =>
                    entryToEdit?.id === entry.id ? (
                        <li key={entry.id} className="md:col-span-2">
                            <WishlistForm
                                entry={entry}
                                onSave={(request) => handleUpdate(entry.id, request)}
                                onCancel={() => setEntryToEdit(null)}
                            />
                        </li>
                    ) : (
                        <li key={entry.id} className="flex flex-col gap-3 rounded-card bg-surface p-6 shadow-card">
                            <div className="flex items-start justify-between gap-3">
                                <h2 className="text-xl font-bold tracking-tight">{entry.destination}</h2>
                                <span
                                    className={`shrink-0 rounded-full px-3 py-1 text-xs font-semibold ${PRIORITY_STYLES[entry.priority]}`}
                                >
                                    {PRIORITY_LABELS[entry.priority]}
                                </span>
                            </div>
                            <p className="text-ink-soft">{entry.reason}</p>
                            <div className="mt-auto flex gap-4 pt-2 text-sm font-semibold">
                                <button
                                    type="button"
                                    onClick={() => openEditForm(entry)}
                                    className="cursor-pointer text-accent hover:text-accent-dark"
                                >
                                    Bearbeiten
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setEntryToDelete(entry)}
                                    className="cursor-pointer text-red-700 hover:text-red-800"
                                >
                                    Löschen
                                </button>
                            </div>
                        </li>
                    ),
                )}
            </ul>

            {entryToDelete && (
                <ConfirmDialog
                    title="Wunsch löschen?"
                    message={`„${entryToDelete.destination}“ wird endgültig von deiner Wunschliste entfernt.`}
                    confirmLabel="Löschen"
                    onConfirm={() => handleDelete(entryToDelete)}
                    onCancel={() => setEntryToDelete(null)}
                />
            )}
        </section>
    )
}

import {useState, type FormEvent} from 'react'
import axios from 'axios'
import {getErrorMessage} from '../api/client'
import FormField from './FormField'
import {PRIORITY_LABELS, type Priority, type WishlistEntry, type WishlistEntryRequest} from '../types/WishlistEntry'

type WishlistFormProps = {
    // The entry to edit; without it the form creates a new entry
    entry?: WishlistEntry
    // Saves the data; the page decides whether that is "create" or "update"
    onSave: (request: WishlistEntryRequest) => Promise<void>
    // Leaves the edit mode; not needed while creating
    onCancel?: () => void
}

// Field errors as the backend sends them: field name → message
type FieldErrors = Record<string, string>

// Form for creating and editing a wishlist entry
export default function WishlistForm({entry, onSave, onCancel}: WishlistFormProps) {
    const [destination, setDestination] = useState(entry?.destination ?? '')
    const [reason, setReason] = useState(entry?.reason ?? '')
    const [priority, setPriority] = useState<Priority>(entry?.priority ?? 'MEDIUM')
    const [error, setError] = useState('')
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
    const [submitting, setSubmitting] = useState(false)

    async function handleSubmit(event: FormEvent) {
        event.preventDefault()
        setError('')
        setFieldErrors({})
        setSubmitting(true)
        try {
            await onSave({destination, reason, priority})
            // After creating an entry the form stays open, so empty it for the next one
            if (!entry) {
                setDestination('')
                setReason('')
                setPriority('MEDIUM')
            }
        } catch (err) {
            // A validation error (400) carries an "errors" object with one message per field
            if (axios.isAxiosError(err) && err.response?.data?.errors) {
                setFieldErrors(err.response.data.errors)
            } else {
                setError(getErrorMessage(err, 'Speichern fehlgeschlagen.'))
            }
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <form onSubmit={handleSubmit} className="flex flex-col gap-5 rounded-card bg-surface p-6 shadow-card">
            <h2 className="text-xl font-bold tracking-tight">
                {entry ? 'Wunschziel bearbeiten' : 'Neues Wunschziel'}
            </h2>

            <FormField
                id="destination"
                label="Ort oder Land"
                placeholder="z. B. Island"
                value={destination}
                onChange={(e) => setDestination(e.target.value)}
                required
            />
            {fieldErrors.destination && (
                <p role="alert" className="-mt-3 text-sm text-red-700">Ort oder Land: {fieldErrors.destination}</p>
            )}

            <div className="flex flex-col gap-2">
                <label htmlFor="reason" className="text-sm font-semibold text-ink-soft">
                    Warum möchtest du dorthin?
                </label>
                <textarea
                    id="reason"
                    rows={4}
                    placeholder="z. B. Nordlichter sehen"
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    required
                    className="rounded-xl border border-line-strong bg-surface px-4 py-3 text-[15px] text-ink outline-none placeholder:text-muted/70 focus:border-accent focus:ring-4 focus:ring-accent-soft"
                />
            </div>
            {fieldErrors.reason && (
                <p role="alert" className="-mt-3 text-sm text-red-700">Grund: {fieldErrors.reason}</p>
            )}

            <div className="flex flex-col gap-2">
                <label htmlFor="priority" className="text-sm font-semibold text-ink-soft">
                    Priorität
                </label>
                <select
                    id="priority"
                    value={priority}
                    onChange={(e) => setPriority(e.target.value as Priority)}
                    className="min-h-12 rounded-xl border border-line-strong bg-surface px-4 text-[15px] text-ink outline-none focus:border-accent focus:ring-4 focus:ring-accent-soft"
                >
                    <option value="HIGH">{PRIORITY_LABELS.HIGH}</option>
                    <option value="MEDIUM">{PRIORITY_LABELS.MEDIUM}</option>
                    <option value="LOW">{PRIORITY_LABELS.LOW}</option>
                </select>
            </div>

            {error && (
                <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                </p>
            )}

            <button
                type="submit"
                disabled={submitting}
                className="min-h-12 cursor-pointer rounded-full bg-accent px-6 text-[15px] font-semibold text-white shadow-[0_6px_18px_rgba(31,111,191,0.25)] hover:bg-accent-dark disabled:cursor-default disabled:opacity-60"
            >
                {submitting && 'Wird gespeichert …'}
                {!submitting && (entry ? 'Änderungen speichern' : 'Zur Wunschliste hinzufügen')}
            </button>

            {onCancel && (
                <button
                    type="button"
                    onClick={onCancel}
                    className="min-h-12 cursor-pointer rounded-full border border-line-strong bg-surface px-6 text-[15px] font-semibold text-ink-soft hover:bg-page"
                >
                    Abbrechen
                </button>
            )}
        </form>
    )
}

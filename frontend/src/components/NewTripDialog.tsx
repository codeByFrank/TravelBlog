import {useEffect, useRef, useState, type FormEvent} from 'react'
import {getErrorMessage, getFieldErrors} from '../api/client'
import {createTrip} from '../api/trips'
import type {Trip} from '../types/trip'
import {COVER_COLORS, DEFAULT_COVER_COLOR} from '../coverColors'
import FormField from './FormField'

type NewTripDialogProps = {
    open: boolean
    onClose: () => void
    onCreated: (trip: Trip) => void
}

const EMPTY_FORM = {title: '', description: '', startDate: '', endDate: '', coverColor: DEFAULT_COVER_COLOR}

export default function NewTripDialog({open, onClose, onCreated}: NewTripDialogProps) {
    const dialogRef = useRef<HTMLDialogElement>(null)
    const [form, setForm] = useState(EMPTY_FORM)
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)

    // The native <dialog> handles focus, Esc and the backdrop
    useEffect(() => {
        const dialog = dialogRef.current
        if (!dialog) return
        if (open && !dialog.open) {
            setForm(EMPTY_FORM)
            setFieldErrors({})
            setError('')
            dialog.showModal()
        }
        if (!open && dialog.open) {
            dialog.close()
        }
    }, [open])

    function update(field: keyof typeof EMPTY_FORM, value: string) {
        setForm({...form, [field]: value})
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault()
        setFieldErrors({})
        setError('')

        if (form.startDate && form.endDate && form.endDate < form.startDate) {
            setFieldErrors({endDate: 'Das Enddatum darf nicht vor dem Startdatum liegen.'})
            return
        }

        setSubmitting(true)
        try {
            const trip = await createTrip({...form, title: form.title.trim()})
            onCreated(trip)
        } catch (err) {
            setFieldErrors(getFieldErrors(err))
            setError(getErrorMessage(err, 'Die Reise konnte nicht gespeichert werden.'))
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <dialog
            ref={dialogRef}
            onClose={onClose}
            aria-labelledby="new-trip-title"
            className="m-auto w-[calc(100%-2rem)] max-w-lg rounded-card bg-surface p-0 text-ink shadow-card backdrop:bg-ink/40"
        >
            <form onSubmit={handleSubmit} className="flex flex-col gap-5 p-6 sm:p-8">
                <h2 id="new-trip-title" className="text-2xl font-bold tracking-tight">Neue Reise</h2>

                <FormField
                    id="trip-title"
                    label="Titel"
                    placeholder="z. B. Italien im Frühling"
                    value={form.title}
                    onChange={(e) => update('title', e.target.value)}
                    error={fieldErrors.title}
                    required
                />

                <div className="grid gap-5 sm:grid-cols-2">
                    <FormField
                        id="trip-start"
                        label="Von"
                        type="date"
                        value={form.startDate}
                        onChange={(e) => update('startDate', e.target.value)}
                        error={fieldErrors.startDate}
                        required
                    />
                    <FormField
                        id="trip-end"
                        label="Bis"
                        type="date"
                        min={form.startDate || undefined}
                        value={form.endDate}
                        onChange={(e) => update('endDate', e.target.value)}
                        error={fieldErrors.endDate}
                        required
                    />
                </div>

                <div className="flex flex-col gap-2">
                    <label htmlFor="trip-description" className="text-sm font-semibold text-ink-soft">
                        Beschreibung <span className="font-normal text-muted">(optional)</span>
                    </label>
                    <textarea
                        id="trip-description"
                        rows={3}
                        value={form.description}
                        onChange={(e) => update('description', e.target.value)}
                        className="rounded-xl border border-line-strong bg-surface px-4 py-3 text-[15px] text-ink outline-none focus:border-accent focus:ring-4 focus:ring-accent-soft"
                    />
                </div>

                <fieldset className="flex flex-col gap-2">
                    <legend className="mb-2 text-sm font-semibold text-ink-soft">Farbe der Titelkarte</legend>
                    <div className="flex flex-wrap gap-3">
                        {COVER_COLORS.map((color) => (
                            <label key={color.value} title={color.label} className="cursor-pointer">
                                <input
                                    type="radio"
                                    name="coverColor"
                                    value={color.value}
                                    checked={form.coverColor === color.value}
                                    onChange={() => update('coverColor', color.value)}
                                    className="peer sr-only"
                                />
                                <span
                                    className="block size-10 rounded-full border border-line-strong peer-checked:ring-4 peer-checked:ring-accent peer-focus-visible:outline-2 peer-focus-visible:outline-offset-4 peer-focus-visible:outline-accent"
                                    style={{backgroundColor: color.value}}
                                />
                                <span className="sr-only">{color.label}</span>
                            </label>
                        ))}
                    </div>
                </fieldset>

                {error && (
                    <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                        {error}
                    </p>
                )}

                <div className="mt-2 flex justify-end gap-3">
                    <button
                        type="button"
                        onClick={onClose}
                        className="min-h-12 cursor-pointer rounded-full px-5 text-[15px] font-semibold text-muted hover:text-ink"
                    >
                        Abbrechen
                    </button>
                    <button
                        type="submit"
                        disabled={submitting}
                        className="min-h-12 cursor-pointer rounded-full bg-accent px-6 text-[15px] font-semibold text-white shadow-[0_6px_18px_rgba(31,111,191,0.25)] hover:bg-accent-dark disabled:cursor-default disabled:opacity-60"
                    >
                        {submitting ? 'Wird gespeichert …' : 'Speichern'}
                    </button>
                </div>
            </form>
        </dialog>
    )
}
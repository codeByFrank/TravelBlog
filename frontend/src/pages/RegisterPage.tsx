import {useState, type SubmitEvent} from 'react'
import {Link, useNavigate} from 'react-router-dom'
import api, {getErrorMessage} from '../api/client'
import AuthCard from '../components/AuthCard'
import FormField from '../components/FormField'

export default function RegisterPage() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)
    const navigate = useNavigate()

    async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
        event.preventDefault()
        setError('')

        if (username.trim().length < 3 || username.trim().length > 30) {
            setError('Der Benutzername muss 3 bis 30 Zeichen lang sein.')
            return
        }
        if (password.length < 8) {
            setError('Das Passwort muss mindestens 8 Zeichen lang sein.')
            return
        }

        setSubmitting(true)
        try {
            await api.post('/api/auth/register', {username, password})
            void navigate('/login')
        } catch (err) {
            setError(getErrorMessage(err, 'Registrierung fehlgeschlagen.'))
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <AuthCard
            title="Konto erstellen"
            subtitle="Alle Reisen an einem Ort – mit Karte, Fotos und Eindrücken."
            footer={
                <>
                    Schon ein Konto?{' '}
                    <Link to="/login" className="font-semibold text-accent hover:text-accent-dark">
                        Anmelden
                    </Link>
                </>
            }
        >
            <form onSubmit={(event) => void handleSubmit(event)} className="flex flex-col gap-5">
                <FormField
                    id="username"
                    label="Benutzername"
                    placeholder="3 bis 30 Zeichen"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    autoComplete="username"
                    required
                />
                <FormField
                    id="password"
                    label="Passwort"
                    type="password"
                    placeholder="Mindestens 8 Zeichen"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    autoComplete="new-password"
                    required
                />

                {error && (
                    <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                        {error}
                    </p>
                )}

                <button
                    type="submit"
                    disabled={submitting}
                    className="mt-2 min-h-12 cursor-pointer rounded-full bg-accent px-6 text-[15px] font-semibold text-white shadow-[0_6px_18px_rgba(31,111,191,0.25)] hover:bg-accent-dark disabled:cursor-default disabled:opacity-60"
                >
                    {submitting ? 'Wird erstellt …' : 'Registrieren'}
                </button>
            </form>
        </AuthCard>
    )
}
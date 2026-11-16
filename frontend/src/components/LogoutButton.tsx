import { useNavigate } from 'react-router-dom'
import { TOKEN_KEY } from '../api/client'

export default function LogoutButton() {
    const navigate = useNavigate()

    function logout() {
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem('username')
        navigate('/login')
    }

    return (
        <button
            type="button"
            onClick={logout}
            className="flex min-h-11 cursor-pointer items-center rounded-full px-4 text-[15px] font-semibold text-muted hover:text-ink"
        >
            Abmelden
        </button>

    )
}
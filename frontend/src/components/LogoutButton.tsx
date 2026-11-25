import { useNavigate } from 'react-router-dom'
import { TOKEN_KEY } from '../api/client'
import { useLanguage } from '../i18n/useLanguage'

export default function LogoutButton() {
    const navigate = useNavigate()
    const { t } = useLanguage()

    function logout() {
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem('username')
        void navigate('/login')
    }

    return (
        <button
            type="button"
            onClick={logout}
            className="flex min-h-11 cursor-pointer items-center rounded-full px-4 text-[15px] font-semibold text-muted hover:text-ink"
        >
            {t.nav.logout}
        </button>
    )
}

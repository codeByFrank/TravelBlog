import { Link } from 'react-router-dom'
import { useLanguage } from '../i18n/useLanguage'
import { HOME_PATH } from '../navigation'

export default function NotFoundPage() {
  const { t } = useLanguage()
  return (
    <main className="mx-auto flex max-w-md flex-col gap-4 px-6 py-16">
      <h1 className="text-4xl font-bold tracking-tight">{t.notFound.title}</h1>
      <p className="text-muted">{t.notFound.text}</p>
      <Link to={HOME_PATH} className="font-semibold text-accent hover:text-accent-dark">
        {t.notFound.back}
      </Link>
    </main>
  )
}

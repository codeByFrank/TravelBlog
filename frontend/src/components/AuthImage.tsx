import {useEffect, useState} from 'react'
import {loadPhotoUrl} from '../api/photos'
import {useLanguage} from '../i18n/useLanguage'

type AuthImageProps = {
    photoId: string
    alt: string
    className?: string
}

// A stored photo; it needs the token, so it is loaded with axios instead of a plain <img src>
export default function AuthImage({photoId, alt, className = ''}: Readonly<AuthImageProps>) {
    const {t} = useLanguage()
    const [url, setUrl] = useState<string | null>(null)
    const [failed, setFailed] = useState(false)

    useEffect(() => {
        let cancelled = false
        loadPhotoUrl(photoId)
            .then((loaded) => {
                if (!cancelled) setUrl(loaded)
            })
            .catch(() => {
                if (!cancelled) setFailed(true)
            })
        return () => {
            cancelled = true
        }
    }, [photoId])

    if (!url) {
        // Grey placeholder of the same size; it pulses while the photo loads (skeleton) and stays still if it is missing
        const pulse = failed ? '' : 'motion-safe:animate-pulse'
        return (
            <div
                role="img"
                aria-label={failed ? t.photos.unavailable : alt}
                className={`bg-chip ${pulse} ${className}`}
            />
        )
    }
    return <img src={url} alt={alt} className={`object-cover ${className}`}/>
}
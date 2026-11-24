import {useEffect, useState} from 'react'
import {getWikipediaArticle, type WikipediaArticle} from '../api/wikipedia'
import {useLanguage} from '../i18n/useLanguage'
import type {Stop} from '../types/trip'

type WikipediaInfoProps = {
    stop: Stop
}

// idle: not loaded yet; found / notFound / failed: the answer
type State =
    | { status: 'idle' }
    | { status: 'loading' }
    | { status: 'found'; article: WikipediaArticle }
    | { status: 'notFound' }
    | { status: 'failed' }

// "Aus Wikipedia" in a stop's card (as in the mockup): the first sentences of the article about the place and a link
// to it. Overnight stops load it right away; for day trips and places on the way a button loads it, so the list
// stays short. Without an article (or when Wikipedia cannot be reached) an overnight stop shows nothing.
export default function WikipediaInfo({stop}: Readonly<WikipediaInfoProps>) {
    const {t} = useLanguage()
    const loadsRightAway = stop.type === 'OVERNIGHT'
    // An overnight stop starts loading right away. TripStops gives this component a key made of the place and the
    // type, so a changed stop gets a new component with a fresh state.
    const [state, setState] = useState<State>(loadsRightAway ? {status: 'loading'} : {status: 'idle'})
    const {name, latitude, longitude} = stop

    async function load() {
        setState({status: 'loading'})
        try {
            const article = await getWikipediaArticle(name, latitude, longitude)
            setState(article ? {status: 'found', article} : {status: 'notFound'})
        } catch {
            setState({status: 'failed'})
        }
    }

    useEffect(() => {
        if (!loadsRightAway) {
            return
        }
        let cancelled = false // ignore the answer if the card was closed in the meantime
        getWikipediaArticle(name, latitude, longitude)
            .then((article) => {
                if (!cancelled) setState(article ? {status: 'found', article} : {status: 'notFound'})
            })
            .catch(() => {
                if (!cancelled) setState({status: 'failed'})
            })
        return () => {
            cancelled = true
        }
    }, [loadsRightAway, name, latitude, longitude])

    if (state.status === 'found') {
        const {article} = state
        return (
            <section aria-label={t.wikipedia.label(article.title)} className="flex flex-col gap-2 rounded-xl bg-page px-4 py-3.5">
                <h4 className="text-xs font-bold uppercase tracking-wide text-muted">{t.wikipedia.heading}</h4>
                <p className="line-clamp-4 text-[15px] text-ink-soft">{article.extract}</p>
                <div className="flex flex-wrap items-baseline justify-between gap-x-4 gap-y-1">
                    <a
                        href={article.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-[15px] font-semibold text-accent underline hover:text-accent-dark"
                    >
                        <span>{t.wikipedia.readMore}</span>
                        <span className="sr-only">{t.common.opensInNewTab}</span>
                    </a>
                    {/* The article text is licensed under CC BY-SA, which asks for the source and the license */}
                    <span className="text-xs text-muted">
                        <span>{t.wikipedia.credit}</span>
                        <a href={t.wikipedia.licenseUrl} target="_blank" rel="noopener noreferrer" className="underline hover:text-ink">
                            CC BY-SA 4.0
                        </a>
                    </span>
                </div>
            </section>
        )
    }

    if (loadsRightAway) {
        // Nothing while loading or without an article, so the card does not jump or show an empty box
        return null
    }

    if (state.status === 'notFound' || state.status === 'failed') {
        return (
            <p className="text-sm text-muted">
                {state.status === 'notFound'
                    ? t.wikipedia.notFound
                    : t.wikipedia.unavailable}
            </p>
        )
    }

    return (
        <button
            type="button"
            onClick={() => void load()}
            disabled={state.status === 'loading'}
            className="w-fit cursor-pointer text-sm font-semibold text-accent hover:text-accent-dark disabled:cursor-default disabled:opacity-60"
        >
            {state.status === 'loading' ? t.wikipedia.loading : t.wikipedia.more(name)}
        </button>
    )
}

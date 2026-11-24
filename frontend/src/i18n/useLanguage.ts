import {useContext} from 'react'
import {LanguageContext} from './LanguageContext'

// The chosen language and its texts, e.g. const {t, language} = useLanguage(); <h1>{t.trips.title}</h1>
export function useLanguage() {
    return useContext(LanguageContext)
}

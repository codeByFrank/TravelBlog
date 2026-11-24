import {useEffect, useMemo, useState, type ReactNode} from 'react'
import {LanguageContext} from './LanguageContext'
import {getLanguage, saveLanguage, type Language} from './language'
import {texts} from './texts'

// Keeps the chosen language for the whole app (main.tsx). Changing it shows every page in the new language at once.
export default function LanguageProvider({children}: Readonly<{ children: ReactNode }>) {
    const [language, setLanguageState] = useState<Language>(getLanguage)

    // Screen readers and the browser's spell check use the language of the page
    useEffect(() => {
        document.documentElement.lang = language
    }, [language])

    // The same object as long as the language stays the same, so the pages are not drawn again for nothing
    const value = useMemo(() => {
        function setLanguage(newLanguage: Language) {
            saveLanguage(newLanguage) // first, so the next API request already sends the new language
            setLanguageState(newLanguage)
        }

        return {language, setLanguage, t: texts[language]}
    }, [language])

    return <LanguageContext value={value}>{children}</LanguageContext>
}

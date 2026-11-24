import {createContext} from 'react'
import type {Language} from './language'
import {texts, type Texts} from './texts'

export type LanguageContextValue = {
    language: Language
    setLanguage: (language: Language) => void
    t: Texts // the texts in the chosen language
}

// Filled by LanguageProvider; the default is only used by a component outside of it
export const LanguageContext = createContext<LanguageContextValue>({
    language: 'de',
    setLanguage: () => {
        // Without a LanguageProvider there is nothing to change
    },
    t: texts.de,
})

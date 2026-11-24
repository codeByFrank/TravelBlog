// The languages of the user interface. German is the default; the app name TravelBlog is never translated.
export type Language = 'de' | 'en'

export const LANGUAGES: Language[] = ['de', 'en']

// The chosen language is kept in the browser, like the login token
export const LANGUAGE_KEY = 'language'

// Locales for dates and numbers: "12.–20. Mai 2024" and "1.234 km" in German, "12–20 May 2024" and "1,234 km" in
// English. British English, because its dates have the day before the month, like the German ones.
export const LOCALES: Record<Language, string> = {
    de: 'de-DE',
    en: 'en-GB',
}

function isLanguage(value: string | null): value is Language {
    return value === 'de' || value === 'en'
}

// The language the user chose; on the first visit the language of the browser: English for an English browser,
// German for all others. The API client also sends it to the backend (Accept-Language), so its messages,
// the place search and Wikipedia use the same language.
export function getLanguage(): Language {
    const saved = localStorage.getItem(LANGUAGE_KEY)
    if (isLanguage(saved)) {
        return saved
    }
    return navigator.language.toLowerCase().startsWith('en') ? 'en' : 'de'
}

export function saveLanguage(language: Language) {
    localStorage.setItem(LANGUAGE_KEY, language)
}

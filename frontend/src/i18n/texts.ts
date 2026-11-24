import {de, type Texts} from './de'
import {en} from './en'
import type {Language} from './language'

// The texts of each language. Components use useLanguage(); helper functions outside of components
// (e.g. the trip search in utils/tripFilters.ts) get the language as a parameter and look the texts up here.
export const texts: Record<Language, Texts> = {de, en}

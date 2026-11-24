import type {Texts} from './i18n/de'

// The colors a trip's cover can have; the names are in the texts (i18n/de.ts → coverColors)
export const COVER_COLORS: { value: string, nameKey: keyof Texts['coverColors'] }[] = [
    {value: '#DCEAF7', nameKey: 'SKY'},
    {value: '#EAE6F5', nameKey: 'LAVENDER'},
    {value: '#E0EFEA', nameKey: 'MINT'},
    {value: '#F6EADF', nameKey: 'PEACH'},
    {value: '#F7E1E6', nameKey: 'PINK'},
    {value: '#F3EFD9', nameKey: 'SAND'},
]

export const DEFAULT_COVER_COLOR = COVER_COLORS[0].value

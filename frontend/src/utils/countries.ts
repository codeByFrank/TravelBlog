import type {Language} from '../i18n/language'
import {texts} from '../i18n/texts'

// The continents, as keys of the texts (texts.continents), e.g. "EUROPE" → "Europa" / "Europe"
export type Continent = keyof typeof texts.de.continents

// All countries a wishlist entry can have, sorted by their German name.
// The codes are ISO 3166-1 alpha-2, e.g. "IS" for Island.
// The list comes from the same Natural Earth data as the world map (without Antarctica),
// so names and codes are the same everywhere in the app. English names come from the browser (see getCountryName).
export type CountryInfo = {
    code: string
    name: string // German name
    continent: Continent
}

export const COUNTRIES: CountryInfo[] = [
    {code: 'AF', name: 'Afghanistan', continent: 'ASIA'},
    {code: 'EG', name: 'Ägypten', continent: 'AFRICA'},
    {code: 'AL', name: 'Albanien', continent: 'EUROPE'},
    {code: 'DZ', name: 'Algerien', continent: 'AFRICA'},
    {code: 'AO', name: 'Angola', continent: 'AFRICA'},
    {code: 'GQ', name: 'Äquatorialguinea', continent: 'AFRICA'},
    {code: 'AR', name: 'Argentinien', continent: 'SOUTH_AMERICA'},
    {code: 'AM', name: 'Armenien', continent: 'ASIA'},
    {code: 'AZ', name: 'Aserbaidschan', continent: 'ASIA'},
    {code: 'ET', name: 'Äthiopien', continent: 'AFRICA'},
    {code: 'AU', name: 'Australien', continent: 'OCEANIA'},
    {code: 'BS', name: 'Bahamas', continent: 'NORTH_AMERICA'},
    {code: 'BD', name: 'Bangladesch', continent: 'ASIA'},
    {code: 'BY', name: 'Belarus', continent: 'EUROPE'},
    {code: 'BE', name: 'Belgien', continent: 'EUROPE'},
    {code: 'BZ', name: 'Belize', continent: 'NORTH_AMERICA'},
    {code: 'BJ', name: 'Benin', continent: 'AFRICA'},
    {code: 'BT', name: 'Bhutan', continent: 'ASIA'},
    {code: 'BO', name: 'Bolivien', continent: 'SOUTH_AMERICA'},
    {code: 'BA', name: 'Bosnien und Herzegowina', continent: 'EUROPE'},
    {code: 'BW', name: 'Botswana', continent: 'AFRICA'},
    {code: 'BR', name: 'Brasilien', continent: 'SOUTH_AMERICA'},
    {code: 'BN', name: 'Brunei', continent: 'ASIA'},
    {code: 'BG', name: 'Bulgarien', continent: 'EUROPE'},
    {code: 'BF', name: 'Burkina Faso', continent: 'AFRICA'},
    {code: 'BI', name: 'Burundi', continent: 'AFRICA'},
    {code: 'CL', name: 'Chile', continent: 'SOUTH_AMERICA'},
    {code: 'CR', name: 'Costa Rica', continent: 'NORTH_AMERICA'},
    {code: 'DK', name: 'Dänemark', continent: 'EUROPE'},
    {code: 'CD', name: 'Demokratische Republik Kongo', continent: 'AFRICA'},
    {code: 'DE', name: 'Deutschland', continent: 'EUROPE'},
    {code: 'DO', name: 'Dominikanische Republik', continent: 'NORTH_AMERICA'},
    {code: 'DJ', name: 'Dschibuti', continent: 'AFRICA'},
    {code: 'EC', name: 'Ecuador', continent: 'SOUTH_AMERICA'},
    {code: 'SV', name: 'El Salvador', continent: 'NORTH_AMERICA'},
    {code: 'CI', name: 'Elfenbeinküste', continent: 'AFRICA'},
    {code: 'ER', name: 'Eritrea', continent: 'AFRICA'},
    {code: 'EE', name: 'Estland', continent: 'EUROPE'},
    {code: 'SZ', name: 'Eswatini', continent: 'AFRICA'},
    {code: 'FK', name: 'Falklandinseln', continent: 'SOUTH_AMERICA'},
    {code: 'FJ', name: 'Fidschi', continent: 'OCEANIA'},
    {code: 'FI', name: 'Finnland', continent: 'EUROPE'},
    {code: 'FR', name: 'Frankreich', continent: 'EUROPE'},
    {code: 'TF', name: 'Französische Süd- und Antarktisgebiete', continent: 'OPEN_OCEAN'},
    {code: 'GA', name: 'Gabun', continent: 'AFRICA'},
    {code: 'GM', name: 'Gambia', continent: 'AFRICA'},
    {code: 'GE', name: 'Georgien', continent: 'ASIA'},
    {code: 'GH', name: 'Ghana', continent: 'AFRICA'},
    {code: 'GR', name: 'Griechenland', continent: 'EUROPE'},
    {code: 'GL', name: 'Grönland', continent: 'NORTH_AMERICA'},
    {code: 'GT', name: 'Guatemala', continent: 'NORTH_AMERICA'},
    {code: 'GN', name: 'Guinea', continent: 'AFRICA'},
    {code: 'GW', name: 'Guinea-Bissau', continent: 'AFRICA'},
    {code: 'GY', name: 'Guyana', continent: 'SOUTH_AMERICA'},
    {code: 'HT', name: 'Haiti', continent: 'NORTH_AMERICA'},
    {code: 'HN', name: 'Honduras', continent: 'NORTH_AMERICA'},
    {code: 'IN', name: 'Indien', continent: 'ASIA'},
    {code: 'ID', name: 'Indonesien', continent: 'ASIA'},
    {code: 'IQ', name: 'Irak', continent: 'ASIA'},
    {code: 'IR', name: 'Iran', continent: 'ASIA'},
    {code: 'IE', name: 'Irland', continent: 'EUROPE'},
    {code: 'IS', name: 'Island', continent: 'EUROPE'},
    {code: 'IL', name: 'Israel', continent: 'ASIA'},
    {code: 'IT', name: 'Italien', continent: 'EUROPE'},
    {code: 'JM', name: 'Jamaika', continent: 'NORTH_AMERICA'},
    {code: 'JP', name: 'Japan', continent: 'ASIA'},
    {code: 'YE', name: 'Jemen', continent: 'ASIA'},
    {code: 'JO', name: 'Jordanien', continent: 'ASIA'},
    {code: 'KH', name: 'Kambodscha', continent: 'ASIA'},
    {code: 'CM', name: 'Kamerun', continent: 'AFRICA'},
    {code: 'CA', name: 'Kanada', continent: 'NORTH_AMERICA'},
    {code: 'KZ', name: 'Kasachstan', continent: 'ASIA'},
    {code: 'QA', name: 'Katar', continent: 'ASIA'},
    {code: 'KE', name: 'Kenia', continent: 'AFRICA'},
    {code: 'KG', name: 'Kirgisistan', continent: 'ASIA'},
    {code: 'CO', name: 'Kolumbien', continent: 'SOUTH_AMERICA'},
    {code: 'XK', name: 'Kosovo', continent: 'EUROPE'},
    {code: 'HR', name: 'Kroatien', continent: 'EUROPE'},
    {code: 'CU', name: 'Kuba', continent: 'NORTH_AMERICA'},
    {code: 'KW', name: 'Kuwait', continent: 'ASIA'},
    {code: 'LA', name: 'Laos', continent: 'ASIA'},
    {code: 'LS', name: 'Lesotho', continent: 'AFRICA'},
    {code: 'LV', name: 'Lettland', continent: 'EUROPE'},
    {code: 'LB', name: 'Libanon', continent: 'ASIA'},
    {code: 'LR', name: 'Liberia', continent: 'AFRICA'},
    {code: 'LY', name: 'Libyen', continent: 'AFRICA'},
    {code: 'LT', name: 'Litauen', continent: 'EUROPE'},
    {code: 'LU', name: 'Luxemburg', continent: 'EUROPE'},
    {code: 'MG', name: 'Madagaskar', continent: 'AFRICA'},
    {code: 'MW', name: 'Malawi', continent: 'AFRICA'},
    {code: 'MY', name: 'Malaysia', continent: 'ASIA'},
    {code: 'ML', name: 'Mali', continent: 'AFRICA'},
    {code: 'MA', name: 'Marokko', continent: 'AFRICA'},
    {code: 'MR', name: 'Mauretanien', continent: 'AFRICA'},
    {code: 'MX', name: 'Mexiko', continent: 'NORTH_AMERICA'},
    {code: 'MN', name: 'Mongolei', continent: 'ASIA'},
    {code: 'ME', name: 'Montenegro', continent: 'EUROPE'},
    {code: 'MZ', name: 'Mosambik', continent: 'AFRICA'},
    {code: 'MM', name: 'Myanmar', continent: 'ASIA'},
    {code: 'NA', name: 'Namibia', continent: 'AFRICA'},
    {code: 'NP', name: 'Nepal', continent: 'ASIA'},
    {code: 'NC', name: 'Neukaledonien', continent: 'OCEANIA'},
    {code: 'NZ', name: 'Neuseeland', continent: 'OCEANIA'},
    {code: 'NI', name: 'Nicaragua', continent: 'NORTH_AMERICA'},
    {code: 'NL', name: 'Niederlande', continent: 'EUROPE'},
    {code: 'NE', name: 'Niger', continent: 'AFRICA'},
    {code: 'NG', name: 'Nigeria', continent: 'AFRICA'},
    {code: 'KP', name: 'Nordkorea', continent: 'ASIA'},
    {code: 'MK', name: 'Nordmazedonien', continent: 'EUROPE'},
    {code: 'NO', name: 'Norwegen', continent: 'EUROPE'},
    {code: 'OM', name: 'Oman', continent: 'ASIA'},
    {code: 'AT', name: 'Österreich', continent: 'EUROPE'},
    {code: 'TL', name: 'Osttimor', continent: 'ASIA'},
    {code: 'PK', name: 'Pakistan', continent: 'ASIA'},
    {code: 'PS', name: 'Palästina', continent: 'ASIA'},
    {code: 'PA', name: 'Panama', continent: 'NORTH_AMERICA'},
    {code: 'PG', name: 'Papua-Neuguinea', continent: 'OCEANIA'},
    {code: 'PY', name: 'Paraguay', continent: 'SOUTH_AMERICA'},
    {code: 'PE', name: 'Peru', continent: 'SOUTH_AMERICA'},
    {code: 'PH', name: 'Philippinen', continent: 'ASIA'},
    {code: 'PL', name: 'Polen', continent: 'EUROPE'},
    {code: 'PT', name: 'Portugal', continent: 'EUROPE'},
    {code: 'PR', name: 'Puerto Rico', continent: 'NORTH_AMERICA'},
    {code: 'TW', name: 'Republik China', continent: 'ASIA'},
    {code: 'CG', name: 'Republik Kongo', continent: 'AFRICA'},
    {code: 'MD', name: 'Republik Moldau', continent: 'EUROPE'},
    {code: 'CY', name: 'Republik Zypern', continent: 'ASIA'},
    {code: 'RW', name: 'Ruanda', continent: 'AFRICA'},
    {code: 'RO', name: 'Rumänien', continent: 'EUROPE'},
    {code: 'RU', name: 'Russland', continent: 'EUROPE'},
    {code: 'SB', name: 'Salomonen', continent: 'OCEANIA'},
    {code: 'ZM', name: 'Sambia', continent: 'AFRICA'},
    {code: 'SA', name: 'Saudi-Arabien', continent: 'ASIA'},
    {code: 'SE', name: 'Schweden', continent: 'EUROPE'},
    {code: 'CH', name: 'Schweiz', continent: 'EUROPE'},
    {code: 'SN', name: 'Senegal', continent: 'AFRICA'},
    {code: 'RS', name: 'Serbien', continent: 'EUROPE'},
    {code: 'SL', name: 'Sierra Leone', continent: 'AFRICA'},
    {code: 'ZW', name: 'Simbabwe', continent: 'AFRICA'},
    {code: 'SK', name: 'Slowakei', continent: 'EUROPE'},
    {code: 'SI', name: 'Slowenien', continent: 'EUROPE'},
    {code: 'SO', name: 'Somalia', continent: 'AFRICA'},
    {code: 'ES', name: 'Spanien', continent: 'EUROPE'},
    {code: 'LK', name: 'Sri Lanka', continent: 'ASIA'},
    {code: 'ZA', name: 'Südafrika', continent: 'AFRICA'},
    {code: 'SD', name: 'Sudan', continent: 'AFRICA'},
    {code: 'KR', name: 'Südkorea', continent: 'ASIA'},
    {code: 'SS', name: 'Südsudan', continent: 'AFRICA'},
    {code: 'SR', name: 'Suriname', continent: 'SOUTH_AMERICA'},
    {code: 'SY', name: 'Syrien', continent: 'ASIA'},
    {code: 'TJ', name: 'Tadschikistan', continent: 'ASIA'},
    {code: 'TZ', name: 'Tansania', continent: 'AFRICA'},
    {code: 'TH', name: 'Thailand', continent: 'ASIA'},
    {code: 'TG', name: 'Togo', continent: 'AFRICA'},
    {code: 'TT', name: 'Trinidad und Tobago', continent: 'NORTH_AMERICA'},
    {code: 'TD', name: 'Tschad', continent: 'AFRICA'},
    {code: 'CZ', name: 'Tschechien', continent: 'EUROPE'},
    {code: 'TN', name: 'Tunesien', continent: 'AFRICA'},
    {code: 'TR', name: 'Türkei', continent: 'ASIA'},
    {code: 'TM', name: 'Turkmenistan', continent: 'ASIA'},
    {code: 'UG', name: 'Uganda', continent: 'AFRICA'},
    {code: 'UA', name: 'Ukraine', continent: 'EUROPE'},
    {code: 'HU', name: 'Ungarn', continent: 'EUROPE'},
    {code: 'UY', name: 'Uruguay', continent: 'SOUTH_AMERICA'},
    {code: 'UZ', name: 'Usbekistan', continent: 'ASIA'},
    {code: 'VU', name: 'Vanuatu', continent: 'OCEANIA'},
    {code: 'VE', name: 'Venezuela', continent: 'SOUTH_AMERICA'},
    {code: 'AE', name: 'Vereinigte Arabische Emirate', continent: 'ASIA'},
    {code: 'US', name: 'Vereinigte Staaten', continent: 'NORTH_AMERICA'},
    {code: 'GB', name: 'Vereinigtes Königreich', continent: 'EUROPE'},
    {code: 'VN', name: 'Vietnam', continent: 'ASIA'},
    {code: 'CN', name: 'Volksrepublik China', continent: 'ASIA'},
    {code: 'EH', name: 'Westsahara', continent: 'AFRICA'},
    {code: 'CF', name: 'Zentralafrikanische Republik', continent: 'AFRICA'},
]

// Finds a country by its code; undefined for an unknown code
export function findCountry(code: string): CountryInfo | undefined {
    return COUNTRIES.find((country) => country.code === code)
}

// The browser knows the names of all countries in both languages, also of small ones like Malta or Andorra
const browserCountryNames: Record<Language, Intl.DisplayNames> = {
    de: new Intl.DisplayNames(['de'], {type: 'region'}),
    en: new Intl.DisplayNames(['en'], {type: 'region'}),
}

// English names the browser writes in an unusual way, e.g. "Congo - Kinshasa" or "Myanmar (Burma)"
const ENGLISH_NAMES: Record<string, string> = {
    BA: 'Bosnia and Herzegovina',
    CD: 'DR Congo',
    CG: 'Republic of the Congo',
    MM: 'Myanmar',
    PS: 'Palestine',
    TT: 'Trinidad and Tobago',
}

// Name of a country in the language of the user interface, e.g. "IS" → "Island" / "Iceland".
// German names come from the list above; countries that are not in it (too small for the world map, e.g. "MT")
// and all English names come from the browser. A code the browser does not know either is shown as it is.
export function getCountryName(code: string, language: Language): string {
    const country = findCountry(code)
    if (language === 'de' && country) {
        return country.name
    }
    if (language === 'en' && ENGLISH_NAMES[code]) {
        return ENGLISH_NAMES[code]
    }

    try {
        return browserCountryNames[language].of(code) ?? code
    } catch {
        // of() throws for codes that are not two letters
        return code
    }
}

// The names of some countries in one text, e.g. ["AR", "CL"] → "Argentinien, Chile", ["IS"] → "Island".
// A wishlist entry without its own place or region is named like this.
export function getCountryNames(codes: string[], language: Language): string {
    const names = codes.map((code) => getCountryName(code, language))
    return names.join(', ')
}

// All countries of the list with their name in the language, sorted by that name (for a select box)
export function getSortedCountries(language: Language): { code: string, name: string }[] {
    return COUNTRIES
        .map((country) => ({code: country.code, name: getCountryName(country.code, language)}))
        .toSorted((a, b) => a.name.localeCompare(b.name, language))
}

// True if a wishlist entry is only named after its countries, e.g. "Island" for ["IS"].
// Such a name was made automatically, in the language of that moment, so both languages are checked.
export function isNamedAfterCountries(destination: string, codes: string[]): boolean {
    return codes.length > 0
        && (destination === getCountryNames(codes, 'de') || destination === getCountryNames(codes, 'en'))
}

// The name of a wishlist entry to show: an entry named after its countries shows them in the language of the
// user interface ("Island" → "Iceland" in English); every other name stays as the user wrote it.
export function getDestinationName(destination: string, codes: string[], language: Language): string {
    return isNamedAfterCountries(destination, codes) ? getCountryNames(codes, language) : destination
}

// The continents of some countries, each only once, e.g. ["AR", "CL"] → ["Südamerika"] / ["South America"]
export function getContinents(codes: string[], language: Language): string[] {
    const continents: Continent[] = []
    for (const code of codes) {
        const continent = findCountry(code)?.continent
        if (continent && !continents.includes(continent)) {
            continents.push(continent)
        }
    }
    return continents.map((continent) => texts[language].continents[continent])
}

// Short text for a wishlist card, as in the mockup:
// ["AR", "CL"] → "Argentinien, Chile · Südamerika", ["NZ"] → "Neuseeland · Ozeanien", [] → ""
export function describeCountries(codes: string[], language: Language): string {
    if (codes.length === 0) {
        return ''
    }

    const names = getCountryNames(codes, language)
    const continents = getContinents(codes, language)

    if (continents.length === 0) {
        return names
    }
    return `${names} · ${continents.join(', ')}`
}

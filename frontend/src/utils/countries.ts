// All countries a wishlist entry can have, sorted by their German name.
// The codes are ISO 3166-1 alpha-2, e.g. "IS" for Island.
// The list comes from the same Natural Earth data as the world map (without Antarctica),
// so names and codes are the same everywhere in the app.
export type CountryInfo = {
    code: string
    name: string
    continent: string
}

export const COUNTRIES: CountryInfo[] = [
    {code: 'AF', name: 'Afghanistan', continent: 'Asien'},
    {code: 'EG', name: 'Ägypten', continent: 'Afrika'},
    {code: 'AL', name: 'Albanien', continent: 'Europa'},
    {code: 'DZ', name: 'Algerien', continent: 'Afrika'},
    {code: 'AO', name: 'Angola', continent: 'Afrika'},
    {code: 'GQ', name: 'Äquatorialguinea', continent: 'Afrika'},
    {code: 'AR', name: 'Argentinien', continent: 'Südamerika'},
    {code: 'AM', name: 'Armenien', continent: 'Asien'},
    {code: 'AZ', name: 'Aserbaidschan', continent: 'Asien'},
    {code: 'ET', name: 'Äthiopien', continent: 'Afrika'},
    {code: 'AU', name: 'Australien', continent: 'Ozeanien'},
    {code: 'BS', name: 'Bahamas', continent: 'Nordamerika'},
    {code: 'BD', name: 'Bangladesch', continent: 'Asien'},
    {code: 'BY', name: 'Belarus', continent: 'Europa'},
    {code: 'BE', name: 'Belgien', continent: 'Europa'},
    {code: 'BZ', name: 'Belize', continent: 'Nordamerika'},
    {code: 'BJ', name: 'Benin', continent: 'Afrika'},
    {code: 'BT', name: 'Bhutan', continent: 'Asien'},
    {code: 'BO', name: 'Bolivien', continent: 'Südamerika'},
    {code: 'BA', name: 'Bosnien und Herzegowina', continent: 'Europa'},
    {code: 'BW', name: 'Botswana', continent: 'Afrika'},
    {code: 'BR', name: 'Brasilien', continent: 'Südamerika'},
    {code: 'BN', name: 'Brunei', continent: 'Asien'},
    {code: 'BG', name: 'Bulgarien', continent: 'Europa'},
    {code: 'BF', name: 'Burkina Faso', continent: 'Afrika'},
    {code: 'BI', name: 'Burundi', continent: 'Afrika'},
    {code: 'CL', name: 'Chile', continent: 'Südamerika'},
    {code: 'CR', name: 'Costa Rica', continent: 'Nordamerika'},
    {code: 'DK', name: 'Dänemark', continent: 'Europa'},
    {code: 'CD', name: 'Demokratische Republik Kongo', continent: 'Afrika'},
    {code: 'DE', name: 'Deutschland', continent: 'Europa'},
    {code: 'DO', name: 'Dominikanische Republik', continent: 'Nordamerika'},
    {code: 'DJ', name: 'Dschibuti', continent: 'Afrika'},
    {code: 'EC', name: 'Ecuador', continent: 'Südamerika'},
    {code: 'SV', name: 'El Salvador', continent: 'Nordamerika'},
    {code: 'CI', name: 'Elfenbeinküste', continent: 'Afrika'},
    {code: 'ER', name: 'Eritrea', continent: 'Afrika'},
    {code: 'EE', name: 'Estland', continent: 'Europa'},
    {code: 'SZ', name: 'Eswatini', continent: 'Afrika'},
    {code: 'FK', name: 'Falklandinseln', continent: 'Südamerika'},
    {code: 'FJ', name: 'Fidschi', continent: 'Ozeanien'},
    {code: 'FI', name: 'Finnland', continent: 'Europa'},
    {code: 'FR', name: 'Frankreich', continent: 'Europa'},
    {code: 'TF', name: 'Französische Süd- und Antarktisgebiete', continent: 'Ozeane'},
    {code: 'GA', name: 'Gabun', continent: 'Afrika'},
    {code: 'GM', name: 'Gambia', continent: 'Afrika'},
    {code: 'GE', name: 'Georgien', continent: 'Asien'},
    {code: 'GH', name: 'Ghana', continent: 'Afrika'},
    {code: 'GR', name: 'Griechenland', continent: 'Europa'},
    {code: 'GL', name: 'Grönland', continent: 'Nordamerika'},
    {code: 'GT', name: 'Guatemala', continent: 'Nordamerika'},
    {code: 'GN', name: 'Guinea', continent: 'Afrika'},
    {code: 'GW', name: 'Guinea-Bissau', continent: 'Afrika'},
    {code: 'GY', name: 'Guyana', continent: 'Südamerika'},
    {code: 'HT', name: 'Haiti', continent: 'Nordamerika'},
    {code: 'HN', name: 'Honduras', continent: 'Nordamerika'},
    {code: 'IN', name: 'Indien', continent: 'Asien'},
    {code: 'ID', name: 'Indonesien', continent: 'Asien'},
    {code: 'IQ', name: 'Irak', continent: 'Asien'},
    {code: 'IR', name: 'Iran', continent: 'Asien'},
    {code: 'IE', name: 'Irland', continent: 'Europa'},
    {code: 'IS', name: 'Island', continent: 'Europa'},
    {code: 'IL', name: 'Israel', continent: 'Asien'},
    {code: 'IT', name: 'Italien', continent: 'Europa'},
    {code: 'JM', name: 'Jamaika', continent: 'Nordamerika'},
    {code: 'JP', name: 'Japan', continent: 'Asien'},
    {code: 'YE', name: 'Jemen', continent: 'Asien'},
    {code: 'JO', name: 'Jordanien', continent: 'Asien'},
    {code: 'KH', name: 'Kambodscha', continent: 'Asien'},
    {code: 'CM', name: 'Kamerun', continent: 'Afrika'},
    {code: 'CA', name: 'Kanada', continent: 'Nordamerika'},
    {code: 'KZ', name: 'Kasachstan', continent: 'Asien'},
    {code: 'QA', name: 'Katar', continent: 'Asien'},
    {code: 'KE', name: 'Kenia', continent: 'Afrika'},
    {code: 'KG', name: 'Kirgisistan', continent: 'Asien'},
    {code: 'CO', name: 'Kolumbien', continent: 'Südamerika'},
    {code: 'XK', name: 'Kosovo', continent: 'Europa'},
    {code: 'HR', name: 'Kroatien', continent: 'Europa'},
    {code: 'CU', name: 'Kuba', continent: 'Nordamerika'},
    {code: 'KW', name: 'Kuwait', continent: 'Asien'},
    {code: 'LA', name: 'Laos', continent: 'Asien'},
    {code: 'LS', name: 'Lesotho', continent: 'Afrika'},
    {code: 'LV', name: 'Lettland', continent: 'Europa'},
    {code: 'LB', name: 'Libanon', continent: 'Asien'},
    {code: 'LR', name: 'Liberia', continent: 'Afrika'},
    {code: 'LY', name: 'Libyen', continent: 'Afrika'},
    {code: 'LT', name: 'Litauen', continent: 'Europa'},
    {code: 'LU', name: 'Luxemburg', continent: 'Europa'},
    {code: 'MG', name: 'Madagaskar', continent: 'Afrika'},
    {code: 'MW', name: 'Malawi', continent: 'Afrika'},
    {code: 'MY', name: 'Malaysia', continent: 'Asien'},
    {code: 'ML', name: 'Mali', continent: 'Afrika'},
    {code: 'MA', name: 'Marokko', continent: 'Afrika'},
    {code: 'MR', name: 'Mauretanien', continent: 'Afrika'},
    {code: 'MX', name: 'Mexiko', continent: 'Nordamerika'},
    {code: 'MN', name: 'Mongolei', continent: 'Asien'},
    {code: 'ME', name: 'Montenegro', continent: 'Europa'},
    {code: 'MZ', name: 'Mosambik', continent: 'Afrika'},
    {code: 'MM', name: 'Myanmar', continent: 'Asien'},
    {code: 'NA', name: 'Namibia', continent: 'Afrika'},
    {code: 'NP', name: 'Nepal', continent: 'Asien'},
    {code: 'NC', name: 'Neukaledonien', continent: 'Ozeanien'},
    {code: 'NZ', name: 'Neuseeland', continent: 'Ozeanien'},
    {code: 'NI', name: 'Nicaragua', continent: 'Nordamerika'},
    {code: 'NL', name: 'Niederlande', continent: 'Europa'},
    {code: 'NE', name: 'Niger', continent: 'Afrika'},
    {code: 'NG', name: 'Nigeria', continent: 'Afrika'},
    {code: 'KP', name: 'Nordkorea', continent: 'Asien'},
    {code: 'MK', name: 'Nordmazedonien', continent: 'Europa'},
    {code: 'NO', name: 'Norwegen', continent: 'Europa'},
    {code: 'OM', name: 'Oman', continent: 'Asien'},
    {code: 'AT', name: 'Österreich', continent: 'Europa'},
    {code: 'TL', name: 'Osttimor', continent: 'Asien'},
    {code: 'PK', name: 'Pakistan', continent: 'Asien'},
    {code: 'PS', name: 'Palästina', continent: 'Asien'},
    {code: 'PA', name: 'Panama', continent: 'Nordamerika'},
    {code: 'PG', name: 'Papua-Neuguinea', continent: 'Ozeanien'},
    {code: 'PY', name: 'Paraguay', continent: 'Südamerika'},
    {code: 'PE', name: 'Peru', continent: 'Südamerika'},
    {code: 'PH', name: 'Philippinen', continent: 'Asien'},
    {code: 'PL', name: 'Polen', continent: 'Europa'},
    {code: 'PT', name: 'Portugal', continent: 'Europa'},
    {code: 'PR', name: 'Puerto Rico', continent: 'Nordamerika'},
    {code: 'TW', name: 'Republik China', continent: 'Asien'},
    {code: 'CG', name: 'Republik Kongo', continent: 'Afrika'},
    {code: 'MD', name: 'Republik Moldau', continent: 'Europa'},
    {code: 'CY', name: 'Republik Zypern', continent: 'Asien'},
    {code: 'RW', name: 'Ruanda', continent: 'Afrika'},
    {code: 'RO', name: 'Rumänien', continent: 'Europa'},
    {code: 'RU', name: 'Russland', continent: 'Europa'},
    {code: 'SB', name: 'Salomonen', continent: 'Ozeanien'},
    {code: 'ZM', name: 'Sambia', continent: 'Afrika'},
    {code: 'SA', name: 'Saudi-Arabien', continent: 'Asien'},
    {code: 'SE', name: 'Schweden', continent: 'Europa'},
    {code: 'CH', name: 'Schweiz', continent: 'Europa'},
    {code: 'SN', name: 'Senegal', continent: 'Afrika'},
    {code: 'RS', name: 'Serbien', continent: 'Europa'},
    {code: 'SL', name: 'Sierra Leone', continent: 'Afrika'},
    {code: 'ZW', name: 'Simbabwe', continent: 'Afrika'},
    {code: 'SK', name: 'Slowakei', continent: 'Europa'},
    {code: 'SI', name: 'Slowenien', continent: 'Europa'},
    {code: 'SO', name: 'Somalia', continent: 'Afrika'},
    {code: 'ES', name: 'Spanien', continent: 'Europa'},
    {code: 'LK', name: 'Sri Lanka', continent: 'Asien'},
    {code: 'ZA', name: 'Südafrika', continent: 'Afrika'},
    {code: 'SD', name: 'Sudan', continent: 'Afrika'},
    {code: 'KR', name: 'Südkorea', continent: 'Asien'},
    {code: 'SS', name: 'Südsudan', continent: 'Afrika'},
    {code: 'SR', name: 'Suriname', continent: 'Südamerika'},
    {code: 'SY', name: 'Syrien', continent: 'Asien'},
    {code: 'TJ', name: 'Tadschikistan', continent: 'Asien'},
    {code: 'TZ', name: 'Tansania', continent: 'Afrika'},
    {code: 'TH', name: 'Thailand', continent: 'Asien'},
    {code: 'TG', name: 'Togo', continent: 'Afrika'},
    {code: 'TT', name: 'Trinidad und Tobago', continent: 'Nordamerika'},
    {code: 'TD', name: 'Tschad', continent: 'Afrika'},
    {code: 'CZ', name: 'Tschechien', continent: 'Europa'},
    {code: 'TN', name: 'Tunesien', continent: 'Afrika'},
    {code: 'TR', name: 'Türkei', continent: 'Asien'},
    {code: 'TM', name: 'Turkmenistan', continent: 'Asien'},
    {code: 'UG', name: 'Uganda', continent: 'Afrika'},
    {code: 'UA', name: 'Ukraine', continent: 'Europa'},
    {code: 'HU', name: 'Ungarn', continent: 'Europa'},
    {code: 'UY', name: 'Uruguay', continent: 'Südamerika'},
    {code: 'UZ', name: 'Usbekistan', continent: 'Asien'},
    {code: 'VU', name: 'Vanuatu', continent: 'Ozeanien'},
    {code: 'VE', name: 'Venezuela', continent: 'Südamerika'},
    {code: 'AE', name: 'Vereinigte Arabische Emirate', continent: 'Asien'},
    {code: 'US', name: 'Vereinigte Staaten', continent: 'Nordamerika'},
    {code: 'GB', name: 'Vereinigtes Königreich', continent: 'Europa'},
    {code: 'VN', name: 'Vietnam', continent: 'Asien'},
    {code: 'CN', name: 'Volksrepublik China', continent: 'Asien'},
    {code: 'EH', name: 'Westsahara', continent: 'Afrika'},
    {code: 'CF', name: 'Zentralafrikanische Republik', continent: 'Afrika'},
]

// Finds a country by its code; undefined for an unknown code
export function findCountry(code: string): CountryInfo | undefined {
    return COUNTRIES.find((country) => country.code === code)
}

// Finds a country by its German name, ignoring upper and lower case, e.g. "island" → Island; undefined if none fits
export function findCountryByName(name: string): CountryInfo | undefined {
    const searchedName = name.trim().toLowerCase()
    return COUNTRIES.find((country) => country.name.toLowerCase() === searchedName)
}

// German name of a country, e.g. "IS" → "Island"; an unknown code is shown as it is
export function getCountryName(code: string): string {
    return findCountry(code)?.name ?? code
}

// The continents of some countries, each only once, e.g. ["AR", "CL"] → ["Südamerika"]
export function getContinents(codes: string[]): string[] {
    const continents: string[] = []
    for (const code of codes) {
        const continent = findCountry(code)?.continent
        if (continent && !continents.includes(continent)) {
            continents.push(continent)
        }
    }
    return continents
}

// Short text for a wishlist card, as in the mockup:
// ["AR", "CL"] → "Argentinien, Chile · Südamerika", ["NZ"] → "Neuseeland · Ozeanien", [] → ""
export function describeCountries(codes: string[]): string {
    if (codes.length === 0) {
        return ''
    }

    const names = codes.map((code) => getCountryName(code))
    const continents = getContinents(codes)

    if (continents.length === 0) {
        return names.join(', ')
    }
    return `${names.join(', ')} · ${continents.join(', ')}`
}

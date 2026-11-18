import {GeoJSON, MapContainer} from 'react-leaflet'
import type {Layer, PathOptions} from 'leaflet'
import 'leaflet/dist/leaflet.css'
import type {Country, CountryCollection} from '../types/country'

type WorldMapProps = {
    // All countries with their borders
    countries: CountryCollection
    // Country codes the user has visited, e.g. ["IT", "PT"]
    visitedCodes: string[]
    // Country codes on the user's wishlist, e.g. ["IS", "PE"]
    wishlistCodes: string[]
    // The visited country whose trips are shown below the map, e.g. "IT"; null if none is chosen
    selectedCode: string | null
    // Called when the user clicks a visited country; clicks on other countries do nothing
    onVisitedCountryClick: (code: string) => void
}

// Shows the German name when the mouse is over a country.
// Outside the component, because it does not use props or state.
function addCountryName(country: Country, layer: Layer) {
    layer.bindTooltip(country.properties.name, {sticky: true})
}

// Shows the world as colored countries, without a street map below:
// visited countries in blue, wishlist destinations in orange, all others light grey (as in the mockup).
export default function WorldMap({
    countries,
    visitedCodes,
    wishlistCodes,
    selectedCode,
    onVisitedCountryClick,
}: Readonly<WorldMapProps>) {
    // Leaflet asks this function for the look of each country.
    // The Tailwind classes work because Leaflet draws the countries as SVG paths.
    function styleCountry(country: Country | undefined): PathOptions {
        const code = country?.properties.code ?? ''

        // Leaflet shows the hand cursor over every country, but only visited countries can be clicked.
        // The ! makes the Tailwind class stronger than the rule in leaflet.css.
        let fillClass = 'fill-land cursor-default!'
        if (code === selectedCode) {
            fillClass = 'fill-accent-dark'
        } else if (visitedCodes.includes(code)) {
            fillClass = 'fill-accent'
        } else if (wishlistCodes.includes(code)) {
            fillClass = 'fill-wish cursor-default!'
        }

        return {
            // outline-none: without it the browser draws a black frame around a country after a click
            className: `${fillClass} stroke-surface outline-none`,
            weight: 0.8,
            fillOpacity: 1,
        }
    }

    // Leaflet calls this once for each country when it draws the map:
    // the name as a tooltip for all countries, and a click handler for the visited ones
    function prepareCountry(country: Country, layer: Layer) {
        addCountryName(country, layer)

        // A few areas have no code (null); they can never be visited
        const code = country.properties.code
        if (code && visitedCodes.includes(code)) {
            layer.on('click', () => onVisitedCountryClick(code))
        }
    }

    // react-leaflet draws a GeoJSON layer only once. The key changes when the codes or the chosen country change,
    // so React builds the layer again with the new colors and click handlers.
    const colorKey = `${visitedCodes.join(',')}|${wishlistCodes.join(',')}|${selectedCode ?? ''}`

    return (
        <MapContainer
            // Leaflet picks the zoom so that everything from New Zealand to Norway fits into the box
            bounds={[[-50, -170], [78, 180]]}
            // Allows zoom steps in quarters, so the world fills the box better
            zoomSnap={0.25}
            // Zoom 0 is needed on phones, where the box is narrow
            minZoom={0}
            maxZoom={6}
            maxBounds={[[-90, -200], [90, 200]]}
            // The page scrolls with the mouse wheel; zooming works with the buttons or a double click
            scrollWheelZoom={false}
            // Inline style, because leaflet.css would otherwise paint the background grey
            style={{background: 'transparent'}}
            className="h-56 w-full md:h-[28rem]"
        >
            <GeoJSON
                key={colorKey}
                data={countries}
                style={styleCountry}
                onEachFeature={prepareCountry}
                attribution="Grenzen: Natural Earth"
            />
        </MapContainer>
    )
}

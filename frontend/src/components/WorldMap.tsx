import {GeoJSON, MapContainer, useMapEvents} from 'react-leaflet'
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
    // Called when the user clicks a visited or wished country; clicks on grey countries do nothing
    onCountryClick: (code: string) => void
}

// Shows the German name when the mouse is over a country.
// Outside the component, because it does not use props or state.
function addCountryName(country: Country, layer: Layer) {
    layer.bindTooltip(country.properties.name, {sticky: true})
}

// Zooming with the mouse wheel is off at first, so that the page scrolls normally when the mouse passes the map.
// After a click into the map the mouse wheel zooms; when the mouse leaves the map, it is switched off again.
// It has to be a component inside MapContainer, because only there useMapEvents knows the map.
function ScrollZoomOnClick() {
    const map = useMapEvents({
        click() {
            map.scrollWheelZoom.enable()
        },
        mouseout() {
            map.scrollWheelZoom.disable()
        },
    })
    // Draws nothing; it only listens to the map
    return null
}

// Shows the world as colored countries, without a street map below:
// visited countries in blue, wishlist destinations in orange, all others light grey (as in the mockup).
// Blue and orange countries can be clicked; the chosen one gets a darker shade.
export default function WorldMap({
    countries,
    visitedCodes,
    wishlistCodes,
    selectedCode,
    onCountryClick,
}: Readonly<WorldMapProps>) {
    // Leaflet asks this function for the look of each country.
    // The Tailwind classes work because Leaflet draws the countries as SVG paths.
    function styleCountry(country: Country | undefined): PathOptions {
        const code = country?.properties.code ?? ''

        // Blue wins when a country is visited and on the wishlist; the chosen country gets the darker shade.
        // Leaflet shows the hand cursor over every country, but grey countries cannot be clicked.
        // The ! makes the Tailwind class stronger than the rule in leaflet.css.
        const isSelected = code === selectedCode
        let fillClass = 'fill-land cursor-default!'
        if (visitedCodes.includes(code)) {
            fillClass = isSelected ? 'fill-accent-dark' : 'fill-accent'
        } else if (wishlistCodes.includes(code)) {
            fillClass = isSelected ? 'fill-wish-dark' : 'fill-wish'
        }

        return {
            // outline-none: without it the browser draws a black frame around a country after a click
            className: `${fillClass} stroke-surface outline-none`,
            weight: 0.8,
            fillOpacity: 1,
        }
    }

    // Leaflet calls this once for each country when it draws the map:
    // the name as a tooltip for all countries, and a click handler for the visited and wished ones
    function prepareCountry(country: Country, layer: Layer) {
        addCountryName(country, layer)

        // A few areas have no code (null); they can never be visited or wished
        const code = country.properties.code
        if (code && (visitedCodes.includes(code) || wishlistCodes.includes(code))) {
            layer.on('click', () => onCountryClick(code))
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
            // The page scrolls with the mouse wheel; zooming works with the buttons, a double click,
            // or the mouse wheel after a click into the map (ScrollZoomOnClick below)
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
            <ScrollZoomOnClick/>
        </MapContainer>
    )
}

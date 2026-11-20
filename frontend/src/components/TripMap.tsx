import {useEffect} from 'react'
import {MapContainer, Marker, Polyline, Popup, TileLayer, Tooltip, useMap} from 'react-leaflet'
import {divIcon, latLngBounds, type LatLngTuple} from 'leaflet'
import 'leaflet/dist/leaflet.css'
import type {Stop} from '../types/trip'
import {getCountryName} from '../utils/countries'
import {formatDayMonth} from '../utils/dates'
import {formatKm} from '../utils/tripFilters'
import MapWheelZoom from './MapWheelZoom'

type TripMapProps = {
    stops: Stop[] // in the order of the visit; at least one
    routeLengthKm: number // sum of the straight-line distances between the stops, from the backend
    className?: string // e.g. to make the card sticky on wide screens
}

// Space around the stops when the map fits them in, and the closest zoom, so that two places in one city
// still show the surroundings
const FIT_OPTIONS = {padding: [48, 48] as [number, number], maxZoom: 12}
// A single place is shown from far away, so that the map tells where in the country or the region it lies
const SINGLE_STOP_FIT_OPTIONS = {...FIT_OPTIONS, maxZoom: 5}

// A round marker in the accent color; with several stops it shows the number of the stop in the order of the visit.
// A divIcon instead of Leaflet's default marker, because the default marker images are not found after the Vite build.
function stopIcon(label: string) {
    return divIcon({
        className: '',
        html: `<span class="flex size-8 items-center justify-center rounded-full border-2 border-white bg-accent text-sm font-bold text-white shadow-card">${label}</span>`,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
        popupAnchor: [0, -18],
    })
}

function fitOptions(stopCount: number) {
    return stopCount === 1 ? SINGLE_STOP_FIT_OPTIONS : FIT_OPTIONS
}

// Fits the map to the stops: once at the start and again whenever a stop is added, moved or deleted,
// but not when only a name or a note changes, so the map stays where the user moved it.
// The positions arrive as text, e.g. "43.77,11.25;45.44,12.33", because a new array after every save
// would run the effect each time.
// It has to be a component inside MapContainer, because only there useMap knows the map.
function FitToStops({positionsKey}: Readonly<{ positionsKey: string }>) {
    const map = useMap()

    useEffect(() => {
        const positions = positionsKey.split(';')
            .map((position) => position.split(',').map(Number) as LatLngTuple)
        // The box is higher with a route than with a single place; Leaflet has to measure it again first
        map.invalidateSize()
        map.fitBounds(latLngBounds(positions), fitOptions(positions.length))
    }, [map, positionsKey])

    return null
}

// The map card in "Reiseverlauf" (as in the mockup): the stops as numbered markers with their names, a line between
// them in the order of the visit, and below the map the number of places and the route length.
// A click on a marker shows the place, its date and the note.
export default function TripMap({stops, routeLengthKm, className = ''}: Readonly<TripMapProps>) {
    const positions: LatLngTuple[] = stops.map((stop) => [stop.latitude, stop.longitude])
    const hasRoute = stops.length > 1

    return (
        <div className={`flex flex-col gap-4 rounded-card bg-surface p-4 shadow-card ${className}`}>
            {/* isolate: Leaflet's layers have a high z-index; this keeps them below the rest of the page.
                The height is set here and not on MapContainer, because react-leaflet sets its class only once,
                and a single place needs less room than a route. */}
            <div className={`isolate overflow-hidden rounded-xl ${hasRoute ? 'h-80 lg:h-[30rem]' : 'h-64 lg:h-80'}`}>
                <MapContainer
                    bounds={latLngBounds(positions)}
                    boundsOptions={fitOptions(stops.length)}
                    // Zooming with a pinch or Ctrl + mouse wheel; plain scrolling scrolls the page (MapWheelZoom below)
                    className="h-full w-full"
                >
                    <TileLayer
                        url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                        maxZoom={19}
                    />
                    {hasRoute && (
                        // The options as props, not as pathOptions: Leaflet reads className only when it creates the line
                        <Polyline
                            positions={positions}
                            className="stroke-accent"
                            weight={3}
                            opacity={0.8}
                            dashArray="8 8"
                        />
                    )}
                    {stops.map((stop, position) => (
                        <Marker
                            key={stop.id}
                            position={[stop.latitude, stop.longitude]}
                            // A single place has no number, as in the list next to the map
                            icon={stopIcon(hasRoute ? String(position + 1) : '')}
                            title={stop.name}
                            alt={stop.name}
                        >
                            {/* The name next to the marker, always visible; the ! classes win over leaflet.css */}
                            <Tooltip
                                permanent
                                direction="right"
                                offset={[14, 0]}
                                className="rounded-full! border-0! px-2.5! py-0.5! font-sans text-[13px] font-semibold text-ink shadow-card! before:hidden"
                            >
                                {stop.name}
                            </Tooltip>
                            <Popup>
                                <div className="flex max-w-60 flex-col gap-1 font-sans">
                                    <strong className="text-base text-ink">{stop.name}</strong>
                                    <span className="text-[13px] font-semibold text-muted">
                                        {formatDayMonth(stop.date)} · {getCountryName(stop.countryCode)}
                                    </span>
                                    {stop.note && <span className="text-sm text-ink-soft">{stop.note}</span>}
                                </div>
                            </Popup>
                        </Marker>
                    ))}
                    <FitToStops positionsKey={positions.map((position) => position.join(',')).join(';')}/>
                    <MapWheelZoom/>
                </MapContainer>
            </div>
            {/* With one place the numbers would only say "1 Ort" and "0 km" */}
            {hasRoute && (
                <dl className="grid grid-cols-2 gap-4 px-2 pb-1">
                    <div className="flex flex-col gap-1">
                        <dt className="text-[13px] font-semibold text-muted">Orte</dt>
                        <dd className="text-2xl font-bold">{stops.length}</dd>
                    </div>
                    <div className="flex flex-col gap-1">
                        <dt className="text-[13px] font-semibold text-muted">Strecke (Luftlinie)</dt>
                        <dd className="text-2xl font-bold">{formatKm(routeLengthKm)}</dd>
                    </div>
                </dl>
            )}
        </div>
    )
}

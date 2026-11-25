import type {ReactElement} from 'react'
import type {Texts} from './i18n/texts'
import StatsPage from './pages/StatsPage'
import TripsPage from './pages/TripsPage'
import WishlistPage from './pages/WishlistPage'
import WorldMapPage from './pages/WorldMapPage'

// Start page: the logo, "/" and the 404 page lead here.
export const HOME_PATH = '/trips'

type NavPage = {
    path: string
    labelKey: Exclude<keyof Texts['nav'], 'main' | 'logout'> // the menu text in the chosen language, e.g. t.nav.trips
    element: ReactElement
}

// Pages in the main navigation, in menu order.
// Each entry creates both the route (App.tsx) and the menu link (NavBar.tsx).
export const navPages: NavPage[] = [
    {path: HOME_PATH, labelKey: 'trips', element: <TripsPage/>},
    {path: '/map', labelKey: 'map', element: <WorldMapPage/>},
    {path: '/stats', labelKey: 'stats', element: <StatsPage/>},
    {path: '/wishlist', labelKey: 'wishlist', element: <WishlistPage/>},
]

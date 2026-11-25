import {useLanguage} from '../i18n/useLanguage'
import {STOP_TYPES, type StopType} from '../types/trip'

type StopTypeFieldProps = {
    name: string // name of the radio group; must be unique on the page, e.g. "stop-type"
    value: StopType
    onChange: (type: StopType) => void
}

// Choice how the user spent the time at a place: overnight stay, passing through or day trip
export default function StopTypeField({name, value, onChange}: Readonly<StopTypeFieldProps>) {
    const {t} = useLanguage()
    return (
        <fieldset className="flex flex-col gap-2">
            <legend className="mb-2 text-sm font-semibold text-ink-soft">{t.stopTypes.question}</legend>
            {STOP_TYPES.map((type) => (
                <label
                    key={type}
                    htmlFor={`${name}-${type}`}
                    className={`flex cursor-pointer items-center gap-3 rounded-xl border px-4 py-3 text-[15px] ${
                        type === value ? 'border-accent bg-accent-soft' : 'border-line-strong hover:border-accent'
                    }`}
                >
                    <input
                        id={`${name}-${type}`}
                        type="radio"
                        name={name}
                        value={type}
                        checked={type === value}
                        onChange={() => onChange(type)}
                        className="size-5 accent-accent"
                    />
                    <span className="flex flex-col">
                        <span className="font-semibold text-ink">{t.stopTypes.labels[type]}</span>
                        <span className="text-sm text-muted">{t.stopTypes.hints[type]}</span>
                    </span>
                </label>
            ))}
        </fieldset>
    )
}

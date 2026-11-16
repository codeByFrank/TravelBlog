import type {InputHTMLAttributes} from 'react'

type FormFieldProps = InputHTMLAttributes<HTMLInputElement> & {
    label: string
}

// Label with input, styled like the rest of the app
export default function FormField({label, id, ...inputProps}: FormFieldProps) {
    return (
        <div className="flex flex-col gap-2">
            <label htmlFor={id} className="text-sm font-semibold text-ink-soft">
                {label}
            </label>
            <input
                id={id}
                className="min-h-12 rounded-xl border border-line-strong bg-surface px-4 text-[15px] text-ink outline-none placeholder:text-muted/70 focus:border-accent focus:ring-4 focus:ring-accent-soft"
                {...inputProps}
            />
        </div>
    )
}
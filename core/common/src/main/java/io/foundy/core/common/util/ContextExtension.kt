package io.foundy.core.common.util

import android.content.Context
import android.os.Build
import java.util.Locale

@Suppress("DEPRECATION")
val Context.currentLocale
    get(): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.resources.configuration.locales.get(0)
        } else {
            this.resources.configuration.locale
        }
    }

package com.g57.issuehub.data

import com.g57.issuehub.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {
    val enabled: Boolean = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL.ifBlank { "https://invalid.local" },
        supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY.ifBlank { "demo" }
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}

package io.github.ohem.punkbrew.di

import android.content.Context
import android.content.SharedPreferences
import io.github.ohem.punkbrew.data.BeersRepository
import io.github.ohem.punkbrew.ui.catalog.CatalogViewModel
import io.github.ohem.punkbrew.ui.details.DetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val PREF_FILE_NAME = "punkbrew_prefs"

val appModule = module {

    single {
        (get() as Context).getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
    }
    factory { BeersRepository(get(), get()) }
    viewModel { CatalogViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}

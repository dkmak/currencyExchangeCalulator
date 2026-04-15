package com.currencyexchangecalculator.data

import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val preferredCurrencyCode = stringPreferencesKey("preferred_currency_code")
    }

    val preferredCurrencyCode : Flow<String> =  dataStore.data.map{ preferences ->
        preferences[Keys.preferredCurrencyCode] ?: "MXN"
    }

    suspend fun savePreferredCurrency(code: String){
        dataStore.edit { preferences ->
            preferences[Keys.preferredCurrencyCode] = code
        }
    }
}
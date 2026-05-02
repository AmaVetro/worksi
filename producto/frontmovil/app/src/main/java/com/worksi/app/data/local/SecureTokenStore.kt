package com.worksi.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureTokenStore {
  private const val PREFS_NAME = "worksi_auth_secure"
  private const val KEY_ACCESS_TOKEN = "access_token"

  private var prefs: SharedPreferences? = null

  fun init(context: Context) {
    if (prefs != null) return
    val app = context.applicationContext
    val masterKey = MasterKey.Builder(app).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    prefs =
        EncryptedSharedPreferences.create(
            app,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
  }

  fun saveAccessToken(token: String) {
    prefs?.edit()?.putString(KEY_ACCESS_TOKEN, token)?.apply()
  }

  fun getAccessToken(): String? = prefs?.getString(KEY_ACCESS_TOKEN, null)

  fun clear() {
    prefs?.edit()?.clear()?.apply()
  }

  fun hasAccessToken(): Boolean = !getAccessToken().isNullOrBlank()
}

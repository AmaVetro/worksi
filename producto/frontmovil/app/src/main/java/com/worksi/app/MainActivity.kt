package com.worksi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.ui.navigation.AppNavigation
import com.worksi.app.ui.theme.WorkSyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PDFBoxResourceLoader.init(applicationContext)
        SecureTokenStore.init(this)
        enableEdgeToEdge()
        setContent {
            WorkSyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}
package com.example.savor.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.example.savor.R
import com.example.savor.data.remote.Product

object IntentHelper {

    private fun productUrl(product: Product): String? {
        val code = product.code?.takeIf { it.isNotBlank() } ?: return null
        return "https://world.openfoodfacts.org/product/$code"
    }

    // Implicit intent 1: open the product page in any browser
    fun openInBrowser(context: Context, product: Product) {
        val url = productUrl(product) ?: return
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        launch(context, intent)
    }

    // Implicit intent 2: compose an email in any email app
    fun shareByEmail(context: Context, product: Product) {
        val name = product.productName.orEmpty()
        val brand = product.brands ?: context.getString(R.string.unknown_brand)
        val url = productUrl(product) ?: ""
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()   // only email apps respond to this
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject, name))
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.email_body, name, brand, url)
            )
        }
        launch(context, intent)
    }

    // If nothing on the device can handle the intent, tell the user instead of crashing
    private fun launch(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_app_found, Toast.LENGTH_SHORT).show()
        }
    }
}

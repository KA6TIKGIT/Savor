package com.example.savor.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.savor.R
import com.example.savor.data.remote.Product

object IntentHelper {

    private fun productUrl(product: Product) =
        "https://world.openfoodfacts.org/product/${product.code}"

    // Implicit intent 1: open the product page in any browser
    fun openInBrowser(context: Context, product: Product) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(productUrl(product)))
        launch(context, intent)
    }

    // Implicit intent 2: compose an email in any email app
    fun shareByEmail(context: Context, product: Product) {
        val name = product.productName.orEmpty()
        val brand = product.brands ?: context.getString(R.string.unknown_brand)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")   // only email apps respond to this
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject, name))
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.email_body, name, brand, productUrl(product))
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
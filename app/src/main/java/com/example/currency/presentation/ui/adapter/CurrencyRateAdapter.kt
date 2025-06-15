package com.example.currency.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currencytask.R

/**
 * A custom [ArrayAdapter] used to display a list of currency rates in a dropdown menu.
 *
 * This adapter is designed for use with an AutoCompleteTextView,
 * rendering each item with a custom layout defined in `R.layout.dropdown_menu_item`.
 *
 * @param context The context used to inflate views.
 * @param items The list of [CurrenciesRatesData] to display in the dropdown.
 */
class CurrencyRateAdapter(
    context: Context,
    private var items: List<CurrenciesRatesData>
) : ArrayAdapter<CurrenciesRatesData>(
    context,
    R.layout.dropdown_menu_item,
    items
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.dropdown_menu_item, parent, false)

        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.selection_text)
        textView.text = item?.currencyCode ?: ""

        return view
    }

    override fun getItem(position: Int): CurrenciesRatesData? {
        return items.getOrNull(position)
    }
}

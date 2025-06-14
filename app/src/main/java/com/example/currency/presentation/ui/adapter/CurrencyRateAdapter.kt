package com.example.currency.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currencytask.R
import com.example.currencytask.databinding.DropdownMenuItemBinding

class CurrencyRateAdapter(
    context: Context,
    private var items: List<CurrenciesRatesData>
) : ArrayAdapter<CurrenciesRatesData>(
    context,
    R.layout.dropdown_menu_item, // your custom layout
    items
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

//        val view: View
//        val vh: ItemRowHolder
//        if (convertView == null) {
//            vh = ItemRowHolder(LayoutInflater.from(parent.context)).apply {
//                view = itemBinding.root
//            }
//            view.tag = vh
//        } else {
//            view = convertView
//            vh = view.tag as ItemRowHolder
//        }
//
//        vh.itemBinding.currencyRate = getItem(position)
//        vh.itemBinding.executePendingBindings()
//        return view
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

//    private class ItemRowHolder(inflater: LayoutInflater) {
//        val itemBinding: DropdownMenuItemBinding =
//            DataBindingUtil.inflate(inflater, R.layout.dropdown_menu_item, null, false)
//    }

//     fun updateItems(newItems: List<CurrenciesRatesData>) {
//        items = newItems
//        clear()
//        addAll(newItems)
//        notifyDataSetChanged()
//    }
}

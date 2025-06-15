package com.example.currency.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currency.domain.model.HistoricalCurrenciesData
import com.example.currencytask.databinding.HistoricalCurrencyItemBinding

/**
 * A [ListAdapter] implementation for displaying a list of historical currency conversion transactions
 * using data binding.
 *
 * This adapter uses DiffUtil to optimize list updates based on [HistoricalCurrenciesData.id].
 * It binds each item to a layout via [HistoricalCurrencyItemBinding].
 */
class HistoricalCurrenciesAdapter :
    ListAdapter<HistoricalCurrenciesData, HistoricalCurrenciesAdapter.HistoricalCurrenciesViewHolder>(
        object : DiffUtil.ItemCallback<HistoricalCurrenciesData>() {
            override fun areItemsTheSame(
                oldItem: HistoricalCurrenciesData,
                newItem: HistoricalCurrenciesData
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: HistoricalCurrenciesData,
                newItem: HistoricalCurrenciesData
            ): Boolean = oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoricalCurrenciesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = HistoricalCurrencyItemBinding.inflate(layoutInflater, parent, false)
        return HistoricalCurrenciesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoricalCurrenciesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class HistoricalCurrenciesViewHolder(
        private val binding: HistoricalCurrencyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoricalCurrenciesData) {
            binding.historicalCurrenciesData = item
            binding.executePendingBindings()
        }
    }
}
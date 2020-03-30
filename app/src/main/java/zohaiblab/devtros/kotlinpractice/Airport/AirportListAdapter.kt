/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zohaiblab.devtros.kotlinpractice.Airport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import zohaiblab.devtros.kotlinpractice.R


class AirportListAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<AirportListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var allAirports = emptyList<Airport>() // Cached copy of words

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtview: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = allAirports[position]
        holder.txtview.text = current.name
    }

    internal fun set(airports: List<Airport>) {
        allAirports = airports
        notifyDataSetChanged()
    }

    override fun getItemCount() = allAirports.size
}



package edu.ub.sportshub.event

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class ArrayAdapterNoFilter(context: Context, resource: Int) :
    ArrayAdapter<String>(context, resource) {

    private var noFilter = NoFilter()

    override fun getFilter(): Filter {
        return noFilter
    }

    class NoFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            return FilterResults()
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            // nothing
        }

    }
}
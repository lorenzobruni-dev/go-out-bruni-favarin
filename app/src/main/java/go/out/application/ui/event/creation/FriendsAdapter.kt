import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import go.out.application.R

class FriendsAdapter(context: Context, resource: Int, objects: List<StateVO>) :
    ArrayAdapter<StateVO>(context, resource, objects) {

    private val mContext: Context = context
    private val listState: List<StateVO> = objects

    fun getNumberOfSelected (friends: List<StateVO>): Int {
        return friends.subList(1 , friends.size).count { it.selected }
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    @SuppressLint("SetTextI18n")
    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.spinner_item_checkbox, parent, false)
            holder = ViewHolder()
            holder.mTextView = convertView?.findViewById<TextView>(R.id.text)!!
            holder.mCheckBox = convertView?.findViewById<CheckBox>(R.id.checkbox)!!
        } else {
            holder = convertView.tag as ViewHolder
        }
        convertView.tag = holder
        holder.mTextView.text = listState[position].title

        holder.mCheckBox.setOnCheckedChangeListener(null)
        holder.mCheckBox.isChecked = listState[position].selected == true

        if (position == 0) {
            holder.mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_dropdown))
            holder.mCheckBox.visibility = View.INVISIBLE
        } else {
            holder.mCheckBox.visibility = View.VISIBLE
        }

        holder.mCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(TAG , getNumberOfSelected(listState).toString())
            listState[0].title = listState.takeIf { it.isNotEmpty() }
                ?.let { "${getNumberOfSelected(listState)} Amici selezionati" }
                ?: "Seleziona amici"
            listState[position].selected = isChecked
        }


        return convertView!!
    }

    private class ViewHolder {
        lateinit var mTextView: TextView
        lateinit var mCheckBox: CheckBox
    }
}
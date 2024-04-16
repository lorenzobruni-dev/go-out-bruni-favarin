import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import go.out.application.R


class FriendsAdapter(context: Context, resource: Int, objects: List<StateVO>) :
    ArrayAdapter<StateVO>(context, resource, objects)  {

    interface StateChangeListener {
        fun onStateChanged()
    }

    private val mContext: Context = context
    private val listState: List<StateVO> = objects
    private var numberOfFriendsSelected : Int = 0
    private var stateChangeListener: StateChangeListener? = null

    fun setStateChangeListener(listener: StateChangeListener) {
        this.stateChangeListener = listener
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
            holder.mCheckBox.visibility = View.INVISIBLE
        } else {
            holder.mCheckBox.visibility = View.VISIBLE
        }

        holder.mCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!listState[position].selected) numberOfFriendsSelected++
            else numberOfFriendsSelected--
            listState[0].title = numberOfFriendsSelected.takeIf { it > 0 }
                ?.let { "${numberOfFriendsSelected} Amici selezionati" }
                ?: "Seleziona amici"
            listState[position].selected = isChecked
            stateChangeListener?.onStateChanged()
        }


        return convertView!!
    }

    private class ViewHolder {
        lateinit var mTextView: TextView
        lateinit var mCheckBox: CheckBox
    }
}
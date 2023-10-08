import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_s.R
import com.example.todo_s.ToDo

class ToDosAdapter(private val todoSet: MutableList<ToDo>) : RecyclerView.Adapter<ToDosAdapter.ToDoViewHolder>() {

    class ToDoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dueDateTextView: TextView = itemView.findViewById(R.id.dueDateTextView)
        val stateTextView: TextView = itemView.findViewById(R.id.stateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDosAdapter.ToDoViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.list, parent, false)
        return ToDoViewHolder(contactView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = todoSet.toList()[position]
        holder.descriptionTextView.text = currentItem.description
        holder.dueDateTextView.text = currentItem.dueDate.toString()
        if (!currentItem.state){
            holder.stateTextView.text = "Ja"
        }
        else{
            holder.stateTextView.text = "Nein"
        }

    }

    override fun getItemCount() = todoSet.size

    fun getItemAtPosition(position: Int): ToDo {
        return todoSet[position]
    }
}

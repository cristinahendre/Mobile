package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Exam
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StudentActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.retry -> {
                logd("retry clicked")
                GlobalScope.launch(Dispatchers.Main) {
                    progress.show()
                    fetchData()
                    observeModel()
                    progress.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    inner class ListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<ListAdapter.PeopleViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val builder = AlertDialog.Builder(context)
        private var data = mutableListOf<Exam>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val groupItemView: TextView = itemView.findViewById(R.id.group)
            val typeItemView: TextView = itemView.findViewById(R.id.type)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val detailsItemView: TextView = itemView.findViewById(R.id.details)
            val studentsItemView: TextView = itemView.findViewById(R.id.students)
            val ivSelect: Button = itemView.findViewById(R.id.ivSelect)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.all_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.nameItemView.text = current.name
            holder.groupItemView.text = current.group
            holder.typeItemView.text = current.type
            holder.statusItemView.text = current.status
            holder.detailsItemView.text = current.details
            holder.idItemView.text = current.id.toString()
            holder.studentsItemView.text = current.students.toString()

            holder.ivSelect.setOnClickListener {
                logd("to join $current")
                join(current)
            }
        }

        internal fun setData(myData: List<Exam>) {
            this.data.clear()
            this.data.addAll(myData)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }

    private fun join(exam: Exam){

        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val resp= model.join(exam)
            if(resp == "off"){
                displayMessage("The server is off")
            }
            else{
                val obj =deserialize(resp)
                if(obj == null){
                    displayMessage("Exam not available!")
                }
                else{
                    exam.students =obj.students
                    myViewModel.update(exam)
                    logd("server data is ${model.data.value}")
                    model.data.value?.let { displayData(it) }
                }
            }
            progress.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {

        myViewModel.getAll()
        myViewModel.data?.observe {  }
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val myData = model.getDraftExams()
            if (myData == null) {
                //the server is off
                displayMessage("The server is down.")
            } else {
                displayData(myData)
            }
            progress.dismiss()
        }

    }


    private fun displayData(gr: List<Exam>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        myViewModel.getAllChanged()
        myViewModel.changedData?.observe {}
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@StudentActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun deserialize(myString: String): Exam? {
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("type") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)

            indexStart = myString.indexOf("students") + 9
            indexStop = myString.indexOf("changed") - 3
            val students = getStringFromArea(indexStart, indexStop, myString).toInt()
            return Exam(id,"","","",status,"",students,0)
        } catch (e: NumberFormatException) {
            logd("exceptie la deserializare $e")
            return null
        }

    }

    private fun getStringFromArea(start: Int, end: Int, myMessage: String): String {
        var result = ""
        for (i in myMessage.indices) {
            if (i in start..end) result += myMessage[i]
        }
        return result
    }

}
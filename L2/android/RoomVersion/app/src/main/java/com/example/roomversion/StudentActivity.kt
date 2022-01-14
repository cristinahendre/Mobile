package com.example.roomversion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Teacher
import com.example.roomversion.models.Model
import com.example.roomversion.viewmodel.GradeViewModel
import com.example.roomversion.viewmodel.TeacherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StudentActivity : AppCompatActivity() {

    private var studentName: String = ""
    private var studentId: Int = -1
    private val model: Model by viewModels()
    private lateinit var gradeViewModel: GradeViewModel
    private var toggle = false  //false= filtered list, true= unfiltered
    private lateinit var teacherViewModel: TeacherViewModel
    private lateinit var adapter: GradeListAdapter


    private val filterActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val dataExtra = data.extras
                    if (dataExtra != null) {
                        val subject = dataExtra.getString(FilterStudentsActivity.SUBJECT)
                        val grade = dataExtra.getString(FilterStudentsActivity.GRADE)
                        val date = dataExtra.getString(FilterStudentsActivity.DATE)
                        if (subject != null && date != null && grade != null) {
                            filterGrades(subject, date, grade)
                        }
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        val extras = intent.extras
        if (extras != null) {
            studentName = extras.getString("Name")!!
            studentId = extras.getInt("Id")
        }

        teacherViewModel = ViewModelProviders.of(this).get(TeacherViewModel::class.java)
        gradeViewModel = ViewModelProviders.of(this).get(GradeViewModel::class.java)
        adapter = GradeListAdapter(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch(Dispatchers.Main) {
            val myStudentsGrades = model.getStudentsGrades(studentId)
            if (myStudentsGrades == null) {
                //use db data
                Toast.makeText(
                    applicationContext,
                    "The server is down, using local data.",
                    Toast.LENGTH_SHORT
                ).show()
                gradeViewModel.getStudentsGrades(studentId)
            }

            observeModel()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_students, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> {
                logd("logout")
                val intent = Intent(this, LoginActivity::class.java)
                studentId = -1
                studentName = ""
                startActivity(intent)
                finish()
            }
        }
        when (item.itemId) {
            R.id.filterNote -> {
                logd("filter note")

                if (!toggle) {

                    toggle = true
                    item.setIcon(R.drawable.no_filter)
                    val intent = Intent(this, FilterStudentsActivity::class.java)
                    filterActivityLauncher.launch(intent)
                } else {
                    item.setIcon(R.drawable.filter)
                    toggle = false
                    model.studentsGrades.value?.let { displayGrades(it) }

                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun filterGrades(subject: String, date: String, grade: String) {

        var teacher: Teacher?
        GlobalScope.launch(Dispatchers.Main) {
            teacher = model.getTeacherBySubject(subject)
            if (teacher != null && teacher!!.id == -1 && teacher!!.name == "") {
                //the server is down
                Toast.makeText(
                    applicationContext,
                    "The server is down, using local data.",
                    Toast.LENGTH_SHORT
                ).show()
                teacher = teacherViewModel.getTeacherBySubjectName(subject)
            }
            setFilteredData(teacher, date, grade)
        }

    }

    private fun displayGrades(gr: List<Grade>) {
        adapter.setGrades(gr)
    }

    private fun setFilteredData(teacher: Teacher?, date: String, grade: String) {
        var idToSend = ""
        if (teacher != null) {
            idToSend = teacher.id.toString()
        }

        var filteredGrades = model.filterStudentsGrades(studentId, idToSend, date, grade)
        if (filteredGrades == null) {
            //the server might be down
            gradeViewModel.filterStudentsGrades(studentId, idToSend, date, grade)
            filteredGrades = gradeViewModel.studentsGradesFiltered
        }
        displayGrades(filteredGrades)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = adapter
    }

    inner class GradeListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<GradeListAdapter.GradeViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var grades = emptyList<Grade>()


        inner class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val teacherItemView: TextView = itemView.findViewById(R.id.teacher)
            val dateItemView: TextView = itemView.findViewById(R.id.date)
            val gradeItemView: TextView = itemView.findViewById(R.id.grade)
            val subjectItemView: TextView = itemView.findViewById(R.id.subject)

        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item_stud, parent, false)
            return GradeViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
            val current = grades[position]
            var t: Teacher?
            GlobalScope.launch(Dispatchers.Main) {
                t = model.getTeacherById(current.teacherId)
                if (t!!.id == -1 && t!!.name == "") {
                    //the server is down
                    Toast.makeText(
                        applicationContext,
                        "The server is down, using local data.",
                        Toast.LENGTH_SHORT
                    ).show()
                    t = teacherViewModel.getTeacherById(current.teacherId)
                }
                if (t != null) {
                    setHolder(holder, t!!)
                } else {
                    Toast.makeText(baseContext, "Invalid teacher.", Toast.LENGTH_SHORT).show()
                }

            }
            holder.dateItemView.text = current.date.toString()
            holder.gradeItemView.text = current.gradeValue.toString()
        }

        internal fun setGrades(words: List<Grade>) {
            this.grades = words
            notifyDataSetChanged()
        }

        private fun setHolder(holder: GradeViewHolder, teacher: Teacher) {
            holder.teacherItemView.text = teacher.name
            holder.subjectItemView.text = teacher.subject

        }

        override fun getItemCount() = grades.size

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun observeModel() {
        model.studentsGrades.observe { displayGrades(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@StudentActivity, { observe(it) })

}
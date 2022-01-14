package com.example.roomversion

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.models.Model
import com.example.roomversion.viewmodel.GradeViewModel
import com.example.roomversion.viewmodel.StudentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var gradeViewModel: GradeViewModel
    private lateinit var studentViewModel: StudentViewModel
    private val model: Model by viewModels()
    private var teacherName: String = ""
    private var teacherSubject: String = ""
    private var teacherId: Int = -1
    private var toggle = false
    private lateinit var adapter: GradeListAdapter
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = View(this)
        val extras = intent.extras
        if (extras != null) {
            teacherName = extras.getString("Name")!!
            teacherId = extras.getInt("Id")
            teacherSubject = extras.getString("Subject")!!
        }

        gradeViewModel = ViewModelProviders.of(this).get(GradeViewModel::class.java)
        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel::class.java)
        adapter = GradeListAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddGradeActivity::class.java)
            intent.putExtra(AddGradeActivity.TEACHER_ID, teacherId)
            startActivity(intent)
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
                teacherId = -1
                teacherName = ""
                teacherSubject = ""
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
                    val intent = Intent(this, FilterTeachersActivity::class.java)
                    filterActivityLauncher.launch(intent)
                } else {

                    item.setIcon(R.drawable.filter)
                    toggle = false
                    if(model.teachersGradesFiltered!= null)
                         model.teachersGradesFiltered?.let { displayGrades(it) }

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    inner class GradeListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<GradeListAdapter.GradeViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val builder = AlertDialog.Builder(context)
        private var grades = mutableListOf<Grade>()

        inner class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val studentItemView: TextView = itemView.findViewById(R.id.student)
            val dateItemView: TextView = itemView.findViewById(R.id.date)
            val gradeItemView: TextView = itemView.findViewById(R.id.grade)
            val ivEdit: Button = itemView.findViewById(R.id.ivEdit)
            val ivDelete: Button = itemView.findViewById(R.id.ivDelete)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return GradeViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
            val current = grades[position]
            var stud: Student?
            var studentName: String
            GlobalScope.launch(
                Dispatchers.Main
            ) {
                //stud= studentViewModel.getStudentById(current.studentId)
                val progressCircular = findViewById<ProgressBar>(R.id.progress_circular)
                progressCircular.visibility = View.VISIBLE

                stud = model.getStudentById(current.studentId)

                if (stud != null) {
                    if (stud!!.id == -1 && stud!!.name == "") {
                        //the server is down, use local data
                        stud = studentViewModel.getStudentById(current.studentId)
                        logd("[main] get student by id: $stud")
                        studentName = if (stud == null) ""
                        else stud!!.name
                    } else {
                        areChangesToBeDone()
                        studentName = stud!!.name
                    }
                } else studentName = ""
                setStudentNameHolder(holder, studentName)
                progressCircular.visibility = View.GONE
            }
            holder.dateItemView.text = current.date.toString()
            holder.gradeItemView.text = current.gradeValue.toString()
            holder.ivEdit.setOnClickListener {
                logd("update")
                updateNote(current)
            }

            holder.ivDelete.setOnClickListener {view:View->
                val progressCircular = findViewById<ProgressBar>(R.id.progress_circular)
                progressCircular.visibility = View.VISIBLE
                view.animate()
                    .setDuration(11000)
                    .alpha(0.0F)
                    .withEndAction {

                        view.alpha = 1.0F
                        progressCircular.visibility = View.GONE
                    }
                builder.setMessage("Are you sure you want to Delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        val id = grades[position].id
                        //gradeViewModel.delete(id)
                        GlobalScope.launch(
                            Dispatchers.Main
                        ) {
                            areChangesToBeDone()
                            val response = model.deleteGrade(id)
                            if (response == -1) {
                                //server down
                                displayMessage("The server is down.")
                                val grade= grades[position]
                                grade.changed = 2
                                gradeViewModel.update(grade)
                            }
                            else {
                                gradeViewModel.delete(id)
                            }
                            progressCircular.visibility = View.GONE
                            //notifyDataSetChanged()
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                        progressCircular.visibility = View.GONE

                    }
                val alert = builder.create()
                alert.show()

            }
        }


        internal fun setGrades(myGrades: List<Grade>) {
            this.grades.clear()
            this.grades.addAll(myGrades)
            notifyDataSetChanged()
        }


        override fun getItemCount() = grades.size

    }

    private fun setStudentNameHolder(holder: GradeListAdapter.GradeViewHolder, stud: String) {
        holder.studentItemView.text = stud

    }

    private fun setStudentNameIntent(intent: Intent, stud: String) {
        intent.putExtra(AddGradeActivity.STUDENT_NAME, stud)
        startActivity(intent)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        //  gradeViewModel.getTeachersGrades(teacherId)
        logd("set recycler teachers grades: ${gradeViewModel.teachersGrades}")
        gradeViewModel.teachersGrades
            ?.observe(this, { myGrades ->
                if (myGrades != null) {
                    adapter.setGrades(myGrades)
                }
            })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateNote(note: Grade) {
        val intent = Intent(this, AddGradeActivity::class.java)
        intent.putExtra(AddGradeActivity.ID, note.id)
        intent.putExtra(AddGradeActivity.TEACHER_ID, note.teacherId)
        intent.putExtra(AddGradeActivity.GRADE, note.gradeValue.toString())
        intent.putExtra(AddGradeActivity.TEACHER_ID, teacherId)
        intent.putExtra(AddGradeActivity.DATE, note.date.toString())

        var studentName: String
        var myStudent: Student?
        GlobalScope.launch(
            Dispatchers.Main
        ) {
//            val progressCircular = findViewById<ProgressBar>(R.id.progress_circular)
//            progressCircular.visibility = View.VISIBLE
//            view.animate()
//                .setDuration(8000)
//                .alpha(0.0F)
//                .withEndAction {
//
//                    view.alpha = 1.0F
//                    progressCircular.visibility = View.GONE
//                }
            areChangesToBeDone()
            myStudent = model.getStudentById(note.studentId)
            if (myStudent == null) {
                displayMessage("The student does not exist.")
            } else if (myStudent!!.id == -1 && myStudent!!.name == "") {
                // displayMessage("The server is down.")
                myStudent = studentViewModel.getStudentById(note.studentId)
                logd("student name found: $myStudent")
                if (myStudent != null) {
                    studentName = myStudent!!.name
                    setStudentNameIntent(intent, studentName)
                } else {

                    displayMessage("The student does not exist.")
                }

            } else {
                areChangesToBeDone()
                studentName = myStudent!!.name
                setStudentNameIntent(intent, studentName)
            }

        }
    }


    private fun filterGrades(student: String, date: String, grade: String) {

        var stud: Student?
        GlobalScope.launch(
            Dispatchers.Main
        ) {
            stud = model.getStudentByName(student)
            if (stud == null) {
                stud = studentViewModel.getStudentByName(student)
            }
            setFilteredData(stud, date, grade)
        }
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            gradeViewModel.getTeachersGradesChanged(teacherId)
            gradeViewModel.teachersGradesChanged?.observe { logd("grades changed ${gradeViewModel.teachersGradesChanged}") }
            val myGrades = model.getTeachersGrades(teacherId)
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down, using local data.")
            } else {
                areChangesToBeDone()
                gradeViewModel.deleteAll()
                gradeViewModel.insertAll(myGrades)
                logd("done inserting")
            }

        }
    }

    private fun setFilteredData(stud: Student?, date: String, grade: String) {
        val idToSend: String = stud?.id?.toString() ?: ""

        model.filterTeachersGrades(
            idToSend, teacherId.toString(),
            date, grade
        )
        var filteredGrades = model.teachersGradesFiltered
        if (filteredGrades == null) {

            //the server is probably off
            gradeViewModel.filterTeachersGrades(
                idToSend, teacherId.toString(),
                date, grade
            )
            filteredGrades = gradeViewModel.teachersGradesFiltered
        }
        displayGrades(filteredGrades)

    }

    private val filterActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                logd("\n\nresult ok after filter")
                val data = result.data
                if (data != null) {
                    val dataExtra = data.extras
                    if (dataExtra != null) {
                        val student = dataExtra.getString(FilterTeachersActivity.STUDENT)
                        val grade = dataExtra.getString(FilterTeachersActivity.GRADE)
                        val date = dataExtra.getString(FilterTeachersActivity.DATE)
                        if (student != null && date != null && grade != null) {
                            filterGrades(student, date, grade)
                        } else {
                            logd("error")
                        }
                    }
                }
            }
        }

    private fun displayGrades(gr: List<Grade>) {
        adapter.setGrades(gr)
    }

    private fun observeModel() {
        //fetchData()
        gradeViewModel.getTeachersGrades(teacherId)

       // model.teachersGrades.observe { displayGrades(it ?: emptyList()) }
        gradeViewModel.teachersGrades?.observe { displayGrades(it ?: emptyList()) }

    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@MainActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        Toast.makeText(
            applicationContext,
            myMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    suspend fun areChangesToBeDone(): Boolean{
        val dbGrades = gradeViewModel.teachersGradesChanged?.value
        try {
            if (dbGrades != null) {
                logd("are changes to be done, with grades $dbGrades")
                if (dbGrades != null) {
                    for (gr: Grade in dbGrades) {
                        logd(gr)
                        if (gr.changed == 1) {
                            //to add
                            gr.changed = 0
                            val res =model.addGrade(gr)
                            if(res!=-1) {
                                gradeViewModel.insert(gr)
                            }
                            else gr.changed =1
                        }
                        if(gr.changed ==2){
                            //to delete
                            logd("delete grade with id $gr.id")
                            gr.changed = 0
                            val res = model.deleteGrade(gr.id)
                            if(res != -1) {
                                gradeViewModel.delete(gr.id)
                            }
                            else gr.changed =2
                        }
                        if(gr.changed ==3){
                            //to update
                            gr.changed = 0
                            val res =model.updateGrade(gr)
                            if(res != -1) {
                                gradeViewModel.update(gr)
                            }
                            else gr.changed = 3
                        }
                    }
                }
            }
        }
        catch (e: Exception){
            return false
        }
        return true
    }

}
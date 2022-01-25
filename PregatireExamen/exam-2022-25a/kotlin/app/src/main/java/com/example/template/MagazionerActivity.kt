package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Produs
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MagazionerActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magazioner)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)

        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MagazionerActivity, AddActivity::class.java)
            startActivity(intent)
        }

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
                    areChangesToBeDone()
                    if ((myViewModel.data == null && myViewModel.data!!.value == null) ||
                            myViewModel.data!!.value?.size   ==0) {
                        logd("in if")
                        val myData = model.getAll()
                        if (myData == null) {
                            //the server is off
                            displayMessage("The server is down")
                        } else {
                            myViewModel.insertAll(myData)
                            logd("done inserting")
                        }
                        observeModel()
                    }
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
        private var myProducts = mutableListOf<Produs>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val numeItemView: TextView = itemView.findViewById(R.id.nume)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val pretItemView: TextView = itemView.findViewById(R.id.pret)
            val cantitateItemView: TextView = itemView.findViewById(R.id.cantitate)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = myProducts[position]
            holder.numeItemView.text = current.nume
            holder.idItemView.text = current.id.toString()
            holder.pretItemView.text = current.pret.toString()
            holder.cantitateItemView.text = current.cantitate.toString()

        }

        internal fun setData(data: List<Produs>) {
            this.myProducts.clear()
            this.myProducts.addAll(data)
            notifyDataSetChanged()
        }

        override fun getItemCount() = myProducts.size

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            progress.show()
            myViewModel.getAll()
            observeModel()
            myViewModel.getAllChanged()
            myViewModel.changedData?.observe { }
            logd("got all local: data is ${myViewModel.data?.value}")
            if ( myViewModel.data!!.value?.size == 0) {
                logd(" in if ")

                val myGrades = model.getAll()
                if (myGrades == null) {
                    //the server is off
                    displayMessage("The server is down, there is no local data.")
                } else {
                    areChangesToBeDone()
                    myViewModel.insertAll(myGrades)
                    logd("done inserting")
                }
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Produs>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        myViewModel.data?.observe { displayData(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@MagazionerActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private suspend fun areChangesToBeDone(): Boolean {
        val dbChangedData = myViewModel.changedData?.value
        progress.show()
        try {
            if (dbChangedData != null) {
                for (gr: Produs in dbChangedData) {
                    logd(gr)
                    if (gr.changed == 1) {
                        //to add
                        gr.changed = 0
                        val res = model.add(gr)
                        logd("res after add $res")
                        if (res != "off") {
                            val myObj =deserialize(res)
                            if(myObj!=null) {
                                myViewModel.delete(gr.id)
                                gr.status = myObj.status
                                gr.id = myObj.id
                                gr.changed =0
                                displaySuccessMessage(gr)
                                myViewModel.insert(gr)
                            }
                        } else gr.changed = 1
                    }
                    if (gr.changed == 2) {
                        //to delete
                        gr.changed = 0
                        val res = model.delete(gr.id)
                        if (res != "off") {
                            myViewModel.delete(gr.id)
                        } else gr.changed = 2
                    }
                    if (gr.changed == 3) {
                        //to update
                        gr.changed = 0
                        val res = model.update(gr)
                        if (res != "off") {
                            myViewModel.update(gr)
                        } else gr.changed = 3
                    }
                }
            }
        } catch (e: Exception) {
            progress.dismiss()
            return false
        }
        progress.dismiss()
        return true
    }

    private fun displaySuccessMessage(product:Produs){
        val text ="Produsul cu numele ${product.nume} si pretul ${product.pret} " +
                " si tipul ${product.tip} a fost salvat in cantitatea ${product.cantitate}" +
                " cu discountul ${product.discount}"
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Produs? {
// Produs(id=51, nume=dummt, tip=dk, cantitate=34, pret=1, discount=22, status=true, changed=0)
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("changed") -3
            val status = getStringFromArea(indexStart, indexStop, myString).toBoolean()
            return Produs(id,"","",0,0,0,status,0)
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
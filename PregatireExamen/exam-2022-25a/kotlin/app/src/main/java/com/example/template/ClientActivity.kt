package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Produs
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClientActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
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
            val tipItemView: TextView = itemView.findViewById(R.id.tip)
            val pretItemView: TextView = itemView.findViewById(R.id.pret)
            val cantitateItemView: TextView = itemView.findViewById(R.id.cantitate)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.item_tip, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = myProducts[position]
            holder.numeItemView.text = current.nume
            holder.tipItemView.text = current.tip
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
            val allProducts = model.getAll()
            if (allProducts == null) {
                //the server is off
                displayMessage("The server is down.")
            } else {
                logd("am primit produse $allProducts")
                val toDisplay = mutableListOf<Produs>()
                var prod:Produs
                for(produs in allProducts){

                     prod =getCheapestOfType(allProducts,produs.tip)
                    if(!toDisplay.contains(prod)){
                        toDisplay.add(prod)
                    }
                }
                displayData(toDisplay)


            }
            progress.dismiss()

        }

    }

    private fun getCheapestOfType(lista:List<Produs>, type:String): Produs{

        var result =Produs(0,"",type,0,389393393,0,true,0)
        for(el in lista){
            if(el.tip == type){
                if(el.pret < result.pret){
                    result =el
                }
            }
        }
        return result
    }

    private fun displayData(gr: List<Produs>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        myViewModel.data?.observe { }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@ClientActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }


}
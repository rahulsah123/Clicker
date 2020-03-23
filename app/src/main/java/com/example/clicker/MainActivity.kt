package com.example.clicker

import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.example.adapter.ListOfPeople
import com.example.model.ListOfPeopleData
import com.example.retrofit.RetrofitError

import com.example.retrofit.RetrofitUtil
import com.example.retrofit.utils.Constant
import com.example.sqliteDB.DatabaseController
import kotlinx.android.synthetic.main.activity_main.view.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var timeleft: TextView
    lateinit var num_of_clicks: TextView
    lateinit var highest_score_person: TextView
    lateinit var your_name: TextView
    lateinit var your_score: TextView
    lateinit var ur_hscore: TextView
    lateinit var listViewData: RecyclerView
    lateinit var click: Button
    lateinit var start: Button
    lateinit var deleteData: Button
    lateinit var changeName: Button
    var secondLeft = 30
    var numberOfClicks = 0
    lateinit var relative: RelativeLayout
    lateinit var databaseController: DatabaseController
    internal var adapter: ListOfPeople? = null
    var h_scoreNmae:String=""
    var h_score:String="1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        relative = findViewById(R.id.relative)
        timeleft = findViewById(R.id.timeleft)
        num_of_clicks = findViewById(R.id.num_of_clicks)
        click = findViewById(R.id.click)
        start = findViewById(R.id.start)
        deleteData = findViewById(R.id.deleteData)
        changeName = findViewById(R.id.changeName)
        highest_score_person = findViewById(R.id.highest_score_person)
        your_name = findViewById(R.id.your_name)
        your_score = findViewById(R.id.your_score)
        listViewData = findViewById(R.id.listViewData)
        ur_hscore = findViewById(R.id.ur_hscore)



        databaseController = DatabaseController(this@MainActivity)

        //  var arrayList=databaseController.allClicks
        //var arrayAdapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_expandable_list_item_1,arrayList)
        //   listViewData.adapter=arrayAdapter

        var linearLayoutManager = LinearLayoutManager(this@MainActivity);
        listViewData.layoutManager = linearLayoutManager
        adapter = ListOfPeople(this@MainActivity, databaseController.allClicks)
        listViewData.adapter = adapter
        adapter!!.notifyDataSetChanged()
        ur_hscore.text = databaseController.highestScore
        // callUpdatedNaameScore("Rahul", "3")
        getHscore()
        val at = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.popup_window, null)
        val editText = view.findViewById<EditText>(R.id.editText)
        val done = view.findViewById<Button>(R.id.done)
        at.setView(view)
        val dialog = at.create()
        dialog.show()
        done.setOnClickListener {
            if (editText.text.length > 0) {
                your_name.text = editText.text.toString()

                dialog.cancel()
            } else {
                editText.hint = "Please Enter Your name"
            }
        }

        //Timer
        val countDownTimer: CountDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondLeft--
                timeleft.text = "Seconds Left: $secondLeft"
                if(secondLeft<=5)click.setBackgroundColor(Color.RED)
                if(secondLeft<=0)click.isEnabled = false
            }

            override fun onFinish() {
                your_score.text = "" + numberOfClicks + ""
                databaseController.insertCliks(your_name.text.toString(), numberOfClicks)
                click.isEnabled = false
                ur_hscore.text = databaseController.highestScore
                adapter = ListOfPeople(this@MainActivity, databaseController.allClicks)
                listViewData.adapter = adapter
                adapter!!.notifyDataSetChanged()
                var what_time_score_at:String=databaseController.dateTime
                var nclicks:Int=h_score.toInt()
                if(nclicks<=numberOfClicks){
                    highest_score_person.setText(your_name.text.toString()+": "+your_score.text)
                }
                callUpdatedNaameScore(your_name.text.toString(), your_score.text.toString(),ur_hscore.text.toString(),what_time_score_at)
            }
        }

        ///Timer End

        start.setOnClickListener {
             countDownTimer.cancel()
            numberOfClicks = 0
            secondLeft = 30
            click.setBackgroundColor(Color.GREEN);
            if (your_name.text.toString().isEmpty()) {
                val at = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.popup_window, null)
                val editText = view.findViewById<EditText>(R.id.editText)
                val done = view.findViewById<Button>(R.id.done)
                at.setView(view)
                val dialog = at.create()
                dialog.show()
                done.setOnClickListener {
                    if (editText.text.length > 0) {
                        your_name.text = editText.text.toString()
                        click.isEnabled = true
                        countDownTimer.start()
                        dialog.cancel()
                    } else {
                        editText.hint = "Please Enter Your name"
                    }
                }
            } else {
                click.isEnabled = true
                countDownTimer.start()
            }

        }
        changeName.setOnClickListener {
            countDownTimer.cancel()
            click.isEnabled = false

            val at = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.popup_window, null)
            val editText = view.findViewById<EditText>(R.id.editText)
            val done = view.findViewById<Button>(R.id.done)
            at.setView(view)
            val dialog = at.create()
            dialog.show()
            done.setOnClickListener {
                if (editText.text.length > 0) {
                    your_name.text = editText.text.toString()
                    your_score.text = ""
                    numberOfClicks = 0
                    secondLeft = 30
                    dialog.cancel()
                } else {
                    editText.hint = "Please Enter Your name"
                }
            }
        }

        click.setOnClickListener {
            numberOfClicks++
            num_of_clicks.text = "Number of Clicks: $numberOfClicks"
        }
        deleteData.setOnClickListener {
            listViewData.removeAllViews()
            databaseController.deleteAllrec()
            adapter = ListOfPeople(this@MainActivity, databaseController.allClicks)
            listViewData.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }

    }

    private fun getHscore(){
        val service = RetrofitUtil.create(Constant.BASE_URLS)
        val call = service.getHscoreName("rahul");

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val r = response.body()!!.string()
                        Log.e("Response", r)
                        val jsonObject = JSONObject(r)
                        val has_error = jsonObject.optString("has_error")
                        if (has_error == "0") {
                            h_scoreNmae=jsonObject.optString("name")
                            h_score=jsonObject.optString("hscore")
                            if(!h_scoreNmae.equals("null")&& !h_score.equals("null")) {
                                highest_score_person.setText(h_scoreNmae + ": " + h_score)
                            }else{
                                h_scoreNmae="Name"
                                h_score="0"
                            }
                            Log.e("MainActivity ", "You have score ")

                        } else {
                            Toast.makeText(this@MainActivity, "Error in fetching data", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                try {
                    val str = RetrofitError.showErrorMessage(t)
                    Toast.makeText(this@MainActivity, str.toString(), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }
            }
        })
    }

    private fun callUpdatedNaameScore(name: String, score: String,highest_score:String,score_time:String) {
        Log.e("MainActivity ", name+"score "+score+" "+highest_score+" "+score_time)
        val service = RetrofitUtil.create(Constant.BASE_URLS)

        val call = service.updateNameScore(name, score,score_time)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val r = response.body()!!.string()
                        Log.e("Response", r)
                        val jsonObject = JSONObject(r)
                        val has_error = jsonObject.optString("has_error")
                        if (has_error == "0") {
                            Log.e("MainActivity ", "You have score ")

                        } else {
                            Toast.makeText(this@MainActivity, "Error in fetching data", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                try {
                    val str = RetrofitError.showErrorMessage(t)
                    Toast.makeText(this@MainActivity, str.toString(), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }
            }
        })
    }
}

package com.cmy.jetpacktest

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainModel
    private lateinit var sp: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved", 0)
        viewModel = ViewModelProvider(this,ViewModelFactory(countReserved)).get(MainModel::class.java)

        initListener()
    }

    override fun onPause() {
        super.onPause()
        sp.edit {
            putInt("count_reserved", viewModel.counter.value ?: 0)
        }
    }

    private fun initListener() {
        plusOneBtn.setOnClickListener {
            viewModel.plusOne()
        }
        clearBtn.setOnClickListener {
            viewModel.clear()
        }

        viewModel.userName.observe(this, Observer {
            Log.d("szjjyh", "userName initListener: $it")
        })

        viewModel.counter.observe(this, Observer{ count ->
            infoText.text = count.toString()
        })

        getUserBtn.setOnClickListener {
            val userId = (0..10000).random().toString()
            viewModel.getUser(userId)
        }
        viewModel.user.observe(this, Observer { user ->
            infoText.text = user.firstName
            Log.d("szjjyh", "user initListener: ${user.toString()}")
        })

        val userDao = AppDatabase.getDatabase(this).userDao()
        val user1 = User("Tom1", "Brady", 40)
        val user2 = User("Tom2", "Hanks", 50)
        addDataBtn.setOnClickListener {
            thread {
               userDao.insertUser(user1)
                userDao.insertUser(user2)
            }
        }
        updateDataBtn.setOnClickListener {
            thread {
                user1.age = 42
                userDao.updateUser(user1)
            }
        }
        deleteDataBtn.setOnClickListener {
            thread {
                userDao.deleteUserByLastName("Hanks")
            }
        }
        queryDataBtn.setOnClickListener {
            thread {
                for (user in userDao.loadAllUsers()) {
                    Log.d("szjjyh MainActivity", user.toString())
                }
            }
        }
//        doWorkBtn.setOnClickListener {
//            val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).build()
//            WorkManager.getInstance(this).enqueue(request)
//        }
    }
}

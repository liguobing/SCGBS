package com.liguobing.scgbs.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liguobing.scgbs.db.Data
import com.liguobing.scgbs.db.DataBase
import kotlinx.coroutines.*

class DataListActivity : AppCompatActivity() {

    private var dataList: MutableList<Data>? = null

    private val TAG = "TTT"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainContent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val instance = DataBase.getInstance(this@DataListActivity)
            val dao = instance.dataDao()
            dao.getAllData().collect {
                withContext(Dispatchers.Main) {
                    it.forEachIndexed { _, data ->
                        dataList!!.add(Data(time = data.time, account = data.account))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent() {

        dataList = remember {
            mutableStateListOf()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Result List", modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            LazyColumn() {
                items(dataList!!) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(start = 50.dp, end = 50.dp, top = 15.dp)
                            .border(BorderStroke(1.dp, Color.Black)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${it.account.toInt() * it.time.toInt()}",
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        }
    }
}
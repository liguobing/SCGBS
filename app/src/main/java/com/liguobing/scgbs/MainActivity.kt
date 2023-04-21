package com.liguobing.scgbs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : AppCompatActivity() {

    private var moneyState: MutableState<String>? = null
    private var timeState: MutableState<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var text: TextView = findViewById(R.id.money)


    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent() {
        val scope = rememberCoroutineScope()

        moneyState = remember {
            mutableStateOf("")
        }
        timeState = remember {
            mutableStateOf(0)
        }

        Scaffold(
            content = { it ->
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                ) {
                    item {
                        Text(text = "Account:")
                    }
                    item {
                        Text(text = "123123123:")
                    }
//                    items(contactItemList!!) {
//                        Box(modifier = Modifier
//                            .padding(start = 10.dp, end = 10.dp, top = 5.dp)
//                            .clip(RoundedCornerShape(10))
//                            .clickable {
//                                if (!editableState!!.value) {
//                                    playingContactNameState!!.value = it.contactBean.contactName
//                                    waitDialogState!!.value = true
//                                    viewModel!!.getRecords(
//                                        this@RecordActivity,
//                                        it.contactBean.contactId
//                                    )
//                                }
//                            }) {
//                            ContactNameCard(it)
//                        }
//                    }
                }
            },
        )
    }
}
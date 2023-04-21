package com.liguobing.scgbs.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType.Companion.Decimal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.liguobing.scgbs.db.Data
import com.liguobing.scgbs.db.DataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Duration
import java.util.*


@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {

    private var accountState: MutableState<String>? = null
    private var formatAccountState: MutableState<String>? = null
    private var timeState: MutableState<String>? = null
    private var formatTimeState: MutableState<String>? = null
    private val df = DecimalFormat("#.##")
    private val calendar: Calendar = Calendar.getInstance()
    private var dialogIsShow: MutableState<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        df.roundingMode = RoundingMode.DOWN
        setContent {
            MaterialTheme {
                MainContent()
            }
        }
    }

    @Composable
    private fun InitParam() {
        accountState = remember {
            mutableStateOf("")
        }
        formatAccountState = remember {
            mutableStateOf("Account is not set")
        }
        timeState = remember {
            mutableStateOf("")
        }
        formatTimeState = remember {
            mutableStateOf("Time is not set")
        }
        dialogIsShow = remember {
            mutableStateOf(false)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent() {
        InitParam()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(modifier = Modifier.padding(top = 30.dp)) {
                Text(text = "Account:")
                Text(text = formatAccountState!!.value)
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = Decimal),
                value = accountState!!.value,
                onValueChange = {
                    if (it.length > accountState!!.value.length) {
                        //只允许一个小数点
                        val pointCount = it.count {
                            it == '.'
                        }
                        if (pointCount > 1) {
                            return@TextField
                        }
                        //起始不能为小数点
                        if (accountState!!.value.isEmpty() && it == ".") {
                            return@TextField
                        }
                        //保留两位小数，覆盖手动移动光标
                        if (it.contains(".") && it.split(".")[1].length > 2) {
                            accountState!!.value = df.format(it.toDouble())
                            thousandsFormat(accountState!!.value)
                            return@TextField
                        }
                        //非数字小数点输入
                        if (!it.matches(Regex("^[\\d.]+\$"))) {
                            return@TextField
                        }
                        accountState!!.value = it
                    } else {
                        accountState!!.value = it
                    }
                    thousandsFormat(accountState!!.value)
                }, placeholder = {
                    Text(text = "Please input account")
                })

            Row(
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Text(text = "Time:")
                Text(text = formatTimeState!!.value)
            }
            TextField(modifier = Modifier
                .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = Decimal),
                value = timeState!!.value,
                onValueChange = {
                    if (it.length > timeState!!.value.length) {
                        //非数字输入
                        if (!it.matches(Regex("^\\d+\$"))) {
                            return@TextField
                        }
                        //起始不能为0
                        if (it.startsWith("0")) {
                            return@TextField
                        }
                        //限制输入时间为1天
                        if (it.toInt() > 86400) {
                            Toast.makeText(this@MainActivity, "只能输入一天内的秒数", Toast.LENGTH_SHORT)
                                .show()
                            return@TextField
                        }
                        timeState!!.value = it
                    } else {
                        timeState!!.value = it
                    }
                    timeFormat(timeState!!.value)
                },
                placeholder = {
                    Text(text = "Please input second")
                })
            Button(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 30.dp),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    if (timeState!!.value.isEmpty() || accountState!!.value.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Time or Account is not entered",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    dialogIsShow!!.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        val instance = DataBase.getInstance(this@MainActivity)
                        val dao = instance.dataDao()
                        val data = Data(time = timeState!!.value, account = accountState!!.value)
                        val result = dao.insertData(data)
                        if (result > 0) {
                            accountState!!.value = ""
                            formatAccountState!!.value = "Account is not set"
                            timeState!!.value = ""
                            formatTimeState!!.value = "Time is not set"
                            val intent = Intent(this@MainActivity, DataListActivity::class.java)
                            startActivity(intent)
                            dialogIsShow!!.value = false
                        }
                    }
                },
            ) {
                Text(text = "Submit")
            }

            if (dialogIsShow!!.value) {
                AlertDialog(
                    text = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Waiting",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    },
                    onDismissRequest = { dialogIsShow!!.value = false },
                    confirmButton = {
                    },
                    properties = DialogProperties(dismissOnClickOutside = false),
                )
            }
        }
    }

    private fun thousandsFormat(input: String) {
        if (input.isNotEmpty()) {
            var bd = BigDecimal(input)
            bd = bd.setScale(2, RoundingMode.HALF_DOWN)
            formatAccountState!!.value = bd.toString()
        }
    }


    private fun timeFormat(input: String) {
        if (input.isEmpty()) {
            formatTimeState!!.value = "未设置时间"
        } else {
            calendar.timeInMillis = input.toLong() * 1000
            val seconds = Duration.ofSeconds(input.toLong())
            val hours: Long = seconds.toHours()
            val minutes: Long = seconds.toMinutes() % 60
            val second: Long = seconds.seconds % 60
            formatTimeState!!.value = "$hours h $minutes m $second s"
        }
    }
}
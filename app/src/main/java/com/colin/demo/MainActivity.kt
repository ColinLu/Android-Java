package com.colin.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.colin.library.android.UtilsDemo
import com.colin.library.android.base.BaseDemo
import com.colin.library.android.http.OkDemo
import com.colin.library.android.http.OkKtDemo
import com.colin.library.android.widgets.WidgetsDemo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}

fun show(){
    BaseDemo.show()
    OkDemo.show()
    OkKtDemo.show()
    UtilsDemo.show()
    WidgetsDemo.show()
}

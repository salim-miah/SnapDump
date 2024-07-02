package com.example.snapdump

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import org.osmdroid.config.Configuration

class MainActivity : AppCompatActivity(){
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var drawermenu = findViewById<DrawerLayout>(R.id.navigationdrawermenu)

        toggle = ActionBarDrawerToggle(this,drawermenu,R.string.open,R.string.close)
        drawermenu.setDrawerListener(toggle)
        toggle.syncState()

        var actions = arrayOf("Map", "Entity Form", "Entity List")
        var adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, actions)
        var listview = findViewById<ListView>(R.id.listview1)
        listview.adapter = adapter


        listview.setOnItemClickListener{ parent, view, position, id->
            drawermenu.closeDrawers()
            when (position){
                0 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, Map()).commit()
                }
                1 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, EntityForm()).commit()
                }
                2 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, EntityList()).commit()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
}
package io.dev.tfg.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.dev.tfg.R

class OfficeActivity : AppCompatActivity() {
    lateinit var navigation : BottomNavigationView

    private val onNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.itemUserFrag -> {
                supportFragmentManager.commit {
                    replace<AddUserFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemGraphicsFrag -> {
                supportFragmentManager.commit {
                    replace<OfficeGraphsFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemModifyFrag -> {
                supportFragmentManager.commit {
                    replace<ModifyDbFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_office)

        navigation =  findViewById(R.id.navMenu)
        navigation.setOnNavigationItemSelectedListener(onNavMenu)
        supportFragmentManager.commit{
            replace<AddUserFragment>(R.id.frameContainer)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }
    }
}
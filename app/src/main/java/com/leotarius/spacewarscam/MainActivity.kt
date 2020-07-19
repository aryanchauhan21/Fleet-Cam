package com.leotarius.spacewarscam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.VisibilitySetterAction
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomSheet()
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = ships.top

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheet.bringToFront()
                    fab.visibility  = View.GONE
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    fab.visibility = View.VISIBLE
                }
            }

        })
    }
}
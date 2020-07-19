package com.leotarius.spacewarscam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.VisibilitySetterAction
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var currCamPosition = Vector3.zero()
    lateinit var centreNode: Node
    lateinit var arFragment: ArFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = fragment as ArFragment

        setUpBottomSheet()

        centreNode = Node()
        centreNode.setParent(arFragment.arSceneView.scene)
        arFragment.arSceneView.scene.addOnUpdateListener {
            val frame = arFragment.arSceneView.arFrame
            frame?.let {
                updateCenterNode()
            }
        }
        
        launch.setOnClickListener {
            loadModelAndAddToScene(R.raw.star_destroyer)
            launch.isEnabled = false;
            Log.d(TAG, "onCreate: adding to scene")
        }

    }

    private fun updateCenterNode() {
//        Log.d(TAG, "updateCenterNode")
        currCamPosition = arFragment.arSceneView.scene.camera.worldPosition
        centreNode.worldPosition = Vector3(currCamPosition.x, 0f, currCamPosition.z)
    }

    private fun loadModelAndAddToScene(modelResourceId: Int) {
        ModelRenderable.builder()
            .setSource(this, modelResourceId)
            .build()
            .thenAccept {
                addNodeToScene(it)
            }.exceptionally {
                Toast.makeText(this, "Error creating node ", Toast.LENGTH_SHORT).show()
                null
            }
    }

    private fun addNodeToScene(modelRenderable: ModelRenderable?) {
        Log.d(TAG, "addNodeToScene: adding a node to the scene")

        val rotatingNode = RotatingNode(90f).apply {
            setParent(centreNode)
        }
        Node().apply {
            renderable = modelRenderable
            setParent(rotatingNode)
            localPosition = Vector3(2f, 1f, 0f)
            localRotation = Quaternion.eulerAngles(Vector3(0f, 180f, 0f))
        }
        Log.d(TAG, "addNodeToScene: added to scene")
        launch.isEnabled = true
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

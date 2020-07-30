package com.leotarius.SpaceWarsCam

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.CamcorderProfile
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    var distanceFromCenter: Float = 0f
    var heightAboveGround: Float = 0f
    var rotationSpeeed: Float = 0f
    var modelId: Int = R.raw.star_destroyer
    private lateinit var videoRecorder: VideoRecorder
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = fragment as ArFragment

        setUpBottomSheet()
        setUpFab()

        videoRecorder = VideoRecorder(this).apply {
            sceneView = arFragment.arSceneView
            setVideoQuality(CamcorderProfile.QUALITY_720P, resources.configuration.orientation)
        }

        star_destroyer.setOnClickListener {
            modelId = R.raw.star_destroyer
            warshipUiUpdate()
        }
        x_wing.setOnClickListener {
            modelId = R.raw.xwing
            warshipUiUpdate()
        }
        tie_silencer.setOnClickListener {
            modelId = R.raw.tie_silencer
            warshipUiUpdate()
        }

        centreNode = Node()
        centreNode.setParent(arFragment.arSceneView.scene)
        arFragment.arSceneView.scene.addOnUpdateListener {
            val frame = arFragment.arSceneView.arFrame
            frame?.let {
                updateCenterNode()
            }
        }
        
        launch.setOnClickListener {

            // Disabling th button until the model is fully loaded.
            launch.isEnabled = false
            launch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#f6e58d"));

            // Taking values from slider
            distanceFromCenter = radius.value
            heightAboveGround = height.value
            rotationSpeeed = speed.value

            // loading the model and adding to scene
            loadModelAndAddToScene(modelId)

            Log.d(TAG, "onCreate: adding to scene")
        }

        about.setOnClickListener{
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpFab() {
        fab.setOnClickListener {
            if (!isRecording){
                fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#eb2f06"))
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                isRecording = videoRecorder.toggleRecordingState()
            } else{
                fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffd32a"))
                isRecording = videoRecorder.toggleRecordingState()
                Toast.makeText(this, "Recording saved to gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun warshipUiUpdate() {
        tie_silencer.setBackgroundColor(Color.parseColor("#66353b48"))
        star_destroyer.setBackgroundColor(Color.parseColor("#66353b48"))
        x_wing.setBackgroundColor(Color.parseColor("#66353b48"))

        when(modelId){
            R.raw.tie_silencer -> tie_silencer.setBackgroundColor(Color.parseColor("#CC2f3640"))
            R.raw.xwing -> x_wing.setBackgroundColor(Color.parseColor("#CC2f3640"))
            R.raw.star_destroyer -> star_destroyer.setBackgroundColor(Color.parseColor("#CC2f3640"))
        }
    }

    private fun updateCenterNode() {
        currCamPosition = arFragment.arSceneView.scene.camera.worldPosition
        centreNode.worldPosition = Vector3(currCamPosition.x, 0f, currCamPosition.z)
    }

    private fun loadModelAndAddToScene(modelResourceId: Int) {
        ModelRenderable.builder()
            .setSource(this, modelResourceId)
            .build()
            .thenAccept {
                val warship = when(modelResourceId){
                    R.raw.star_destroyer -> Warship.StarDestroyer
                    R.raw.tie_silencer -> Warship.TieSilencer
                    R.raw.xwing -> Warship.XWing
                    else -> Warship.XWing
                }
                addNodeToScene(it, warship)
            }.exceptionally {
                Toast.makeText(this, "Error creating node ", Toast.LENGTH_SHORT).show()
                null
            }
    }

    private fun addNodeToScene(modelRenderable: ModelRenderable?, warship: Warship) {
        Log.d(TAG, "addNodeToScene: adding a node to the scene")

        val rotatingNode = RotatingNode(rotationSpeeed).apply {
            setParent(centreNode)
        }
        Node().apply {
            renderable = modelRenderable
            setParent(rotatingNode)
            localPosition = Vector3(distanceFromCenter, heightAboveGround, 0f)
            localRotation = Quaternion.eulerAngles(Vector3(0f, warship.rotationDegree, 0f))
        }
        Log.d(TAG, "addNodeToScene: added to scene")
        launch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffdd59"));
        launch.isEnabled = true
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val inner = bottomSheet
        inner.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                inner.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val hidden = inner.getChildAt(1)
                bottomSheetBehavior.peekHeight = hidden.top
            }
        })

        bottomSheetBehavior.isGestureInsetBottomIgnored = true

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

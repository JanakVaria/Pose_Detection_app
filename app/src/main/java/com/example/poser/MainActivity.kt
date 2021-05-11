package com.example.poser

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.wonderkiln.camerakit.*


class MainActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var cameraButton: LinearLayout? = null
    var galleryButton: LinearLayout? = null

    var cameraViewPose: CameraView? = null
    var progressBar: ProgressBar? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences("Login",Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        cameraButton = findViewById(R.id.cameraBtn)
        galleryButton = findViewById(R.id.galleryBtn)
        cameraViewPose = findViewById(R.id.poseCamera)

        auth = Firebase.auth

        // Progress Bar
        progressBar = findViewById(R.id.spin_kit)
        var fadingCircle = FadingCircle()
        progressBar?.setIndeterminateDrawableTiled(fadingCircle)
        progressBar?.setVisibility(View.INVISIBLE)


        // OnClick
        cameraButton?.setOnClickListener(View.OnClickListener {
            poseDetect()
        })
        galleryButton?.setOnClickListener(View.OnClickListener {
            galleryAdd()
        })

        cameraViewPose?.addCameraKitListener(object : CameraKitEventListener {
            override fun onEvent(cameraKitEvent: CameraKitEvent) {}
            override fun onError(cameraKitError: CameraKitError) {}
            override fun onImage(cameraKitImage: CameraKitImage) {
                progressBar?.setVisibility(View.VISIBLE)
                var bitmap = cameraKitImage.bitmap
                bitmap = Bitmap.createScaledBitmap(
                    bitmap!!,
                    cameraViewPose!!.getWidth(),
                    cameraViewPose!!.getHeight(),
                    false
                )
                cameraViewPose?.stop()
                runPose(bitmap)
            }

            override fun onVideo(cameraKitVideo: CameraKitVideo) {}
        })
    }

//    fun onClick(view: View) {
//        when (view.id) {
//            R.id.cameraBtn ->
//            R.id.galleryBtn ->
//        }
//    }

    final val Requset_Gallery = 1
    fun galleryAdd(){
        val mediaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(mediaIntent, Requset_Gallery)
    }

    var galleryImage: Bitmap? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Requset_Gallery -> try {
                val imageUri = data!!.data
                val inputStream = contentResolver.openInputStream(imageUri!!)
                galleryImage = BitmapFactory.decodeStream(inputStream)
                runPose(galleryImage)
                progressBar!!.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Image Could Not Be Taken From Gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun poseDetect() {
        cameraViewPose!!.start()
        cameraViewPose!!.captureImage()
    }

    override fun onResume() {
        super.onResume()
        cameraViewPose!!.start()
    }

    override fun onStop() {
        super.onStop()
        cameraViewPose!!.stop()
    }

    override fun onPause() {
        super.onPause()
        cameraViewPose!!.stop()
    }

//pose detect
    val options = AccuratePoseDetectorOptions.Builder()
    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
    .build()

    val poseDetector = PoseDetection.getClient(options)

    var resizedBitmap: Bitmap? = null


    private fun runPose(bitmap: Bitmap?) {
        val rotationDegree = 0

        val width = bitmap!!.width
        val height = bitmap!!.height

        resizedBitmap = Bitmap.createBitmap(bitmap!!, 0, 0, width, height)

        val image = InputImage.fromBitmap(resizedBitmap, rotationDegree)

        poseDetector.process(image)
            .addOnSuccessListener { pose -> processPose(pose) }
            .addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "Pose Not Detected",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    var angleText: String? = null

    private fun processPose(pose: Pose?) {
        try {

            // Shoulder
            val leftShoulder = pose!!.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose!!.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

            // Elbows
            val leftElbow = pose!!.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose!!.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)

            // Wrists
            val leftWrist = pose!!.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose!!.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

            // hip
            val leftHip = pose!!.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose!!.getPoseLandmark(PoseLandmark.RIGHT_HIP)

            // Foot Legs
            val leftKnee = pose!!.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose!!.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

            // Ankles
            val leftAnkle = pose!!.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose!!.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)


            //Shoulder
            val leftShoulderP = leftShoulder.position
            val lShoulderX = leftShoulderP.x
            val lShoulderY = leftShoulderP.y
            val rightSoulderP = rightShoulder.position
            val rShoulderX = rightSoulderP.x
            val rShoulderY = rightSoulderP.y

            //Elbow
            val leftElbowP = leftElbow.position
            val lElbowX = leftElbowP.x
            val lElbowY = leftElbowP.y
            val rightElbowP = rightElbow.position
            val rElbowX = rightElbowP.x
            val rElbowY = rightElbowP.y

            // Hand Wrist
            val leftWristP = leftWrist.position
            val lWristX = leftWristP.x
            val lWristY = leftWristP.y
            val rightWristP = rightWrist.position
            val rWristX = rightWristP.x
            val rWristY = rightWristP.y

            // Hip
            val leftHipP = leftHip.position
            val lHipX = leftHipP.x
            val lHipY = leftHipP.y
            val rightHipP = rightHip.position
            val rHipX = rightHipP.x
            val rHipY = rightHipP.y

            //Knee
            val leftKneeP = leftKnee.position
            val lKneeX = leftKneeP.x
            val lKneeY = leftKneeP.y
            val rightKneeP = rightKnee.position
            val rKneeX = rightKneeP.x
            val rKneeY = rightKneeP.y

            // Ankle
            val leftAnkleP = leftAnkle.position
            val lAnkleX = leftAnkleP.x
            val lAnkleY = leftAnkleP.y
            val rightAnkleP = rightAnkle.position
            val rAnkleX = rightAnkleP.x
            val rAnkleY = rightAnkleP.y

            // Angle Text
            val leftArmAngle: Double = getAngle(leftShoulder, leftElbow, leftWrist)
            val leftArmAngleText = String.format("%.2f", leftArmAngle)
            val rightArmAngle: Double =getAngle(rightShoulder, rightElbow, rightWrist)
            val rightArmAngleText = String.format("%.2f", rightArmAngle)
            val leftLegAngle: Double =getAngle(leftHip, leftKnee, leftAnkle)
            val leftLegAngleText = String.format("%.2f", leftLegAngle)
            val rightLegAngle: Double =getAngle(rightHip, rightKnee, rightAnkle)
            val rightLegAngleText = String.format("%.2f", rightLegAngle)
            angleText = """
                Left Arm Angle : $leftArmAngleText
                Right Arm Angle : $rightArmAngleText
                Left Leg Angle : $leftLegAngleText
                Right Leg Angle : $rightLegAngleText
                """.trimIndent()
            DisplayAll(
                lShoulderX, lShoulderY, rShoulderX, rShoulderY,
                lElbowX, lElbowY, rElbowX, rElbowY,
                lWristX, lWristY, rWristX, rWristY,
                lHipX, lHipY, rHipX, rHipY,
                lKneeX, lKneeY, rKneeX, rKneeY,
                lAnkleX, lAnkleY, rAnkleX, rAnkleY
            )
        } catch (e: java.lang.Exception) {
            Toast.makeText(this@MainActivity, "Pose not Detected", Toast.LENGTH_SHORT).show()
            progressBar!!.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id==R.id.logout){
            editor.clear()
            editor.apply()
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    // Pose Draw
    private fun DisplayAll(
        lShoulderX: Float, lShoulderY: Float, rShoulderX: Float, rShoulderY: Float,
        lElbowX: Float, lElbowY: Float, rElbowX: Float, rElbowY: Float,
        lWristX: Float, lWristY: Float, rWristX: Float, rWristY: Float,
        lHipX: Float, lHipY: Float, rHipX: Float, rHipY: Float,
        lKneeX: Float, lKneeY: Float, rKneeX: Float, rKneeY: Float,
        lAnkleX: Float, lAnkleY: Float, rAnkleX: Float, rAnkleY: Float
    ) {
        val paint = Paint()
        paint.color = Color.GREEN
        val strokeWidth = 4.0f
        paint.strokeWidth = strokeWidth
        val drawBitmap = Bitmap.createBitmap(
            resizedBitmap!!.width,
            resizedBitmap!!.height,
            resizedBitmap!!.config
        )
        val canvas = Canvas(drawBitmap)
        canvas.drawBitmap(resizedBitmap!!, 0f, 0f, null)

        // From Left Shoulder to Right Shoulder
        canvas.drawLine(lShoulderX, lShoulderY, rShoulderX, rShoulderY, paint)

        // Right Shoulder to Right Elbow
        canvas.drawLine(rShoulderX, rShoulderY, rElbowX, rElbowY, paint)

        //From Right Elbow to Right Wrist
        canvas.drawLine(rElbowX, rElbowY, rWristX, rWristY, paint)

        // Left Shoulder to Left Elbow
        canvas.drawLine(lShoulderX, lShoulderY, lElbowX, lElbowY, paint)

        //From Left Elbow to Left Hand Wrist
        canvas.drawLine(lElbowX, lElbowY, lWristX, lWristY, paint)

        //Right Shoulder to Right Hip
        canvas.drawLine(rShoulderX, rShoulderY, rHipX, rHipY, paint)

        // From Left Shoulder to Left Hip
        canvas.drawLine(lShoulderX, lShoulderY, lHipX, lHipY, paint)

        // Hip (Waist)
        canvas.drawLine(lHipX, lHipY, rHipX, rHipY, paint)

        // Right Hip to Right Foot Knee
        canvas.drawLine(rHipX, rHipY, rKneeX, rKneeY, paint)

        // Left Hip to Left Foot Knee
        canvas.drawLine(lHipX, lHipY, lKneeX, lKneeY, paint)

        // Right Foot Knee to Right Ankle
        canvas.drawLine(rKneeX, rKneeY, rAnkleX, rAnkleY, paint)

        // From Left Foot Knee to Left Ankle
        canvas.drawLine(lKneeX, lKneeY, lAnkleX, lAnkleY, paint)

        // MainActivity to MainActivity2
        val intent = Intent(this@MainActivity, MainActivity2::class.java)
        intent.putExtra("Text", angleText)
        val singleton: Singleton? = com.example.poser.Singleton
        if (singleton != null) {
            singleton.setMyImage(drawBitmap)
        }
        startActivity(intent)
    }

    // Angle Detect
    fun getAngle(
        firstPoint: PoseLandmark,
        midPoint: PoseLandmark,
        lastPoint: PoseLandmark
    ): Double {
        var result = Math.toDegrees(
            Math.atan2(
                (lastPoint.position.y - midPoint.position.y).toDouble(), (
                        lastPoint.position.x - midPoint.position.x).toDouble()
            )
                    - Math.atan2(
                (firstPoint.position.y - midPoint.position.y).toDouble(), (
                        firstPoint.position.x - midPoint.position.x).toDouble()
            )
        )
        result = Math.abs(result) // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result) // Always get the acute representation of the angle
        }
        return result
    }

    override fun onBackPressed() {

    }
}

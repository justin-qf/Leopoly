package com.app.leopoly.activity.ScannerActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.common.Constant
import com.app.leopoly.common.LeoPolyApp
import com.app.leopoly.databinding.ActivityNewScannerBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.models.GetScannerData
import com.budiyev.android.codescanner.CodeScanner
import com.google.zxing.BarcodeFormat
import com.vovance.omcsalesapp.Common.PubFun
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*


class ScannerActivity : BaseActivity(), View.OnClickListener, ZXingScannerView.ResultHandler {

    private var binding: ActivityNewScannerBinding? = null
    private var isGranted: Boolean = false
    private var isGrantedCameraPermission: Boolean = false
    private var scannerView: ZXingScannerView? = null
    private var scanResult: String = ""
    val data = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = this
        binding = DataBindingUtil.setContentView(act, R.layout.activity_new_scanner)
        checkPermissionAndOpenCamera()
    }

    private fun initView() {
        binding!!.toolbar.title.text = getString(R.string.scan_title)
        HELPER.setTextColourWithText(binding!!.toolbar.title, act)
        HELPER.setImageColour(binding!!.toolbar.ivBack, act)
        HELPER.setLayoutBgColour(binding!!.toolbar.mainToolLayout, act)
        binding!!.toolbar.ivBack.visibility = View.VISIBLE
        binding!!.toolbar.ivBack.setOnClickListener(this)
        binding!!.title.setOnClickListener(this)
        startScanning()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        HELPER.print("onBackPressed", "DONE")
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.toolbar.ivBack.id -> {
                onBackPressed()
            }
            binding!!.title.id -> {
                if (Patterns.WEB_URL.matcher(binding!!.title.text).matches()) {
                    // Open URL
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(binding!!.title.text.toString()))
                    startActivity(browserIntent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGrantedCameraPermission) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED
            ) {
                act.onBackPressed()
                return
            } else {
                initView()
            }

        } else {
            if (isGranted) {
                if (scannerView != null) {
                    binding!!.scannerView.setResultHandler(this);
                    binding!!.scannerView.startCamera();
                }
            }
        }
    }

    //    override fun onPause() {
//        super.onPause()
//        if (!isGrantedCameraPermission && isGranted) {
//            if (scannerView != null) {
//                binding!!.scannerView.stopCamera()
//            }
//        }
//        HELPER.print("onPause","DONE")
//    }
    override fun onPause() {
        super.onPause()
        if (!isGrantedCameraPermission && isGranted) {
            if (scannerView != null) {
                // Release the camera when the activity is paused
                scannerView!!.stopCamera()
            }
        }
        HELPER.print("onPause", "DONE")
    }

    private fun startScanning() {
        scannerView = ZXingScannerView(this)
        binding!!.scannerView.addView(scannerView)
        binding!!.scannerView.setResultHandler(this)
        binding!!.scannerView.setAspectTolerance(0.5f)
//        val formats = mutableListOf<BarcodeFormat>()
//        formats.add(BarcodeFormat.QR_CODE)
//        formats.add(BarcodeFormat.DATA_MATRIX)
//        binding!!.scannerView.setFormats(formats)
//        binding!!.scannerView.formats
        binding!!.scannerView.startCamera(CodeScanner.CAMERA_BACK) // or CAMERA_FRONT or specific camera id
        binding!!.scannerView.setFormats(CodeScanner.ALL_FORMATS)  // list of type BarcodeFormat,
        binding!!.scannerView.setAutoFocus(true)
        binding!!.scannerView.startCamera()
        binding!!.scannerView.setOnClickListener {
            scannerView!!.startCamera()
        }
    }

    override fun handleResult(rawResult: com.google.zxing.Result?) {
        HELPER.print("ScannerData", rawResult.toString())
        try {
            if (rawResult != null) {
                HELPER.print("ScannerData", rawResult.text) // Prints scan results
                HELPER.print("FORMAT", rawResult.barcodeFormat.name)
               // Toast.makeText(act, rawResult.text, Toast.LENGTH_SHORT).show()
                runOnUiThread {
                    binding!!.title.text = rawResult.text
                    scanResult = rawResult.text
                    scannerView!!.resumeCameraPreview(this)
                    prefManager.scannerId = rawResult.text
                    val filterData = GetScannerData()
                    filterData.scannerData =
                        if (rawResult.text.toString().isEmpty()) {
                            ""
                        } else {
                            rawResult.text
                        }
                    LeoPolyApp.instance!!.observer!!.setValue(
                        Constant.OBSERVER_SCANNING_DATA,
                        gson.toJson(filterData)
                    )
                    onBackPressed()
                }
            }
        } catch (e: Exception) {
            HELPER.print("Exception", e.printStackTrace().toString())
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            isGranted = false
            requestPermission()
        } else {
            isGranted = true
            initView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                isGranted = true
                initView()
            } else {
                // Camera permission denied, handle accordingly
                isGranted = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        PubFun.permissionDialog(
                            act,
                            getString(R.string.permissionRequiredMsg),
                            listener = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    isGrantedCameraPermission = true
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts(
                                        "package",
                                        packageName,
                                        null
                                    )
                                    intent.data = uri
                                    act.startActivityForResult(intent, 0)
                                }
                            },
                            true
                        )
                    }
                }

            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            Constant.PERMISSION_REQUEST_CODE
        )
    }
}
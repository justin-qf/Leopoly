package com.app.leopoly.common

import android.app.Activity
import android.os.Environment
import com.app.leopoly.R
import java.io.File
import java.util.*

internal object FilesURis {
    private fun getRootDirPath(act: Activity): String {
        return Environment.getExternalStorageDirectory()
            .toString() + "/" + act.getString(R.string.app_name)
    }

    fun checkFileExist(act: Activity, downloadUrl: String): Boolean {
        return if (downloadUrl.isEmpty()) {
            false
        } else File(getRootDirPath(act), downloadUrl).exists()
    }

    fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }
}
package easy.pack.app

import android.Manifest
import android.app.ActivityManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.github.boybeak.easypermission.Callback
import com.github.boybeak.easypermission.EasyPermission
import easy.fp.EasyFP
import easy.picker.Config
import easy.picker.EasyPicker
import easy.picker.type.ImageType
import easy.safr.EasySAFR
import easy.safr.OnResultAdapter
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EasyPermission.ask(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .go(this, object : Callback {
                override fun onGranted(permissions: MutableList<String>, requestCode: Int) {
                }

                override fun onDenied(permission: String, requestCode: Int, neverAsk: Boolean) {
                }
            })

        Proxy.newProxyInstance(classLoader, arrayOf(ActivityCompat.OnRequestPermissionsResultCallback::class.java), )

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0 && isStackRoot()) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }

    }

    private fun isStackRoot(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = am.getRunningTasks(10)
        return taskList[0].numActivities == 1 && taskList[0].topActivity!!.className == this::class.java.name
    }

    fun getSingle(v: View) {
//        Log.v(TAG, "OF=${EasyPicker.getConfig().outputFactory} ---")
        EasyPicker.captureAudio().capture(this) { uri, file ->
            Log.v(TAG, "uri=$uri file=$file")
        }
        EasyPicker.fromGallery(ImageType.any()).multipleGet(this) { uris, files ->
            Log.v(TAG, "multipleGet ${uris.size} ${files.size}")
        }
        EasyPicker.captureImage()
            .capture(this) { uri, file ->

            }

    }

    fun showSt(v: View) {
        EasyFP.withDefault(this)

    }

    fun check(v: View) {
        val pair = EasyFP.withDefault(this, true)
            .externalCacheDir()
            .mkdirs("xyz")
            .name("123.txt")
            .pair()

        val realUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", pair.file())
        Log.v(TAG, "file=${pair.file()}")
        Log.v(TAG, "pair.uri=${pair.uri()}")
        Log.v(TAG, "realUri =${realUri}")

        val guessFile = EasyFP.guess(this, realUri)
        Log.v(TAG, "gile=$guessFile")
    }

    fun safr(v: View) {
        val it = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//        val it = Intent(this, ResultMakerActivity::class.java)
        val output = EasyFP.withDefault(this)
            .externalCacheDir()
            .name("abc.jpg")
            .uri()
        it.putExtra(MediaStore.EXTRA_OUTPUT, output)
        it.clipData = ClipData.newRawUri(null, output)
        EasySAFR.startActivityForResult(
            applicationContext,
            it,
            object : OnResultAdapter {
                override fun onOk(id: String, requestCode: Int, data: Intent?) {
                    Toast.makeText(this@MainActivity, "onOk", Toast.LENGTH_SHORT).show()
                }

                override fun onCancel(id: String) {
                    Toast.makeText(this@MainActivity, "onCancel", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

}
package easy.pack.app

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.boybeak.easypermission.Callback
import com.github.boybeak.easypermission.EasyPermission
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
        ).go(this, object : Callback {
                override fun onGranted(permissions: MutableList<String>, requestCode: Int) {
                }

                override fun onDenied(permission: String, requestCode: Int, neverAsk: Boolean) {
                }
            })

        Proxy.newProxyInstance(classLoader,
            arrayOf(ActivityCompat.OnRequestPermissionsResultCallback::class.java)) { proxy, method, args ->
            method.invoke(args)
        }

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

    }

    fun showSt(v: View) {
    }

    fun check(v: View) {
    }

    fun safr(v: View) {
    }

}
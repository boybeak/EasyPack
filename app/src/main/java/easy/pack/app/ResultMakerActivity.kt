package easy.pack.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ResultMakerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_maker)
    }

    fun onResultOk(v: View) {
        setResult(RESULT_OK, Intent().putExtra("RESULT", "I am result"))
        finish()
    }

    fun onResultCancel(v: View) {
        setResult(RESULT_CANCELED)
        finish()
    }

}
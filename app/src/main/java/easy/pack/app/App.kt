package easy.pack.app

import android.app.Application
import easy.fp.EasyFP
import easy.picker.Config
import easy.picker.EasyPicker
import java.io.File
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        EasyPicker.init(Config.defaultConfig(this))
        EasyPicker.init(
            Config.Builder()
                .copyDir(File(cacheDir, "easy_picker"))
                .noMedia()
                .outputFactory { cxt ->
                    EasyFP.withDefault(cxt)
                        .cacheDir()
                        .mkdirs("easy_picker")
                        .name(UUID.randomUUID().toString())
                        .pair()
                }
                .build()
        )
    }
}
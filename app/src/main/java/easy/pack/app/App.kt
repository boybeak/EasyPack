package easy.pack.app

import android.app.Application
import easy.picker.Config
import easy.picker.EasyPicker

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        EasyPicker.init(Config.defaultConfig(this))
    }
}
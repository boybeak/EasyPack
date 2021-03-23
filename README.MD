# EasyPack ![](https://img.shields.io/badge/EasyPack-v0.02-blue)
## Install

In your root build.gradle

```groovy
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

In your module's build.gradle

```groovy
dependencies {
  implementation "com.github.boybeak:easy-safr:$easy_pack_version"
  implementation "com.github.boybeak:easy-fp:$easy_pack_version"
  implementation "com.github.boybeak:easy-picker:$easy_pack_version"
}
```



## Summary

I create a principle called **WCWC**, means "Where calls, where callbacks". Aimed to put calls and callbacks together.

For example, `startActivityForResult` is a call, but the callback is in another method `onActivityResult`. This breaks **WCWC** principle.

**easy-safr:** SAFR is short for *startActivityForResult*.

**easy-fp:** A helper library for FileProvider.

**easy-picker:** A helper library for get or capture audio, image or video content from recorder, gallery or camera. This library based on easy-safr and easy-fp.



## Easy-safr

```kotlin
val it = Intent(this, ResultMakeActivity::class.java)
EasySAFR.startActivityForResult(applicationContext, it,
  object : OnResultAdapter {
    override fun onOk(id: String, requestCode: Int, data: Intent?) {
      Toast.makeText(this@MainActivity, "onOk", Toast.LENGTH_SHORT).show()
    }

    override fun onCancel(id: String) {
      Toast.makeText(this@MainActivity, "onCancel", Toast.LENGTH_SHORT).show()
    }
  }
)
```

There's no need to handle the result in onActivityResult method.



## Easy-fp

FileProvider is a bit complex. Easy-fp makes it easy to use and provide a uri-to-file access.

Make a paths.xml file in R.xml res folder.

```xml
<!--R.xml.paths-->
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path name="files_path" path="images/"/>
    <cache-path name="my_images" path="images/" />
    <external-cache-path
        name="external_cache_path"
        path="images"
        />
</paths>
```

> **Do not repeat any one of the path items above.**
>
> If you make 2 files-path items with the same name or path, this library not suite for you.

```xml
<provider
	android:name="androidx.core.content.FileProvider"
	android:authorities="${applicationId}.fileprovider"
	android:exported="false"
	android:grantUriPermissions="true">
  <meta-data
		android:name="android.support.FILE_PROVIDER_PATHS"
		android:resource="@xml/paths" />
</provider>
```

Get file from uri.

```kotlin
val uri = ...
val file = EasyFP.guess(this, uri)
```

In this example, the `uri` must be in the R.xml.paths items.

Get uri by dir and file name.

```kotlin
EasyFP.withAuthority(this, "your authority")
  .cacheDir()
  .name("xyz.jpg")
  .uri()
//.file() or .uri()
```

Use a default FileProvider - The first FileProvider listed in manifest file.

```kotlin
EasyFP.withDefault(this)
  .cacheDir()
  .name("xyz.jpg")
  .pair() 
//.file() or .uri()
```

`uri()` means return uri, `file` means return file, `pair()`means return both uri and file.



## Easy-picker

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        EasyPicker.init(Config.defaultConfig(this))
      	// OR
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
    }
}
```



```kotlin
// Get mutiple images
EasyPicker.fromGallery(ImageType.any()).multipleGet(this) { uris, files ->
	Log.v(TAG, "multipleGet ${uris.size} ${files.size}")
}

// Record an audio
EasyPicker.captureAudio().capture(this) { uri, file ->
	Log.v(TAG, "uri=$uri file=$file")
}

// Capture an image
EasyPicker.captureImage()
.output(somePair) // If you do not provide somePair, it will auto generate in OutputFactory
.capture(this) { uri, file ->
}
```


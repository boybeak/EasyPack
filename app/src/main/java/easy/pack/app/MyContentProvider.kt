package easy.pack.app

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.util.Log

class MyContentProvider : ContentProvider() {

    companion object {
        private val TAG = MyContentProvider::class.java.simpleName
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return "application/octet-stream"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return uri

    }

    override fun attachInfo(context: Context?, info: ProviderInfo?) {
        super.attachInfo(context, info)
        Log.v(TAG, "attachInfo info=${info?.authority}")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}
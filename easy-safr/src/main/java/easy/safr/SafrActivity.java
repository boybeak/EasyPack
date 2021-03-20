package easy.safr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class SafrActivity extends Activity {

    private static final String TAG = SafrActivity.class.getSimpleName();

    private String id = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        id = getIntent().getStringExtra(EasySAFR.KEY_ID);
        Intent it = getIntent().getParcelableExtra(EasySAFR.KEY_SOURCE_INTENT);
        int requestCode = getIntent().getIntExtra(EasySAFR.KEY_REQUEST_CODE, EasySAFR.REQUEST_CODE_DEFAULT);
        try {
            startActivityForResult(it, requestCode);
        } catch (Throwable t) {
            EasySAFR.onThrow(id, t);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult " + resultCode);
        EasySAFR.onResult(id, requestCode, resultCode, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        id = null;
    }
}

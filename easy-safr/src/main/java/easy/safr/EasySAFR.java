package easy.safr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

public class EasySAFR {

    private static final String TAG = EasySAFR.class.getSimpleName();

    private static final int WHAT_ADDING_FRAGMENT = 1;
    static final String KEY_SOURCE_INTENT = "easy.safr.KEY_SOURCE_INTENT",
            KEY_ID = "easy.safr.KEY_ID", TAG_SAFR_FRAGMENT = "easy.safr.TAG_SAFR_FRAGMENT",
            KEY_REQUEST_CODE = "easy.safr.KEY_REQUEST_CODE";
    public static final int REQUEST_CODE_DEFAULT = 16;

    private static final WeakHashMap<String, SafrFragment> sIdFragments = new WeakHashMap<>();
    private static final Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (WHAT_ADDING_FRAGMENT == msg.what) {
                String id = (String) msg.obj;
                Bundle data = msg.getData();
                int requestCode = data.getInt(KEY_REQUEST_CODE);
                Intent it = data.getParcelable(KEY_SOURCE_INTENT);
                fragmentStartActivityForResult(id, sIdFragments.remove(id), it, requestCode);
            }
        }
    };

    private static final Map<String, OnResult> sMap = new WeakHashMap<>();

    private static String putOnResult(OnResult onResult) {
        String id = UUID.randomUUID().toString();
        sMap.put(id, onResult);
        return id;
    }

    private static OnResult takeOnResult(String id) {
        OnResult onResult = sMap.remove(id);
        if (onResult == null) {
            throw new IllegalStateException("No OnResult was set to id(" + id + ")");
        }
        return onResult;
    }

    public static void startActivityForResult(Context context, Intent intent, OnResult onResult) {
        startActivityForResult(context, intent, REQUEST_CODE_DEFAULT, onResult);
    }
    public static void startActivityForResult(Context context, Intent intent, int requestCode, OnResult onResult) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("You must call this method in main thread");
        }
        String id = putOnResult(onResult);
        onStart(id);
        if (context instanceof FragmentActivity) {
            if (!sIdFragments.isEmpty()) {
                return;
            }
            FragmentActivity fa = (FragmentActivity)context;
            FragmentManager fm = fa.getSupportFragmentManager();
            SafrFragment safrFragment = (SafrFragment) fm.findFragmentByTag(TAG_SAFR_FRAGMENT);
            if (safrFragment == null) {
                safrFragment = new SafrFragment();
                fm.beginTransaction().add(safrFragment, TAG_SAFR_FRAGMENT).commitAllowingStateLoss();
                sIdFragments.put(id, safrFragment);
                Message msg = sHandler.obtainMessage(WHAT_ADDING_FRAGMENT, id);
                Bundle data = new Bundle();
                data.putParcelable(KEY_SOURCE_INTENT, intent);
                data.putInt(KEY_REQUEST_CODE, requestCode);
                msg.setData(data);
                sHandler.sendMessage(msg);
            } else {
                fragmentStartActivityForResult(id, safrFragment, intent, requestCode);
            }
        } else {
            Intent proxyIt = new Intent(context, SafrActivity.class);
            if (!(context instanceof Activity)) {
                proxyIt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            proxyIt.putExtra(KEY_ID, id).putExtra(KEY_REQUEST_CODE, requestCode)
                    .putExtra(KEY_SOURCE_INTENT, intent);
            try {
                context.startActivity(proxyIt);
            } catch (Throwable t) {
                onThrow(id, t);
            }
        }
    }

    private static void fragmentStartActivityForResult(String id, SafrFragment fragment, Intent intent, int requestCode) {
        try {
            fragment.setID(id);
            fragment.startActivityForResult(intent, requestCode);
        } catch (Throwable t) {
            onThrow(id, t);
        }
    }

    private static void onStart(String id) {
        Objects.requireNonNull(sMap.get(id)).onStart(id);
    }

    static void onResult(String id, int requestCode, int resultCode, Intent data) {
        OnResult onResult = takeOnResult(id);
        if (resultCode == Activity.RESULT_CANCELED) {
            onResult.onCancel(id);
        } else if (resultCode == Activity.RESULT_OK) {
            onResult.onOk(id, requestCode, data);
        }
    }

    static void onThrow(String id, Throwable t) {
        takeOnResult(id).onThrow(id, t);
    }

    private EasySAFR(){}

}

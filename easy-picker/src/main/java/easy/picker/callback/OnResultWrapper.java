package easy.picker.callback;

import android.content.Intent;
import android.util.Pair;

import androidx.annotation.NonNull;

import easy.picker.AsyncExecutor;
import easy.safr.OnResult;

public abstract class OnResultWrapper<F, S> implements OnResult {

    private OnGet<F, S> onGet;

    public OnResultWrapper(OnGet<F, S> onGet) {
        this.onGet = onGet;
    }

    @Override
    public final void onStart(@NonNull String id) {
        onGet.onStart();
    }

    @Override
    public final void onOk(@NonNull String id, int requestCode, Intent data) {
        AsyncExecutor.execute(() -> convertAsync(data), new AsyncExecutor.Callback<Pair<F, S>>() {
            @Override
            public void onSuccess(Pair<F, S> fsPair) {
                onGet.onGet(fsPair.first, fsPair.second);
                onGet = null;
            }

            @Override
            public void onThrow(Throwable t) {
                onGet.onThrow(t);
                onGet = null;
            }
        });
    }

    @Override
    public final void onCancel(@NonNull String id) {
        onGet.onCancel();
        onGet = null;
    }

    @Override
    public final void onThrow(@NonNull String id, Throwable t) {
        onGet.onThrow(t);
        onGet = null;
    }

    /**
     * This method runs on a sub thread, do not do UI operations.
     * @param data
     * @return
     */
    public abstract Pair<F, S> convertAsync(Intent data) throws Exception;

}

package easy.picker.callback;

import android.content.Context;

import java.lang.ref.WeakReference;

public abstract class ContextOnResultWrapper<F, S> extends OnResultWrapper<F, S> {
    private WeakReference<Context> contextRef = null;
    public ContextOnResultWrapper(Context context, OnGet<F, S> onGet) {
        super(onGet);
        contextRef = new WeakReference<>(context);
    }

    public final Context contextUnsafe() {
        return contextRef.get();
    }

    public final void release() {
        contextRef.clear();
        contextRef = null;
    }

}

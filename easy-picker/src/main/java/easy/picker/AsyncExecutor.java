package easy.picker;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {

    private AsyncExecutor(){}

    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static final ExecutorService sWorkService = Executors.newSingleThreadExecutor();

    public static <T> void execute(Task<T> task, Callback<T> callback) {
        sWorkService.execute(() -> {
            try {
                T result = task.run();
                sHandler.post(() -> callback.onSuccess(result));
            } catch (Throwable t) {
                sHandler.post(() -> callback.onThrow(t));
            }
        });
    }

    public interface Task<T> {
        T run() throws Exception;
    }
    public interface Callback<T> {
        void onSuccess(T t);
        void onThrow(Throwable t);
    }
}

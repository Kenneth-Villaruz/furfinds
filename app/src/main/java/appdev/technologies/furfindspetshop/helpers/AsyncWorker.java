package appdev.technologies.furfindspetshop.helpers;

import android.app.Activity;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncWorker
{
    private final ExecutorService executorService;
    private final Handler handler;
    private static final int MAX_THREAD_POOL = 4;

    public AsyncWorker(Activity hostActivity)
    {
        executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL);
        handler = new Handler(hostActivity.getMainLooper());
    }

    /**
     * Execute a long-running operation in the background with an option to
     * execute UI-related action after the task has completed.
     *
     * @param task The long-running operation
     * @param invokeOnUI Action to perform on UI thread
     */
    public void Run(Runnable task, Runnable invokeOnUI)
    {
        if (task != null)
        {
            executorService.execute(() -> {
                task.run();

                if (invokeOnUI != null)
                    handler.post(invokeOnUI);
            });
        }
    }

    /**
     * Must be called in onDestroy() lifecycle of host activity.
     * Shutdown the executor service when leaving the activity
     * to prevent memory leaks
     */
    public void Kill()
    {
        if (executorService != null)
            executorService.shutdown();
    }
}

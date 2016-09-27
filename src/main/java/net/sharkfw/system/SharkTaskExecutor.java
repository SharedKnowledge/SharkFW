package net.sharkfw.system;

import java.util.concurrent.*;

/**
 * Created by j4rvis on 9/24/16.
 */
public class SharkTaskExecutor {

    private final ScheduledExecutorService executorService;

    private static SharkTaskExecutor instance = new SharkTaskExecutor();

    private SharkTaskExecutor() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public static SharkTaskExecutor getInstance(){
        return SharkTaskExecutor.instance;
    }

    public <T> Future<T> submit(SharkTask<T> task){
        return executorService.submit((Callable) task);
    }

    public void scheduleAtFixedRate(SharkTask task, long initialDelay, long period, TimeUnit unit){
        executorService.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public void shutdown(){
        executorService.shutdownNow();
    }
}

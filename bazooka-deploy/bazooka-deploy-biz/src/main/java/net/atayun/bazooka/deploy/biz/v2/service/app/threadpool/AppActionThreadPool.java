package net.atayun.bazooka.deploy.biz.v2.service.app.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.concurrent.*;

/**
 * @author Ping
 */
@Slf4j
public class AppActionThreadPool {

    private static final int APP_COMMAND_POOL_CORE_POOL_SIZE = 10;
    private static final int APP_COMMAND_POOL_MAXIMUM_POOL_SIZE = 100;
    private static final int APP_COMMAND_POOL_BLOCKING_QUEUE_CAPACITY = 512;
    private static final int APP_COMMAND_POOL_KEEP_ALIVE_TIME = 0;
    private static final boolean APP_COMMAND_POOL_PRE_START_CORE_THREAD = false;

    private static ThreadPoolExecutor APP_COMMAND_THREAD_POOL_EXECUTOR;

    public AppActionThreadPool() {
        TimeUnit milliseconds = TimeUnit.MILLISECONDS;
        LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>(APP_COMMAND_POOL_BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("app-cmd-pool-%d")
                .build();
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();
        APP_COMMAND_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                APP_COMMAND_POOL_CORE_POOL_SIZE,
                APP_COMMAND_POOL_MAXIMUM_POOL_SIZE,
                APP_COMMAND_POOL_KEEP_ALIVE_TIME,
                milliseconds,
                linkedBlockingQueue,
                threadFactory,
                abortPolicy);
        if (APP_COMMAND_POOL_PRE_START_CORE_THREAD) {
            APP_COMMAND_THREAD_POOL_EXECUTOR.prestartAllCoreThreads();
        }
    }

    public static void execute(@NotNull Runnable runnable) {
        try {
            APP_COMMAND_THREAD_POOL_EXECUTOR.execute(runnable);
        } catch (RejectedExecutionException ree) {
            log.warn("新任务被拒绝执行", ree);
            throw new BizException("", "新任务被拒绝执行", ree);
        }
    }
}

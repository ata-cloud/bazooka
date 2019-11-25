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
public class FlowStepThreadPool {

    private static final int FLOW_STEP_POOL_CORE_POOL_SIZE = 20;
    private static final int FLOW_STEP_POOL_MAXIMUM_POOL_SIZE = 200;
    private static final int FLOW_STEP_POOL_BLOCKING_QUEUE_CAPACITY = 512;
    private static final int FLOW_STEP_POOL_KEEP_ALIVE_TIME = 0;
    private static final boolean FLOW_STEP_POOL_PRE_START_CORE_THREAD = false;

    private static ThreadPoolExecutor FLOW_STEP_THREAD_POOL_EXECUTOR;

    public FlowStepThreadPool() {
        TimeUnit milliseconds = TimeUnit.MILLISECONDS;
        LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>(FLOW_STEP_POOL_BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("flow-step-pool-%d")
                .build();
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();
        FLOW_STEP_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                FLOW_STEP_POOL_CORE_POOL_SIZE,
                FLOW_STEP_POOL_MAXIMUM_POOL_SIZE,
                FLOW_STEP_POOL_KEEP_ALIVE_TIME,
                milliseconds,
                linkedBlockingQueue,
                threadFactory,
                abortPolicy);
        if (FLOW_STEP_POOL_PRE_START_CORE_THREAD) {
            FLOW_STEP_THREAD_POOL_EXECUTOR.prestartAllCoreThreads();
        }
    }

    public static void execute(@NotNull Runnable runnable) {
        try {
            FLOW_STEP_THREAD_POOL_EXECUTOR.execute(runnable);
        } catch (RejectedExecutionException ree) {
            log.warn("新任务被拒绝执行", ree);
            throw new BizException("", "新任务被拒绝执行", ree);
        }
    }
}

package zhuhp.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuhuapeng on 18-12-7.
 */
public class SemaphoreDemo {

    public static final int THREAD_COUNT = 30;
    public static ExecutorService threadPool =
            Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        final Semaphore semaphore = new Semaphore(10);
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println("i got it! current available is " + semaphore.availablePermits());
                        atomicInteger.incrementAndGet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }

                }
            });
        }
        System.out.println(atomicInteger.get());
        threadPool.shutdown();
    }

}

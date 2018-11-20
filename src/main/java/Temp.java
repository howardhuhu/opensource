import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhuhuapeng on 18-7-4.
 */
public class Temp {

    private AtomicLong atomicLong = new AtomicLong(0l);

    private static Unsafe theUnsafe;

    static {
        Field unsafe = null;
        try {
            unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);
            theUnsafe = (Unsafe) unsafe.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Temp() throws Exception {
        countOffSet = theUnsafe.objectFieldOffset(Temp.class.getDeclaredField("count"));
    }

    private volatile long count = 0l;
    private long countOffSet;


    public long increment() throws Exception {
        long result = count;
        while (!theUnsafe.compareAndSwapLong(this, countOffSet, result, result + 1)) {
            result = count;
        }
        return count;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        final Temp temp = new Temp();

        ExecutorService incrementThreadSafeTestService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        final int cycle = 100000;
        final CountDownLatch latch = new CountDownLatch(cycle);

        final ArrayBlockingQueue<Long> outputQueue = new ArrayBlockingQueue<Long>(100000);
        for (int i = 0; i < cycle; i++) {
            incrementThreadSafeTestService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        long val  = temp.atomicLong.getAndIncrement();
                        outputQueue.offer(val);
                        latch.countDown();

//                        long val = temp.increment();
//                        outputQueue.offer(val);
//                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        Set<Long> outputSet = new HashSet<>();
        latch.await();
        System.out.println("queue size:" + outputQueue.size());
        while (!outputQueue.isEmpty()) {
            outputSet.add(outputQueue.take());
        }


        incrementThreadSafeTestService.shutdown();
        long endTime = System.currentTimeMillis();
        System.out.println("cost :" + (endTime - startTime) + ":ms");
        System.out.println(outputSet.size());
    }
}

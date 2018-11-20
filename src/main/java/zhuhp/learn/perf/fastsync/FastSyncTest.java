package zhuhp.learn.perf.fastsync;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by zhuhuapeng on 18-3-21.
 */
public class FastSyncTest {

    public static void main(String[] args) throws NoSuchFieldException {
        TestRunner runner = new TestRunner(new Counter(),100000000);
        new Thread(runner).start();


    }

    static class TestRunner implements  Runnable{

        Counter counter ;
        long num ;

        public  TestRunner ( Counter counter, long num){
            this.counter = counter ;
            this.num = num ;
        }

        @Override
        public void run() {
            long starttime = System.nanoTime() ;
          for(long i  = 0 ; i < num ; i ++ ){
                 counter.increment();
          }
            System.out.println("time cost:"+(System.nanoTime() - starttime)/1000000 + " ns!");
            System.out.println(counter.getCount());

        }
    }

    static Unsafe unsafe;
    static{
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe =  (Unsafe) field.get(null);
        }catch(Exception e )
        {
            e.printStackTrace();
        }
    }

   static class Counter{

        long counterFieldOffset ;

        public Counter() throws NoSuchFieldException {
            counterFieldOffset =   unsafe.objectFieldOffset(Counter.class.getDeclaredField("count"));
        }

        long count ;
        public void increment(){
            long start = count ;
            while(!unsafe.compareAndSwapLong(this,counterFieldOffset,start,start+1)){
                start = count ;
            }
        }

        public  long getCount(){
            return count;
        }

    }
}

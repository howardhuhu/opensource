import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by zhuhuapeng on 18-3-6.
 */
public class TestMain {

    public static void main(String[] args) throws  Exception{
        Guard guard = new Guard();
        System.out.println(  guard.giveAccess());


        Unsafe unsafe = getUnsafe();
        Field f = guard.getClass().getDeclaredField("ACCESS_ALLOWED");
        unsafe.putInt(guard, unsafe.objectFieldOffset(f), 42); // memory corruption
        System.out.println(  guard.giveAccess());
    }

    private static Unsafe getUnsafe() throws Exception
    {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }


   static class Guard {
        private int ACCESS_ALLOWED = 1;

        public boolean giveAccess() {
            return 42 == ACCESS_ALLOWED;
        }
    }

}

package zhuhp.learn.perf.serializable;

/**
 * Created by zhuhuapeng on 18-3-19.
 */
public abstract class PerformanceTestCase {
    private final String name;
    private final int repetitions;
    private final ObjectToBeSerialised testInput;
    private ObjectToBeSerialised testOutput;
    private long writeTimeNanos;
    private long readTimeNanos;

    public PerformanceTestCase(final String name, final int repetitions,
                               final ObjectToBeSerialised testInput)
    {
        this.name = name;
        this.repetitions = repetitions;
        this.testInput = testInput;
    }

    public String getName()
    {
        return name;
    }

    public ObjectToBeSerialised getTestOutput()
    {
        return testOutput;
    }

    public long getWriteTimeNanos()
    {
        return writeTimeNanos;
    }

    public long getReadTimeNanos()
    {
        return readTimeNanos;
    }

    public void performTest() throws Exception
    {
        final long startWriteNanos = System.nanoTime();
        testWrite(testInput);
        writeTimeNanos = (System.nanoTime() - startWriteNanos) / repetitions;

        final long startReadNanos = System.nanoTime();
        testOutput = testRead();
        readTimeNanos = (System.nanoTime() - startReadNanos) / repetitions;
    }

    public abstract void testWrite(ObjectToBeSerialised item) throws Exception;
    public abstract ObjectToBeSerialised testRead() throws Exception;
}

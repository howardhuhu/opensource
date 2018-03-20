package zhuhp.learn.perf.serializable;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by zhuhuapeng on 18-3-19.
 */
public class ObjectToBeSerialised implements Serializable {
    private static final long serialVersionUID = 10275539472837495L;

    private final long sourceId;
    private final boolean special;
    private final int orderCode;
    private final int priority;
    private final double[] prices;
    private final long[] quantities;

    public ObjectToBeSerialised(final long sourceId, final boolean special,
                                final int orderCode, final int priority,
                                final double[] prices, final long[] quantities)
    {
        this.sourceId = sourceId;
        this.special = special;
        this.orderCode = orderCode;
        this.priority = priority;
        this.prices = prices;
        this.quantities = quantities;
    }

    public void write(final ByteBuffer byteBuffer)
    {
        byteBuffer.putLong(sourceId);
        byteBuffer.put((byte)(special ? 1 : 0));
        byteBuffer.putInt(orderCode);
        byteBuffer.putInt(priority);

        byteBuffer.putInt(prices.length);

//        byteBuffer.asDoubleBuffer().put(prices);
//        byteBuffer.position(byteBuffer.position() + prices.length * 8 );
        for (final double price : prices)
        {
            byteBuffer.putDouble(price);
        }

        byteBuffer.putInt(quantities.length);
        for (final long quantity : quantities)
        {
            byteBuffer.putLong(quantity);
        }
    }

    public static ObjectToBeSerialised read(final ByteBuffer byteBuffer)
    {
        final long sourceId = byteBuffer.getLong();
        final boolean special = 0 != byteBuffer.get();
        final int orderCode = byteBuffer.getInt();
        final int priority = byteBuffer.getInt();

        final int pricesSize = byteBuffer.getInt();
        final double[] prices = new double[pricesSize];
        for (int i = 0; i < pricesSize; i++)
        {
            prices[i] = byteBuffer.getDouble();
        }

        final int quantitiesSize = byteBuffer.getInt();
        final long[] quantities = new long[quantitiesSize];
        for (int i = 0; i < quantitiesSize; i++)
        {
            quantities[i] = byteBuffer.getLong();
        }

        return new ObjectToBeSerialised(sourceId, special, orderCode,
                priority, prices, quantities);
    }

    public void write(final UnsafeMemory buffer)
    {
        buffer.putLong(sourceId);
        buffer.putBoolean(special);
        buffer.putInt(orderCode);
        buffer.putInt(priority);
        buffer.putDoubleArray(prices);
        buffer.putLongArray(quantities);
    }

    public static ObjectToBeSerialised read(final UnsafeMemory buffer)
    {
        final long sourceId = buffer.getLong();
        final boolean special = buffer.getBoolean();
        final int orderCode = buffer.getInt();
        final int priority = buffer.getInt();
        final double[] prices = buffer.getDoubleArray();
        final long[] quantities = buffer.getLongArray();

        return new ObjectToBeSerialised(sourceId, special, orderCode,
                priority, prices, quantities);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final ObjectToBeSerialised that = (ObjectToBeSerialised)o;

        if (orderCode != that.orderCode)
        {
            return false;
        }
        if (priority != that.priority)
        {
            return false;
        }
        if (sourceId != that.sourceId)
        {
            return false;
        }
        if (special != that.special)
        {
            return false;
        }
        if (!Arrays.equals(prices, that.prices))
        {
            return false;
        }
        if (!Arrays.equals(quantities, that.quantities))
        {
            return false;
        }

        return true;
    }
}

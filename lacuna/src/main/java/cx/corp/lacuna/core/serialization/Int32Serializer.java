package cx.corp.lacuna.core.serialization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Int32Serializer extends AbstractTypeSerializer<Integer> {

    private static final int DATA_LENGTH = 4;

    public Int32Serializer() {
        super(DATA_LENGTH);
    }

    @Override
    public byte[] serialize(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        ByteBuffer buf = ByteBuffer.allocate(DATA_LENGTH);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(value);
        buf.flip();

        byte[] ret = new byte[DATA_LENGTH];
        buf.get(ret, 0, DATA_LENGTH);
        return ret;
    }

    @Override
    public Integer deserialize(byte[] data) {
        if (!canDeserialize(data)) {
            throw new IllegalArgumentException("Data cannot be deserialized");
        }

        ByteBuffer buf = ByteBuffer.allocate(DATA_LENGTH);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(data);
        buf.flip();
        return buf.getInt(0);
    }
}

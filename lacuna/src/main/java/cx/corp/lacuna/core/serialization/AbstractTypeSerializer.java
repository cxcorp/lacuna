package cx.corp.lacuna.core.serialization;

public abstract class AbstractTypeSerializer<T> implements TypeSerializer<T> {

    private final int dataByteLength;

    protected AbstractTypeSerializer(int dataLength) {
        this.dataByteLength = dataLength;
    }

    protected void throwIfNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument cannot be null!");
        }
    }

    @Override
    public boolean canDeserialize(byte[] data) {
        return data != null && data.length == dataByteLength;
    }
}

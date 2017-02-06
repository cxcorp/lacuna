package cx.corp.lacuna.core.serialization;

public class Boolean8Serializer extends AbstractTypeSerializer<Boolean> {

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    public Boolean8Serializer() {
        super(1);
    }

    @Override
    public byte[] serialize(Boolean value) {
        throwIfNull(value);
        return new byte[] { value ? TRUE : FALSE };
    }

    @Override
    public Boolean deserialize(byte[] data) {
        if (!canDeserialize(data)) {
            throw new IllegalArgumentException("Specified data cannot be deserialized!");
        }
        return data[0] == TRUE;
    }

    @Override
    public boolean canDeserialize(byte[] data) {
        return super.canDeserialize(data)
            && (data[0] == TRUE || data[0] == FALSE);
    }
}

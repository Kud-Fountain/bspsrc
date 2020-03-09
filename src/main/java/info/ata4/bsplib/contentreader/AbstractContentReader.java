package info.ata4.bsplib.contentreader;

public abstract class AbstractContentReader<T> implements LumpContentReader<T> {

    private final T nullData;

    public AbstractContentReader(T nullData) {
        this.nullData = nullData;
    }

    @Override
    public T nullData() {
        return nullData;
    }
}

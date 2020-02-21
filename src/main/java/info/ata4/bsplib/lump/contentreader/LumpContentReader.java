package info.ata4.bsplib.lump.contentreader;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface LumpContentReader<T> {
    T read(ByteBuffer buffer) throws IOException;
    T nullData();
}

package info.ata4.bsplib.lump.contentreader;

import info.ata4.log.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class BytesContentReader extends AbstractContentReader<byte[]> {

    private static final Logger L = LogUtils.getLogger();

    public BytesContentReader() {
        super(new byte[0]);
    }

    @Override
    public byte[] read(ByteBuffer buffer) throws IOException {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        L.fine(String.format("%s: %d bytes read", getClass(), data.length));
        return data;
    }
}

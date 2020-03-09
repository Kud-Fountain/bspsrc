package info.ata4.bsplib.contentreader;

import info.ata4.io.DataReader;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class BytesContentReader extends AbstractContentReader<byte[]> {

    private static final Logger L = LogUtils.getLogger();

    public BytesContentReader() {
        super(new byte[0]);
    }

    @Override
    public byte[] read(DataReader in) throws IOException {
        byte[] data = new byte[Math.toIntExact(in.remaining())];
        in.readBytes(data);

        L.fine(String.format("%s: %d bytes read", getClass(), data.length));
        return data;
    }
}

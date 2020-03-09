package info.ata4.bsplib.contentreader;

import info.ata4.io.DataReader;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractPacketContentReader<E> extends AbstractContentReader<List<E>> {

    private static final Logger L = LogUtils.getLogger();

    public AbstractPacketContentReader() {
        super(Collections.emptyList());
    }

    @Override
    public List<E> read(DataReader in) throws IOException {
        return read(in, Math.toIntExact(in.remaining() / packetSize()));
    }

    public List<E> read(DataReader in, int packetCount) throws IOException {
        List<E> packets = new ArrayList<>(packetCount);

        for (int i = 0; i < packetCount; i++) {
            E packet = readPacket(in);
            packets.add(packet);
        }

        String packetClassName = packets.size() == 0 ? "-" : packets.get(0).getClass().getSimpleName();
        L.fine(String.format("%s: %d packets read", packetClassName, packets.size()));
        return packets;
    }

    protected abstract int packetSize();
    protected abstract E readPacket(DataReader in) throws IOException;
}

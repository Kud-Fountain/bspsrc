package info.ata4.bsplib.contentreader;

import info.ata4.bsplib.struct.DStruct;
import info.ata4.io.DataReader;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DStructPacketsContentReader<E extends DStruct> extends AbstractPacketContentReader<E> {

    private static final Logger L = LogUtils.getLogger();

    private final Supplier<E> dStructSupplier;
    protected final int packetSize;

    public DStructPacketsContentReader(Supplier<E> dStructSupplier) {
        this.dStructSupplier = Objects.requireNonNull(dStructSupplier);
        this.packetSize = dStructSupplier.get().getSize();
    }

    @Override
    protected int packetSize() {
        return packetSize;
    }

    @Override
    protected E readPacket(DataReader in) throws IOException {
        E dStruct = dStructSupplier.get();

        long pos = in.position();
        dStruct.read(in);

        if (in.position() - pos != packetSize) {
            throw new IOException(String.format("DStruct bytes read: %d; expected: %d", pos, packetSize));
        }

        return dStruct;
    }
}

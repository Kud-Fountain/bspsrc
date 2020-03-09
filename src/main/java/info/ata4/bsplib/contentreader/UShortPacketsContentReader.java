package info.ata4.bsplib.contentreader;

import info.ata4.io.DataReader;

import java.io.IOException;

public class UShortPacketsContentReader extends AbstractPacketContentReader<Integer> {

    @Override
    protected int packetSize() {
        return 2;
    }

    @Override
    protected Integer readPacket(DataReader in) throws IOException {
        return in.readUnsignedShort();
    }
}

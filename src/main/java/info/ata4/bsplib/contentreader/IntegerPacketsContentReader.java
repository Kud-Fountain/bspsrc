package info.ata4.bsplib.contentreader;

import info.ata4.io.DataReader;

import java.io.IOException;

public class IntegerPacketsContentReader extends AbstractPacketContentReader<Integer> {

    @Override
    protected int packetSize() {
        return 4;
    }

    @Override
    protected Integer readPacket(DataReader in) throws IOException {
        return in.readInt();
    }
}

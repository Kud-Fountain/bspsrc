package info.ata4.bsplib.lump.contentreader;

import info.ata4.io.DataReader;

import java.io.IOException;

public abstract class IntegerPacketsContentReader extends AbstractPacketContentReader<Integer> {

    @Override
    protected Integer readPacket(DataReader in) throws IOException {
        return in.readInt();
    }

    public static IntegerPacketsContentReader forAllBytes() {
        return new IntegerPacketsContentReader() {
            @Override
            protected int packetCount(int remainingBytes) {
                return remainingBytes / 4;
            }
        };
    }

    public static IntegerPacketsContentReader forCount(int count) {
        return new IntegerPacketsContentReader() {
            @Override
            protected int packetCount(int remainingBytes) {
                return count;
            }
        };
    }
}

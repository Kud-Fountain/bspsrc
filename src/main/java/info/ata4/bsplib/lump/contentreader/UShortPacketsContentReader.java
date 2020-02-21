package info.ata4.bsplib.lump.contentreader;

import info.ata4.io.DataReader;

import java.io.IOException;

public abstract class UShortPacketsContentReader extends AbstractPacketContentReader<Integer> {

    @Override
    protected Integer readPacket(DataReader in) throws IOException {
        return in.readUnsignedShort();
    }

    public static UShortPacketsContentReader forAllBytes() {
        return new UShortPacketsContentReader() {
            @Override
            protected int packetCount(int remainingBytes) {
                return remainingBytes / 2;
            }
        };
    }

    public static UShortPacketsContentReader forCount(int count) {
        return new UShortPacketsContentReader() {
            @Override
            protected int packetCount(int remainingBytes) {
                return count;
            }
        };
    }
}

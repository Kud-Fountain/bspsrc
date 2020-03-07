package info.ata4.bsplib.lump.contentreader;

import info.ata4.bsplib.struct.LevelFlag;
import info.ata4.io.DataReader;
import info.ata4.log.LogUtils;
import info.ata4.util.EnumConverter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

public class FlagsLumpContentReader extends AbstractContentReader<Set<LevelFlag>> {
    private static final Logger L = LogUtils.getLogger();

    public FlagsLumpContentReader() {
        super(Collections.emptySet());
    }

    @Override
    public Set<LevelFlag> read(DataReader in) throws IOException {
        Set<LevelFlag> mapFlags = EnumConverter.fromInteger(LevelFlag.class, in.readInt());

        if (in.hasRemaining()) {
            L.warning(String.format("%d bytes remaining", in.remaining()));
        }

        L.fine("Map flags: " + mapFlags);
        return mapFlags;
    }
}

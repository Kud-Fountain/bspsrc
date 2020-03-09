package info.ata4.bsplib.contentreader;

import info.ata4.io.DataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TexdataStringDataLumpContentReader extends AbstractContentReader<List<String>> {

    private final List<Integer> stringTableIndices;

    public TexdataStringDataLumpContentReader(List<Integer> stringTableIndices) {
        super(Collections.emptyList());
        this.stringTableIndices = Objects.requireNonNull(stringTableIndices);
    }

    @Override
    public List<String> read(DataReader in) throws IOException {
        List<String> texnames = new ArrayList<>(stringTableIndices.size());
        for (int stringTableIndex : stringTableIndices) {
            in.position(stringTableIndex);
            texnames.add(in.readStringNull(Math.toIntExact(in.remaining())));
        }

        // Set DataReader position to end to prevent log message "Lump %s has %d bytes remaining..."
        in.position(in.position() + in.remaining());
        return texnames;
    }
}

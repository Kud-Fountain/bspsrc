package info.ata4.bsplib.contentreader;

import info.ata4.io.DataReader;

import java.io.IOException;

public interface LumpContentReader<T> {
    T read(DataReader reader) throws IOException;
    T nullData();
}

package info.ata4.bsplib.lump.contentreader;

import info.ata4.bsplib.struct.DOccluderData;
import info.ata4.bsplib.struct.DOccluderPolyData;
import info.ata4.io.DataReader;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class OcclusionLumpContentReader<DATA extends DOccluderData>
        extends AbstractContentReader<OcclusionLumpContentReader.OcclusionData<DATA>> {

    private static final Logger L = LogUtils.getLogger();

    private final Supplier<DATA> dOccluderDataSupplier;

    public OcclusionLumpContentReader(Supplier<DATA> dOccluderDataSupplier) {
        super(new OcclusionData<>());

        this.dOccluderDataSupplier = Objects.requireNonNull(dOccluderDataSupplier);
    }

    @Override
    public OcclusionData<DATA> read(DataReader in) throws IOException {
        int dOcculderDataCount = in.readInt();
        List<DATA> dOccluderData = DStructPacketsContentReader.forCount(dOccluderDataSupplier, dOcculderDataCount)
                .read(in);

        int dOccluderPolyCount = in.readInt();
        List<DOccluderPolyData> dOccluderPolyData =
                DStructPacketsContentReader.forCount(DOccluderPolyData::new, dOccluderPolyCount).read(in);

        int vertexCount = in.readInt();
        List<Integer> vertexIndices = IntegerPacketsContentReader.forCount(vertexCount).read(in);

        return new OcclusionData<>(dOccluderData, dOccluderPolyData, vertexIndices);
    }

    public static class OcclusionData<DATA extends DOccluderData> {

        public final List<DATA> dOccluderData;
        public final List<DOccluderPolyData> dOccluderPolyData;
        public final List<Integer> vertexIndices;

        public OcclusionData() {
            this(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }

        public OcclusionData(List<DATA> dOccluderData, List<DOccluderPolyData> dOccluderPolyData,
                             List<Integer> vertexIndices) {
            this.dOccluderData = Objects.requireNonNull(dOccluderData);
            this.dOccluderPolyData = Objects.requireNonNull(dOccluderPolyData);
            this.vertexIndices = Objects.requireNonNull(vertexIndices);
        }
    }
}

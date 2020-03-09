package info.ata4.bsplib.contentreader;

import info.ata4.bsplib.entity.Entity;
import info.ata4.bsplib.io.EntityInputStream;
import info.ata4.io.DataReader;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class EntityLumpContentReader extends AbstractContentReader<List<Entity>> {

    private static final Logger L = LogUtils.getLogger();

    private final boolean allowEscSeq;

    public EntityLumpContentReader(boolean allowEscSeq) {
        super(Collections.emptyList());

        this.allowEscSeq = allowEscSeq;
    }

    @Override
    public List<Entity> read(DataReader in) throws IOException {
        List<Entity> entities = new ArrayList<>();
        try (EntityInputStream entReader = new EntityInputStream(in.stream())) {
            // allow escaped quotes for VTBM
            entReader.setAllowEscSeq(allowEscSeq);

            Entity ent;
            while ((ent = entReader.readEntity()) != null) {
                entities.add(ent);
            }
        }

        L.fine("Entities: " + entities.size());
        return entities;
    }
}

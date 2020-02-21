package info.ata4.bsplib.lump.contentreader;

import info.ata4.bsplib.entity.Entity;
import info.ata4.bsplib.io.EntityInputStream;
import info.ata4.io.buffer.ByteBufferInputStream;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
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
    public List<Entity> read(ByteBuffer buffer) throws IOException {
        List<Entity> entities = new ArrayList<>();
        try (EntityInputStream entReader = new EntityInputStream(new ByteBufferInputStream(buffer))) {
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

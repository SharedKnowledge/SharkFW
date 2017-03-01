package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpSharkGeometry implements SharkGeometry {

    private final DumpSharkKB kb;
    private final SharkGeometry sharkGeometry;

    public DumpSharkGeometry(DumpSharkKB kb, SharkGeometry sharkGeometry) {
        this.kb = kb;
        this.sharkGeometry = sharkGeometry;
    }

    @Override
    public String getWKT() {
        return this.sharkGeometry.getWKT();
    }

    @Override
    public String getEWKT() {
        return this.sharkGeometry.getEWKT();
    }

    @Override
    public int getSRS() {
        return this.sharkGeometry.getSRS();
    }
}

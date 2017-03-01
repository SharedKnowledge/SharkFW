package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpSharkGeometry implements SharkGeometry {

    private final FileDumpSharkKB kb;
    private final SharkGeometry sharkGeometry;

    public FileDumpSharkGeometry(FileDumpSharkKB kb, SharkGeometry sharkGeometry) {
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

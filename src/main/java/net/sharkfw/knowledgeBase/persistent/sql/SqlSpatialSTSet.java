//package net.sharkfw.knowledgeBase.persistent.sql;
//
//import net.sharkfw.knowledgeBase.FragmentationParameter;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.SpatialSTSet;
//import net.sharkfw.knowledgeBase.SpatialSemanticTag;
//import net.sharkfw.knowledgeBase.geom.SharkGeometry;
//import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
//import net.sharkfw.knowledgeBase.inmemory.InMemoSpatialSemanticTag;
//
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Enumeration;
//import java.util.List;
//
///**
// * Created by dfe on 24.05.2017.
// */
//public class SqlSpatialSTSet extends SqlSTSet implements SpatialSTSet {
//
//    public SqlSpatialSTSet(SqlSharkKB sharkKB) throws SQLException {
//        super(sharkKB, "spatial", null);
//    }
//
//    public SqlSpatialSTSet(SqlSharkKB sharkKB, int id) {
//        super(sharkKB, id);
//    }
//
//    @Override
//    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException {
//        return null;
//    }
//
//    @Override
//    public double getDistance(SpatialSemanticTag gc1, SpatialSemanticTag gc2) {
//        return 0;
//    }
//
//    @Override
//    public boolean isInRange(SpatialSemanticTag gc1, SpatialSemanticTag gc2, double radius) {
//        return false;
//    }
//
//    @Override
//    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry geom) throws SharkKBException {
//        try {
//            return new SqlSpatialSemanticTag(si, name, getSqlSharkKB(), geom.getWKT());
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new SharkKBException();
//        }
//    }
//
//    @Override
//    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry[] geoms) throws SharkKBException {
//        return createSpatialSemanticTag(name, si, geoms[0]);
//    }
//
//    @Override
//    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
//        return getSpatialSemanticTag(sis[0]);
//    }
//
//    @Override
//    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException {
//        SqlSemanticTag tag = new SqlSemanticTag(si, getSqlSharkKB());
//        return new SqlSpatialSemanticTag(tag.getId(), getSqlSharkKB());
//
//    }
//
//    @Override
//    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException {
////        DSLContext getTags = DSL.using(this.getConnection(), SQLDialect.SQLITE);
////        String tags = getTags.selectFrom(table("semantic_tag")).where(field("tag_kind").eq(inline("spatial"))).and(field("tag_set").eq(inline(this.getStSetID()))).getSQL();
////        ResultSet rs = null;
//        List<SpatialSemanticTag> list = new ArrayList<>();
//        try {
//            rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags);
//            while (rs.next()) {
//                list.add(new InMemoSpatialSemanticTag(InMemoSharkGeometry.createGeomByWKT(rs.getString("wkt"))));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new SharkKBException();
//        }
//        return Collections.enumeration(list);
//    }
//
//}

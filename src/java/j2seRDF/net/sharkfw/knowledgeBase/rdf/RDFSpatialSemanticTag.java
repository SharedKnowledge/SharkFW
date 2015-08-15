package net.sharkfw.knowledgeBase.rdf;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * 
 * @author Barret dfe
 *
 */
public class RDFSpatialSemanticTag extends RDFSemanticTag implements SpatialSemanticTag {

	private SharkGeometry sg;

	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/

	public RDFSpatialSemanticTag(RDFSharkKB kb, String[] si, String topic, SharkGeometry sg) {
		super(si, topic);
		this.sg = sg;
		Dataset dataset = kb.getDataset();
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(RDFConstants.SPATIAL_MODEL_NAME);
			try {
				Statement s = m.createStatement(m.createResource(si[i]), m.createProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
				m.add(s);
				m.getResource(si[i]).addProperty(m.createProperty(RDFConstants.SPATIAL_TAG_PREDICATE),
						m.createResource(si[i] + RDFConstants.SPATIAL_TAG_OBJECT_NAME_GEOMETRY));

				m.createResource(si[i] + RDFConstants.SPATIAL_TAG_OBJECT_NAME_GEOMETRY).addProperty(m.createProperty(RDFConstants.SPATIAL_TAG_EWKT), sg.getEWKT());

				m.createResource(si[i] + RDFConstants.SPATIAL_TAG_OBJECT_NAME_GEOMETRY).addProperty(m.createProperty(RDFConstants.SPATIAL_TAG_WKT), sg.getWKT());

				m.createResource(si[i] + RDFConstants.SPATIAL_TAG_OBJECT_NAME_GEOMETRY).addProperty(m.createProperty(RDFConstants.SPATIAL_TAG_SRS),
						Integer.toString(sg.getSRS()));

				dataset.commit();

			} finally {
				dataset.end();
			}
		}
	}

	/**********
	 * RDFKB-READ (read in db) CONSTRUCTOR
	 * 
	 * @throws SharkKBException
	 **********/

	public RDFSpatialSemanticTag(RDFSharkKB kb, String si) throws SharkKBException {
		super(kb, si, RDFConstants.SPATIAL_MODEL_NAME);
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.SPATIAL_MODEL_NAME);
		StmtIterator resultWKT = m.listStatements(m.getResource(si + RDFConstants.SPATIAL_TAG_OBJECT_NAME_GEOMETRY), m.getProperty(RDFConstants.SPATIAL_TAG_WKT),
				(String) null);
		dataset.end();
		sg = InMemoSharkGeometry.createGeomByWKT(resultWKT.next().getObject().toString());
	}

	@Override
	public SharkGeometry getGeometry() {
		return sg;
	}
	
	@Override
	public void addSI(String si) throws SharkKBException {

		addSIModelIndependenent(si, RDFConstants.SPATIAL_MODEL_NAME);
	}
	
	@Override
	public void removeSI(String si) throws SharkKBException {
		
		this.removeSIModelIndependenent(si, RDFConstants.SPATIAL_MODEL_NAME);
	}

}

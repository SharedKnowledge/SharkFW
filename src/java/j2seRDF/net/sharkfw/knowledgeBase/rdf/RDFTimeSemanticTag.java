package net.sharkfw.knowledgeBase.rdf;

import net.sharkfw.knowledgeBase.TimeSemanticTag;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author Barret dfe
 *
 */
public class RDFTimeSemanticTag extends RDFSemanticTag implements TimeSemanticTag {

	private long from, duration;
	
	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/
	
	public RDFTimeSemanticTag(RDFSharkKB kb, long from, long duration) {
		super(kb ,new String[] {RDFConstants.TIME_TAG + "/from:" + from + "/duration:" + duration},"timeST", RDFConstants.TIME_MODEL_NAME);
		Dataset dataset = kb.getDataset();
		this.from = from;
		this.duration = duration;
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.TIME_MODEL_NAME);
		try {
			Resource r = m.getResource(RDFConstants.TIME_TAG + "/from:" + from + "/duration:" + duration);
			r.addProperty(m.createProperty(RDFConstants.TIME_TAG_FROM), String.valueOf(from));	
			r.addProperty(m.createProperty(RDFConstants.TIME_TAG_DURATION), String.valueOf(duration));
			dataset.commit();
		} finally {
			dataset.end();
		}
	}	
	
	
	
	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public long getFrom() {
		return from;
	}

}

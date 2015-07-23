package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;

import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

public class RDFSNSemanticTag implements SNSemanticTag {

	private RDFSemanticTag tag;
	
	public RDFSNSemanticTag(RDFSemanticTag tag) throws SharkKBException {
		if (tag != null) {
			this.tag = tag;
		}
		else {
			throw new SharkKBException("Invalid parameter was given for RDFSNSemanticTag()");
		}
	}
	
	public RDFSemanticTag getTag() {
		return tag;
	}

	public void setTag(RDFSemanticTag tag) {
		this.tag = tag;
	}

	@Override
	public String getName() {
		return tag.getName();
	}

	@Override
	public String[] getSI() {
		return tag.getSi();
	}
	

	@Override
	public Enumeration<String> predicateNames() {
	
		return null;
	}

	@Override
	public Enumeration<String> targetPredicateNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<SNSemanticTag> targetTags(String predicateName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPredicate(String type, SNSemanticTag target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePredicate(String type, SNSemanticTag target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSI(String si) throws SharkKBException {
		tag.removeSI(si);
		
	}

	@Override
	public void addSI(String si) throws SharkKBException {
		tag.addSI(si);
		
	}

	@Override
	public void setName(String newName) {
		tag.setName(newName);
	}

	@Override
	public void merge(SemanticTag st) {
		tag.merge(st);
		
	}

	@Override
	public void setHidden(boolean isHidden) {
		tag.setHidden(isHidden);
	}

	@Override
	public boolean hidden() {
		return tag.hidden();
	}

	@Override
	public boolean isAny() {
		return tag.isAny();
	}

	@Override
	public boolean identical(SemanticTag other) {
		return tag.identical(other);
	}

	@Override
	public void setSystemProperty(String name, String value) {
		tag.setSystemProperty(name, value);
	}

	@Override
	public String getSystemProperty(String name) {
		return tag.getSystemProperty(name);
	}

	@Override
	public void setProperty(String name, String value) throws SharkKBException {
		tag.setProperty(name, value);
	}

	@Override
	public String getProperty(String name) throws SharkKBException {
		return tag.getProperty(name);
	}

	@Override
	public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
		tag.setProperty(name, value, transfer);
	}

	@Override
	public void removeProperty(String name) throws SharkKBException {
		tag.removeProperty(name);
	}

	@Override
	public Enumeration<String> propertyNames() throws SharkKBException {
		return tag.propertyNames();
	}

	@Override
	public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
		return tag.propertyNames(all);
	}


	@Override
	public void merge(SNSemanticTag toMerge) {
		throw new UnsupportedOperationException("This method is not supported yet.");
		
	}

}

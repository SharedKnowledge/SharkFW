package net.sharkfw.knowledgeBase.rdf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

public class RDFInformation implements Information {

	private long creationTime;
	
	private byte[] contentAsByte;
	
	private String contentAsString;
	
	private long contentLength;
	
	private String contentType;
	
	@Override
	public long creationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getContentAsByte() {
		return contentAsByte;
	}

	@Override
	public String getContentAsString() throws SharkKBException {
		return contentAsString;
	}

	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}
	
	@Override
	public void setContent(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContent(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContent(InputStream arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void streamContent(OutputStream arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public InputStream getInputStream() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueID() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeContent() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public long lastModified() {
		return 0;
	}
	
	@Override
	public String getSystemProperty(String arg0) {
		throw new UnsupportedOperationException("System Properties are not yet supported for the RDFSharkKB!");
	}

	@Override
	public void setSystemProperty(String arg0, String arg1) {
		throw new UnsupportedOperationException("System Properties are not yet supported for the RDFSharkKB!");
	}

	@Override
	public String getProperty(String arg0) throws SharkKBException {
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}

	@Override
	public Enumeration<String> propertyNames() throws SharkKBException {
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}
	

	@Override
	public Enumeration<String> propertyNames(boolean arg0) throws SharkKBException {
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}
	

	@Override
	public void removeProperty(String arg0) throws SharkKBException{
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}

	@Override
	public void setProperty(String arg0, String arg1){
		throw new UnsupportedOperationException("Properties are not yet supported for Information in the RDFSharkKB!");
	}
		
	

	@Override
	public void setProperty(String arg0, String arg1, boolean arg2) throws SharkKBException{
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}


}

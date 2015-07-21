package net.sharkfw.knowledgeBase.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;

public class RDFInformation implements Information {

	@SuppressWarnings("unused")
	private RDFSharkKB kb;

	private long creationTime;

	private byte[] contentAsByte;

	private String contentAsString;

	private long contentLength;

	private String contentType;

	public RDFInformation(RDFSharkKB kb, AnonId contextPointID, byte[] content) throws SharkKBException {
		FileOutputStream out = null;
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.INFORMATION_MODEL_NAME);
		try {
			File contentFile = new File(kb.getDirectory() + "\\" + contextPointID.toString());
			try {
				out = new FileOutputStream(contentFile);
				out.write(content);
			} catch (FileNotFoundException e) {
				throw new SharkKBException(e.getMessage());
			} catch (IOException e) {
				throw new SharkKBException(e.getMessage());
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					throw new SharkKBException(e.getMessage());
				}
			}
			m.createStatement(m.createResource(RDFConstants.INFORMATION_SUBJECT + "/" + contextPointID.toString()),
					m.createProperty(RDFConstants.INFORMATION_PREDICATE), contextPointID.toString());
		} finally {
			dataset.close();

		}
	}

	@Override
	public long creationTime() {
		return creationTime;
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
		return contentLength;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void removeContent() {
		// TODO Auto-generated method stub

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
	public void removeProperty(String arg0) throws SharkKBException {
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}

	@Override
	public void setProperty(String arg0, String arg1) {
		throw new UnsupportedOperationException("Properties are not yet supported for Information in the RDFSharkKB!");
	}

	@Override
	public void setProperty(String arg0, String arg1, boolean arg2) throws SharkKBException {
		throw new SharkKBException("Properties are not yet supported for Information in the RDFSharkKB!");
	}

}

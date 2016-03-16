package net.sharkfw.knowledgeBase.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.rdf.model.AnonId;

public class RDFInformation implements Information {

	private long creationTime;

	private byte[] content;

	private String contentAsString;

	private long contentLength;

	private String contentType;

	private String path;

	public RDFInformation(AnonId contextPointID, byte[] content) throws SharkKBException {
		path = RDFConstants.INFORMATION_PATH + contextPointID.toString().replaceAll(":", "");
		this.content = content;
		FileOutputStream out = null;
		File contentFile = new File(path);
		try {
			out = new FileOutputStream(contentFile);
			out.write(content);
		} catch (FileNotFoundException e) {
			throw new SharkKBException(e.getMessage());
		} catch (IOException e) {
			throw new SharkKBException(e.getMessage());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				throw new SharkKBException(e.getMessage());
			}
		}
	}

	public RDFInformation(AnonId contextPointID) throws SharkKBException {
		path = RDFConstants.INFORMATION_PATH + contextPointID.toString().replaceAll(":", "");
		File contentFile = new File(path);
		FileInputStream fis = null;
		List<Byte> list = new ArrayList<Byte>();
		byte i = 0;
		try {
			fis = new FileInputStream(contentFile);
			while ((i = (byte) fis.read()) != -1) {
				list.add(i);
			}
		} catch (FileNotFoundException e) {
			throw new SharkKBException(e.getMessage());
		} catch (IOException e) {
			throw new SharkKBException(e.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new SharkKBException(e.getMessage());
				}
			}
		}
		content = new byte[list.size()];
		for (int j = 0; j < content.length; j++) {
			content[j] = list.get(j);
		}
	}

	@Override
	public long creationTime() {
		return creationTime;
	}

	@Override
	public byte[] getContentAsByte() {
		return content;
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
	public ASIPSpace getASIPSpace() throws SharkKBException {
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

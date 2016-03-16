package net.sharkfw.knowledgeBase.rdf;

import java.io.File;
import java.io.IOException;

/**
 * This class provides some Utilities for the testing of the RDFSharkKB
 * 
 * @author Barret dfe
 *
 */
public class TestUtils {

	/**
	 * Deletes the given file or content of directory.
	 * 
	 * @param file The file or the content of the directory which shall be deleted
	 * @throws IOException
	 */
	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				System.out.println("Directory is deleted : " + file.getAbsolutePath());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory is deleted : " + file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			//System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}

}

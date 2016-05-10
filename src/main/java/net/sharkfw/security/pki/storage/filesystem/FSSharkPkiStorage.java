package net.sharkfw.security.pki.storage.filesystem;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

import java.io.*;

/**
 * Handling the serialization and deserialization of the SharkPkiStorage. This Methods should be only used if
 * there is no need to use the default behaviour of storing data.
 * @author ac
 */
public class FSSharkPkiStorage {

    private final String filePath;

    /**
     * Constructor to get access to the provided methods.
     * @param filePath Filepath to store or load the {@link FSSharkPkiStorage}
     */
    public FSSharkPkiStorage(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * Serialize the given object and saved it to the path from the initialization.
     * @param sharkPkiStorage {@link SharkPkiStorage}
     * @return true or false as indicator for success or failure in case of saving the object.
     */
    public boolean save(SharkPkiStorage sharkPkiStorage) {
        try {
            FileOutputStream fileOutputStream =  new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(sharkPkiStorage);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Deserialize the object from the given filepath.
     * @return SharkPkiStorage
     */
    public SharkPkiStorage load() throws SharkKBException {

        SharkPkiStorage sharkPkiStorage;

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            sharkPkiStorage = (SharkPkiStorage) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return sharkPkiStorage;
        } catch (IOException | ClassNotFoundException e) {
            throw new SharkKBException(e.getMessage());
        }
    }
}

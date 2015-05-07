package net.sharkfw.security.pki.storage.filesystem;

import net.sharkfw.security.pki.storage.SharkPkiStorage;

import java.io.*;

/**
 * @author ac
 */
public class FSSharkPkiStorage {

    private String filePath;

    /**
     * Constructor
     * @param filePath
     */
    public FSSharkPkiStorage(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * Serialize the given object and saved it to the given path.
     * @param sharkPkiStorage
     * @return
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
     * @return SharkKeyStorage
     */
    public SharkPkiStorage load() {

        SharkPkiStorage sharkPkiStorage = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            sharkPkiStorage = (SharkPkiStorage) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return sharkPkiStorage;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

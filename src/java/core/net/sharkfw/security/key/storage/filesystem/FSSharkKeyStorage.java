package net.sharkfw.security.key.storage.filesystem;

import net.sharkfw.security.key.storage.SharkKeyStorage;

import java.io.*;

/**
 * @author ac
 */
public class FSSharkKeyStorage {

    private String filePath;

    /**
     * Constructor.
     * @param filePath
     */
    public FSSharkKeyStorage(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * Serialize the given object and saved it to the given path.
     * @param sharkKeyStorage
     * @return
     */
    public boolean save(SharkKeyStorage sharkKeyStorage) {
        try {
            FileOutputStream fileOutputStream =  new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(sharkKeyStorage);
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
    public SharkKeyStorage load() {

        SharkKeyStorage sharkKeyStorage = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            sharkKeyStorage = (SharkKeyStorage) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return sharkKeyStorage;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

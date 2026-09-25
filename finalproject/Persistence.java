package ap.exercises.finalproject;

import java.io.*;

public class Persistence implements Serializable {
    private final String savePath = "datafile.bin";

    public void save(LibrarySystem librarySystem) {

        try (FileOutputStream fileOut = new FileOutputStream(savePath);
             ObjectOutputStream writeObj = new ObjectOutputStream(fileOut)) {
            writeObj.writeObject(librarySystem);
            System.out.println("Saving completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LibrarySystem load() {
        LibrarySystem result = null;

        File file = new File(savePath);
        if (!file.exists()) {
            result = new LibrarySystem();
        } else {

            try (FileInputStream fileIn = new FileInputStream(savePath);
                 ObjectInputStream readObj = new ObjectInputStream(fileIn)) {
                result = (LibrarySystem) readObj.readObject();
                System.out.println("Loading completed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

package org.suyash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PersistState {

    public static void save(CrawlState state) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Constants.STATE_FILE))) {
            out.writeObject(state);
        }
    }

    public static CrawlState load() throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(Constants.STATE_FILE))) {
            return (CrawlState) in.readObject();
        }
    }

    public static boolean stateExists() {
        return new File(Constants.STATE_FILE).exists();
    }

}

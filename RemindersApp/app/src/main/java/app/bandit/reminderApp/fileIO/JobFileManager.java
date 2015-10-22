package app.bandit.reminderApp.fileIO;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Gets and Sets the next Job ID from a saved file.
 *
 * Created by Carlton Semple on 10/17/2015.
 */
public class JobFileManager {
    public static final String filename = "jobcount";
    private File file;

    /*
     * Create the file if it doesn't exist
     */
    public JobFileManager(File dir) {
        // Check for the file
        file = new File(dir, filename);
        if(!file.exists()) {
            writeJobCountFile(file, 0);
        }
    }

    /*
     * Increment the job count file and return the latest value
     */
    public int incrementCount() {
        int count = getJobCount();
        if(count + 1 < Integer.MAX_VALUE) {
            count++;
        } else {
            count = 0;
        }
        setJobCount(count);
        return getJobCount();
    }

    public int getJobCount() {
        return getJobCountFromFile(file);
    }

    public void setJobCount(int count) {
        writeJobCountFile(file, count);
    }

    /*
     * Write to the job count file with the given count
     */
    private void writeJobCountFile(File f, int count) {
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(count);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private int getJobCountFromFile(File f) {
        FileInputStream inputStream = null;
        int x = -1;
        if(!f.exists())
            return x;
        try {
            inputStream = new FileInputStream(f);
            x = inputStream.read();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;
    }
}

package com.agifans.picedit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Main class to launch the PicEdit application. This is required so that the
 * executable JAR can set the max heap (since this isn't possible via the 
 * MANIFEST file). All it does is execute the PicEdit class with a max heap
 * setting specified.
 * 
 * @author Lance Ewing
 */
public class PicEditLauncher {

    /**
     * Launches the PicEdit application.
     */
    public static void main(String[] args) throws Exception {
        String pathToJar = PicEditLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        ProcessBuilder picEditProcessBuilder = new ProcessBuilder("java", "-Xmx512m", "-classpath", pathToJar, "com.agifans.picedit.PicEdit");
        Process picEditProcess = picEditProcessBuilder.start();

        BackgroundPrinter stdout = new BackgroundPrinter(picEditProcess.getInputStream());
        stdout.start();

        // kill process and wait max 10 seconds for output to complete
        picEditProcess.waitFor();
        stdout.join(10000);
    }

    /**
     * Catches output from a "java.lang.Process" and writes to standard out. For debugging
     * use only. Don't need this once the app is ready to release.
     */
    private static class BackgroundPrinter extends Thread {
        private InputStream in;

        public BackgroundPrinter(InputStream in) {
            this.in = in;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));

                // read buffer
                char[] buf = new char[1024];

                // write data to target, until no more data is left to read
                int numberOfReadBytes;
                while ((numberOfReadBytes = reader.read(buf)) != -1) {
                    char[] clearedbuf = new char[numberOfReadBytes];
                    System.arraycopy(buf, 0, clearedbuf, 0, numberOfReadBytes);
                    System.out.print(clearedbuf);
                }
            } catch (Exception e) {
            }
        }
    }
}

package fi.helsinki.cs.tmc.intellij.spyware.spywareutils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class RecursiveZipper {
    private File rootDir;
    private ZippingDecider zippingDecider;

    public static interface ZippingDecider {
        /**
         * Tells whether the given file or directory should be zipped.
         * Zip paths are separated by slashes and don't have a starting slash.
         * Directory paths always end in a slash.
         */
        public boolean shouldZip(String zipPath);
    }

    public static final ZippingDecider ZIP_ALL_THE_THINGS = new ZippingDecider() {
        @Override
        public boolean shouldZip(String zipPath) {
            return true;
        }
    };

    public RecursiveZipper(File rootDir, ZippingDecider zippingDecider) {
        this.rootDir = rootDir;
        this.zippingDecider = zippingDecider;
    }

    /**
     * Zip up a project directory, only including stuff decided by the {@link ZippingDecider}.
     */
    public byte[] zipProjectSources() throws IOException {
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new FileNotFoundException("Root directory "
                    + rootDir + " not found for zipping!");
        }

        ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(zipBuffer);

        try {
            zipRecursively(rootDir, zos, "");
        } finally {
            zos.close();
        }

        return zipBuffer.toByteArray();
    }

    private void writeEntry(File file, ZipOutputStream zos, String zipPath) throws IOException {
        zos.putNextEntry(new ZipEntry(zipPath + "/" + file.getName()));

        FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, zos);
        in.close();
        zos.closeEntry();
    }

    /**
     * Zips a directory recursively.
     */
    private void zipRecursively(File dir,
                                ZipOutputStream zos,
                                String parentZipPath) throws IOException {
        String thisDirZipPath;
        if (parentZipPath.isEmpty()) {
            thisDirZipPath = dir.getName();
        } else {
            thisDirZipPath = parentZipPath + "/" + dir.getName();
        }

        // Create an entry for the directory
        zos.putNextEntry(new ZipEntry(thisDirZipPath + "/"));
        zos.closeEntry();

        File[] files = dir.listFiles();
        for (File file : files) {
            boolean isDir = file.isDirectory();
            String zipPath = thisDirZipPath + "/" + file.getName();
            if (isDir) {
                zipPath += "/";
            }
            if (zippingDecider.shouldZip(zipPath)) {
                if (isDir) {
                    zipRecursively(file, zos, thisDirZipPath);
                } else {
                    writeEntry(file, zos, thisDirZipPath);
                }
            }
        }
    }
}
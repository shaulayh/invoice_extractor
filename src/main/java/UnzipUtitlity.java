import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 *
 * @author www.codejava.net
 */
public class UnzipUtitlity {
    public static void main(String[] args) throws IOException {
        String fileZip = "C:\\Users\\DELL\\Desktop\\uber-invoice\\invoices-2020-01-01T10_37_23-2020-01-01T20_43_59.zip";
        File destDir = new File("C:\\Users\\DELL\\Desktop\\fileUber");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();


        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\DELL\\Desktop\\fileUber"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(x -> {
                        try {
                            readPDF(x.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }


    public static void readPDF(String filePath) throws IOException {

        String input = "file:///" + filePath;
        URL url = new URL(input);

        InputStream inputStream = url.openStream();
        BufferedInputStream fileparse = new BufferedInputStream(inputStream);

        PDDocument document = null;

        document = PDDocument.load(fileparse);

        String output = new PDFTextStripper().getText(document);
        System.out.println(output);
        document.close();
    }

}
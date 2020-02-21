import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
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
//        String fileZip = "C:\\Users\\DELL\\Desktop\\uber-invoice\\invoices-2020-01-04T20_31_32-2020-01-09T16_47_06.zip";
//        File destDir = new File("C:\\Users\\DELL\\Desktop\\fileUber");
//        byte[] buffer = new byte[1024];
//        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
//        ZipEntry zipEntry = zis.getNextEntry();
//        while (zipEntry != null) {
//            File newFile = newFile(destDir, zipEntry);
//            FileOutputStream fos = new FileOutputStream(newFile);
//            int len;
//            while ((len = zis.read(buffer)) > 0) {
//                fos.write(buffer, 0, len);
//            }
//            fos.close();
//            zipEntry = zis.getNextEntry();
//        }
//        zis.closeEntry();
//        zis.close();
//


        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\DELL\\Desktop\\fileUber"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(x -> {
                        try {
                            readPDF(x.toString());
                        } catch (IOException | ParseException e) {
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


    public static void readPDF(String filePath) throws IOException, ParseException {
        Entity buyer = new Entity();
        Entity seller = new Entity();
        InvoiceData data = new InvoiceData();
        String input = "file:///" + filePath;
        URL url = new URL(input);

        InputStream inputStream = url.openStream();
        BufferedInputStream fileparse = new BufferedInputStream(inputStream);

        PDDocument document = null;

        document = PDDocument.load(fileparse);

        String output = new PDFTextStripper().getText(document);
//        System.out.println(output);
        String[] lines = output.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            System.out.print(lines[i]);
            System.out.println("  " + i);


            buyer.setName(lines[2]);
            buyer.setAddress(lines[3]);
            buyer.setPostalCode(lines[4]);
            buyer.setCity(lines[5]);
            buyer.setNip(lines[6]);

            seller.setName(lines[9]);
            seller.setAddress(lines[10]);
            seller.setPostalCode(lines[11]);
            seller.setCity(lines[12]);
            seller.setNip(lines[13]);

            String[] numberData = lines[14].split(":");
            data.setNumber(numberData[1]);

            String[] dateData = lines[15].split(":");
            data.setDateInString(dateData[1]);

            String[] mainData = lines[23].split("\\s");

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date date = format.parse(mainData[1]);
            data.setDate(date);

//            data.setQuantity(Integer.parseInt(mainData[4]));
            data.setNetto(Double.parseDouble(mainData[9]));
            System.out.println("---------------------------------------------------");
        }
        System.out.println(buyer.toString());
        System.out.println(seller.toString());
        System.out.println(data.toString());
        System.out.println("********************************************************");
        document.close();
    }

}
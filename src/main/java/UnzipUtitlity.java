import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 *
 * @author Azeez G. Shola
 */
public class UnzipUtitlity {
    public static void main(String[] args) throws IOException {


        ArrayList<String> buyers = new ArrayList<>();


        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\DELL\\Desktop\\uber-invoice"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(x -> {
                        try {
                            unzipToFolder(x.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\DELL\\Desktop\\fileUber"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(x -> {
                        try {
                            buyers.add(readPDF(x.toString()));
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    });
        }
        System.out.println(buyers.size());
        writeToCSV(buyers);
    }


    public static void unzipToFolder(String input) throws IOException {
        File destDir = new File("C:\\Users\\DELL\\Desktop\\fileUber");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(input));
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


    public static String readPDF(String filePath) throws IOException, ParseException {

        int pointer = 0;
        Entity buyer = new Entity();
        Entity seller = new Entity();
        InvoiceData data = new InvoiceData();
        String input = "file:///" + filePath;
        URL url = new URL(input);

        InputStream inputStream = url.openStream();
        BufferedInputStream fileparse = new BufferedInputStream(inputStream);

        PDDocument document;

        document = PDDocument.load(fileparse);

        String output = new PDFTextStripper().getText(document);

        String[] lines = output.split("\\r?\\n");


        for (int i = 0; i < lines.length; i++) {
            System.out.print(lines[i]);
            System.out.println("  " + i);

            Boolean checker = lines[lines.length - (pointer + 2)].contains("brutto");
            System.out.println("is of the vat mode" + checker);
            if (!checker) {
                pointer = -1;
            }
            System.out.println(pointer);
            System.out.println(lines[lines.length - (pointer + 2)]);
            String brutto = lines[lines.length - (pointer + 2)].split("brutto")[1].trim();

            buyer.setName(lines[lines.length - (pointer + 25)]);
            buyer.setAddress(lines[lines.length - (pointer + 24)]);
            buyer.setPostalCode(lines[lines.length - (pointer + 23)]);
            buyer.setCity(lines[lines.length - (pointer + 22)]);
            buyer.setNip(lines[lines.length - (pointer + 21)]);

            seller.setName(lines[lines.length - (pointer + 18)]);
            seller.setAddress(lines[lines.length - (pointer + 17)]);
            seller.setPostalCode(lines[lines.length - (pointer + 16)]);
            seller.setCity(lines[lines.length - (pointer + 15)]);
            seller.setNip(lines[lines.length - (pointer + 14)]);


            String[] numberData = lines[lines.length - (pointer + 13)].split(":");

            System.out.println(lines[lines.length - (pointer + 23)].matches("[0-9]{2}\\-[0-9]{3}"
            ));

            if (!(lines[lines.length - (pointer + 23)].matches("[0-9]{2}\\-[0-9]{3}"
            ))) {
                buyer.setName(lines[lines.length - (pointer + 27)] + lines[lines.length - (pointer + 26)] + " " + lines[lines.length - (pointer + 25)]);
                buyer.setAddress(lines[lines.length - (pointer + 24)]);
                buyer.setPostalCode("40-001");
                buyer.setCity(lines[lines.length - (pointer + 23)]);
                buyer.setNip(lines[lines.length - (pointer + 22)]);
            }

//            System.out.println(Arrays.toString(numberData));
            data.setNumber(numberData[1]);

            String[] dateData = lines[lines.length - (pointer + 12)].split(":");


            boolean check2 = lines[lines.length - (pointer + 12)].contains("Data faktury");
            if (!check2) {
                dateData = lines[lines.length - (1 + pointer + 12)].split(":");
            }
            data.setDateInString(dateData[1]);

            String[] mainData = lines[lines.length - (pointer + 4)].split("\\s");

            boolean infoChecker = lines[lines.length - (pointer + 4)].contains("Usługa transportowa");


            if (!infoChecker) {
                mainData = lines[lines.length - (1 + pointer + 4)].split("\\s");
            }

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date date = format.parse(mainData[1]);
            data.setDate(date);


            data.setQuantity(Integer.parseInt(mainData[4].substring(0, 1)));


            data.setNetto(convertToDouble(mainData[9]));
            data.setVatRate(convertToDouble(mainData[7]));
            data.setVatAmount(convertToDouble(mainData[8]));
            data.setService(mainData[3]);


            brutto = brutto.replace(".00", "")
                    .replace("PLN", "").replaceAll("\\s+", "");

            data.setBrutto(convertToDouble(brutto));
            System.out.println("---------------------------------------------------");
        }
        System.out.println(buyer.toString());
        System.out.println(seller.toString());
        System.out.println(data.toString());

        convertDetailToCSVFormat(buyer, seller, data);
        System.out.println("********************************************************");
        document.close();
        return convertDetailToCSVFormat(buyer, seller, data);
    }

    private static double convertToDouble(String in) {

        NumberFormat form = NumberFormat.getInstance(Locale.FRANCE);
        try {
            Number number = form.parse(in);
            return number.doubleValue();
        } catch (ParseException e) {
            return 0.0;
        }

    }


    private static void writeToCSV(ArrayList<String> input) {

        CSVWriter csvWriter = null;
        try {
            String csv = "data.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(csv));

            //Create record
            String[] record = "Buyer Name,Address,Postal code,city,nip,invoice number, date , quantity ,netto, brutto, VAT,services".split(",");
            //Write the record to file
            writer.writeNext(record);


            for (String rec : input) {
                writer.writeNext(rec.split(","));
            }

            //close the writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String convertDetailToCSVFormat(Entity buyer, Entity seller, InvoiceData data) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(buyer.getName().replaceAll("\\s", "")).append(",");
        stringBuilder.append(buyer.getAddress().replaceAll("\\s", "")).append(",");
        stringBuilder.append(buyer.getPostalCode().replaceAll("\\s", "")).append(",");
        stringBuilder.append(buyer.getCity().replaceAll("\\s", "")).append(",");
        stringBuilder.append(buyer.getNip().replaceAll("\\s", "")).append(",");
        stringBuilder.append(data.getNumber().replaceAll("\\s{2}", "0")).append(",");
        stringBuilder.append(data.getDate()).append(",");
        stringBuilder.append(data.getQuantity()).append(",");
        stringBuilder.append(data.getNetto()).append(",");
        stringBuilder.append(data.getBrutto()).append(",");
        stringBuilder.append(data.getVatAmount()).append(",");
        stringBuilder.append(data.getService().replaceAll("\\s", "")).append("\n");
        return stringBuilder.toString();
    }
}
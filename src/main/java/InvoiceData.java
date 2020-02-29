import java.util.Date;

public class InvoiceData {

    private String number;
    private Date date = new Date();
    private String dateInString;
    private int quantity;
    private double netto;
    private double brutto;
    private double vatRate;
    private double vatAmount;
    private String service;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getNetto() {
        return netto;
    }

    public void setNetto(double netto) {
        this.netto = netto;
    }

    public double getBrutto() {
        return brutto;
    }

    public void setBrutto(double brutto) {
        this.brutto = brutto;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateInString() {
        return dateInString;
    }

    public void setDateInString(String dateInString) {
        this.dateInString = dateInString;
    }

    @Override
    public String toString() {
        return "InvoiceData{" +
                "number='" + number + '\'' +
                ", date=" + date +
                ", dateInString='" + dateInString + '\'' +
                ", quantity=" + quantity +
                ", netto=" + netto +
                ", brutto=" + brutto +
                ", vatRate=" + vatRate +
                ", vatAmount=" + vatAmount +
                ", service='" + service + '\'' +
                '}';
    }
}

import java.util.Date;

public class InvoiceData {

    private String number;
    private Date date = new Date();
    private String dateInString;

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
                '}';
    }
}

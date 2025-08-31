package nfl.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class Spread {

    @CsvDate(value = "yyyy-MM-dd HH:mm z")
    @CsvBindByName
    private Date dateTime;

    @CsvBindByName
    private String favorite;

    @CsvBindByName
    private String underdog;

    @CsvBindByName
    private Double spread;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getUnderdog() {
        return underdog;
    }

    public void setUnderdog(String underdog) {
        this.underdog = underdog;
    }

    public Double getSpread() {
        return spread;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }
}

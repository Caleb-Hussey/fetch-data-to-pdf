package nfl.model;

import java.util.List;

public class WeeklyData {

    // private static final String headerPrefix = "NFL Point Spreads for Week ";
    private String title = "NFL Super Bowl Spread";
    private List<Spread> spreads;

    public String getTitle() {
        return title;
        // return headerPrefix + weekNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Spread> getSpreads() {
        return spreads;
    }

    public void setSpreads(List<Spread> spreads) {
        this.spreads = spreads;
    }
}

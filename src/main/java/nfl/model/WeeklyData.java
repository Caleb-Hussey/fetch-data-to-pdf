package nfl.model;

import java.util.List;

public class WeeklyData {

    // private static final String headerPrefix = "NFL Point Spreads for Week ";
    private static final String header = "NFL Super Bowl Spread";
    private int weekNumber;
    private List<Spread> spreads;

    public String getHeader() {
        return header;
        // return headerPrefix + weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public List<Spread> getSpreads() {
        return spreads;
    }

    public void setSpreads(List<Spread> spreads) {
        this.spreads = spreads;
    }
}

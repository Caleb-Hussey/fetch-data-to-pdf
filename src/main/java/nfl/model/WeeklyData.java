package nfl.model;

import java.util.List;

public class WeeklyData {

    // private static final String headerPrefix = "NFL Point Spreads for Week ";
    private String title = null;
    private List<Spread> spreads;
    private List<String> byeWeekTeams;

    public String getTitle() {
        if (title == null) {
            throw new RuntimeException("Title shouldn't be null");
        }
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

    public List<String> getByeWeekTeams() {
        return byeWeekTeams;
    }

    public void setByeWeekTeams(List<String> byeWeekTeams) {
        this.byeWeekTeams = byeWeekTeams;
    }
}

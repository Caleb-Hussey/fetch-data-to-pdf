package nfl;

import nfl.model.Spread;
import nfl.model.WeeklyData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

class SpreadFetcher {

    private final DateTimeFormatter testDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy h:mm a", Locale.ENGLISH);
    private final ZoneId newYorkZone = ZoneId.of("America/New_York");
    private final DateTimeFormatter dateTimeFormatterAlternative = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);
    private final ZoneId zuluZone = ZoneId.of("UTC");
    private final String[] allowedTeams = new String[]{
            "Buffalo",
            "Miami",
            "New England",
            "New York Jets",
            "Baltimore",
            "Cincinnati",
            "Cleveland",
            "Pittsburgh",
            "Houston",
            "Indianapolis",
            "Jacksonville",
            "Tennessee",
            "Denver",
            "Kansas City",
            "Las Vegas",
            "Los Angeles Chargers",
            "Dallas",
            "New York Giants",
            "Philadelphia",
            "Washington",
            "Chicago",
            "Detroit",
            "Green Bay",
            "Minnesota",
            "Atlanta",
            "Carolina",
            "New Orleans",
            "Tampa Bay",
            "Arizona",
            "Los Angeles Rams",
            "San Francisco",
            "Seattle"};


    WeeklyData fetch(String header) throws IOException, ParseException {
        return fetchSpreadsFromConfiguredUrl(header);
    }

    private WeeklyData fetchSpreadsFromConfiguredUrl(String header) throws IOException {
        Properties appProps = new Properties();
        appProps.load(Files.newInputStream(Paths.get("app.properties")));

        final String url = appProps.getProperty("spreadsSource");
        // String gameDate = "";
        // SimpleDateFormat formatter = new SimpleDateFormat("y MMMM d h:mmaa z", Locale.ENGLISH);
        // formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        Document doc = Jsoup.connect(url).get();

        Elements spreadsByWeek = doc.select("[data-testid=\"prism-LayoutCard\"]");
        // System.out.println("Found " + spreadsByWeek.size() + " week containers");

        Element relevantWeek = null;
        for (Element weekElement : spreadsByWeek) {
            if (isRelevantWeek(weekElement, header)) {
                relevantWeek = weekElement;
                break;
            }
        }
        if (relevantWeek == null) {
            System.out.println("Could not find " + header);
            throw new RuntimeException("Could not find header");
        }

        Elements detailSections = relevantWeek.children().get(1).children();
        if (detailSections.size() % 2 == 1) {
            throw new RuntimeException("Found unexpected odd number of detail sections");
        }

        return extractSpreadsFromDetailSections(detailSections);
    }

    private boolean isRelevantWeek(Element element, String header) {
        Elements children = element.children();
        for (Element child : children) {
            if (child.text().contains(header)) {
                return true;
            }
        }
        return false;
    }

    private WeeklyData extractSpreadsFromDetailSections(Elements detailSections) {
        WeeklyData data = new WeeklyData();
        List<Spread> rawSpreads = new ArrayList<>();

        int numberOfGameDays = detailSections.size() / 2;

        System.out.println("Number of game days " + numberOfGameDays);

        for (int day = 0; day < numberOfGameDays; day++) {
            rawSpreads.addAll(getSpreadsByDay(detailSections.get(2 * day),  detailSections.get(2 * day + 1)));
        }

        data.setSpreads(rawSpreads);
        return data;
    }

    private List<Spread> getSpreadsByDay(Element date, Element games) {
        try {
            testDateFormatter.parse(date.text());
        } catch (Exception ex) {
            System.out.println("Failed to parse date " + date.text());
            throw new RuntimeException(ex);
        }

        List<Spread> spreads = new ArrayList<>();
        for (Element game : games.children()) {
            Elements children = game.children().get(0).children().get(0).children().get(0).children();
            String dateText = date.text() + " 2025 " + children.get(0).text();
            Date dateTime;
            try {
                LocalDateTime ldt = LocalDateTime.parse(dateText, dateTimeFormatter);
                dateTime = Date.from(ldt.atZone(newYorkZone).toInstant());
            } catch (Exception ex) {
                System.out.println("Using alternative");
                LocalDateTime ldt = LocalDateTime.parse(children.get(0).text(), dateTimeFormatterAlternative);
                dateTime = Date.from(ldt.atZone(zuluZone).toInstant());
            }

            Spread spread = new Spread();
            spread.setDateTime(dateTime);

            setTeamsAndSpreadValue(spread, children);
            spreads.add(spread);
        }
        return spreads;
    }

    private void setTeamsAndSpreadValue(Spread spread, Elements elements) {
        String firstTeam = elements.get(5).text();
        // System.out.println("First team full string: " + firstTeam);
        String secondTeam = elements.get(10).text();
        // System.out.println("Second team full string: " + secondTeam);

        String spreadString = elements.get(7).text().split(" ")[0];
        // System.out.println("Value text: " + spreadString);

        List<String> evenValues = List.of("Ev", "-", "OFF", "0.0");
        double spreadValue = evenValues.contains(spreadString) ? 0.5 : Double.parseDouble(spreadString);
        if ((spreadValue % 1 == 0) && (spreadValue != 0)) {
            spreadValue -= 0.5;
        }

        for (String team : allowedTeams) {
            if (firstTeam.contains(team)) {
                firstTeam = (firstTeam.contains("Home") ? "At " : "")
                        + team.replace("Los Angeles", "LA").replace("New York", "NY");
            } else if (secondTeam.contains(team)) {
                secondTeam = (secondTeam.contains("Home") ? "At " : "")
                        + team.replace("Los Angeles", "LA").replace("New York", "NY");
            }
        }

        if (spreadValue > 0) {
            spread.setFavorite(secondTeam);
            spread.setUnderdog(firstTeam);
            spread.setSpread(spreadValue);
        } else {
            spread.setFavorite(firstTeam);
            spread.setUnderdog(secondTeam);
            spread.setSpread(-1 * spreadValue);
        }
    }
}

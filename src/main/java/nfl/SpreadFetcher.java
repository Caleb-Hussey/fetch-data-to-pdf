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
import java.util.*;

class SpreadFetcher {

    private final DateTimeFormatter testDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d h:mm a", Locale.ENGLISH);
    private final ZoneId newYorkZone = ZoneId.of("America/New_York");


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
        System.out.println("Found " + spreadsByWeek.size() + " week containers");

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

        System.out.println("Number of days " + numberOfGameDays);

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
            String dateText = date.text() + " " + children.get(0).text();
            System.out.println("Datetime is " + dateText);
            LocalDateTime ldt = LocalDateTime.parse(dateText, dateTimeFormatter);
            Date dateTime = Date.from(ldt.atZone(newYorkZone).toInstant());
            Spread spread = new Spread();
            spread.setDateTime(dateTime);

            for (Element child : children.get(0).children().get(0).children().get(0).children()) {
                System.out.println("Text is " + child.text());
            }
            
            spreads.add(spread);
        }
        return spreads;
    }


//            if (weekElement.hasClass("odds--group__details-date-container")) {
//        System.out.println("Date container");
//        dateCorrect = false;
//        String elementText = groupElement.text();
//        System.out.println("elementText " + elementText);
//        for (String date : dates) {
//            if (elementText.contains(date)) {
//                dateCorrect = true;
//                gameDate = date;
//                System.out.println("dateCorrect true ");
//                break;
//            } else {
//                System.out.println("dateCorrect false ");
//                dateCorrect = false;
//            }
//        }
//    } else if (groupElement.hasClass("odds--group__events-container football")) {
//        System.out.println("football container");
//        if (!dateCorrect) {
//            continue;
//        }
//        Elements children = groupElement.children();
//        System.out.println("Number of games = " + children.size());
//        for (Element child : children) {
//            Elements timeElements = child.getElementsByClass("odds--group__event-time");
//            Elements participantsElements = child.getElementsByClass("odds--group__event-participants");
//            Elements openingSpreadsElements = child.getElementsByClass("odds-spread opening");
//
//            if (timeElements.size() > 1) {
//                throw new RuntimeException("Too many time elements " + timeElements.size());
//            }
//            if (timeElements.size() == 0) {
//                continue;
//            }
//            String matchTime = timeElements.get(0).text();
//
//            if (participantsElements.size() != 1) {
//                throw new RuntimeException("Unexpected number of participants elements " + participantsElements.size());
//            }
//            if (openingSpreadsElements.size() != 2) {
//                throw new RuntimeException("Unexpected number of participants elements " + openingSpreadsElements.size());
//            }
//
//            Element participantsElement = participantsElements.get(0);
//            String firstTeam = participantsElement.children().get(0).children().get(0).children().get(0).text();
//            String secondTeam = participantsElement.children().get(2).children().get(0).children().get(0).text();
//
//            Spread spread = new Spread();
//
//            String dateString = "2025 " + gameDate + " " + matchTime.trim();
//            System.out.println(dateString);
//            Date gameTime = formatter.parse(dateString);
//            spread.setDateTime(gameTime);
//
//
//            String spreadString = openingSpreadsElements.get(0).children().get(0).text();
//
//            double spreadValue = ("Ev".equals(spreadString) || "-".equals(spreadString)) ? 0.5 : Double.parseDouble(spreadString);
//
//            if (spreadValue > 0) {
//                spread.setFavorite(secondTeam);
//                spread.setUnderdog(firstTeam);
//                spread.setSpread(spreadValue);
//            } else {
//                spread.setFavorite(firstTeam);
//                spread.setUnderdog(secondTeam);
//                spread.setSpread(-1 * spreadValue);
//            }
//
//            if ((spreadValue % 1 == 0) && (spreadValue != 0)) {
//                spread.setSpread(spread.getSpread() - 0.5);
//            }
//
//            rawSpreads.add(spread);
//
//        }
//    }

}

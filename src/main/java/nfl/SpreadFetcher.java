package nfl;

import nfl.model.GamePredictionDetails;
import nfl.model.RawSpreadData;
import nfl.model.Spread;
import nfl.model.WeeklyData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class SpreadFetcher {

    private final static int numberOfSources = 5;


    WeeklyData fetch(String header) throws IOException, ParseException {
        return fetchSpreadsFromEspn(header);
    }

    private WeeklyData fetchSpreadsFromEspn(String header) throws IOException, ParseException {
        WeeklyData data = new WeeklyData();
        List<Spread> rawSpreads = new ArrayList<>();

        Properties appProps = new Properties();
        appProps.load(Files.newInputStream(Paths.get("app.properties")));

        final String url = appProps.getProperty("spreadsSource");
        System.out.println("Url is " + url);
        // String gameDate = "";
        // SimpleDateFormat formatter = new SimpleDateFormat("y MMMM d h:mmaa z", Locale.ENGLISH);
        // formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));

//        Document doc = Jsoup.connect(url).get();
//
//        Elements groupContainer = doc.select("div.odds--group__container").get(0).children();
//        System.out.println(" Length " + groupContainer.size());
//
//        //int counter = 0;
//        for (Element groupElement : groupContainer) {
//            System.out.println("Starting group container");
//            //counter++;
//
//            if (groupElement.hasClass("odds--group__details-date-container")) {
//                System.out.println("Date container");
//                dateCorrect = false;
//                String elementText = groupElement.text();
//                System.out.println("elementText " + elementText);
//                for (String date : dates) {
//                    if (elementText.contains(date)) {
//                        dateCorrect = true;
//                        gameDate = date;
//                        System.out.println("dateCorrect true ");
//                        break;
//                    } else {
//                        System.out.println("dateCorrect false ");
//                        dateCorrect = false;
//                    }
//                }
//            } else if (groupElement.hasClass("odds--group__events-container football")) {
//                System.out.println("football container");
//                if (!dateCorrect) {
//                    continue;
//                }
//                Elements children = groupElement.children();
//                System.out.println("Number of games = " + children.size());
//                for (Element child : children) {
//                    Elements timeElements = child.getElementsByClass("odds--group__event-time");
//                    Elements participantsElements = child.getElementsByClass("odds--group__event-participants");
//                    Elements openingSpreadsElements = child.getElementsByClass("odds-spread opening");
//
//                    if (timeElements.size() > 1) {
//                        throw new RuntimeException("Too many time elements " + timeElements.size());
//                    }
//                    if (timeElements.size() == 0) {
//                        continue;
//                    }
//                    String matchTime = timeElements.get(0).text();
//
//                    if (participantsElements.size() != 1) {
//                        throw new RuntimeException("Unexpected number of participants elements " + participantsElements.size());
//                    }
//                    if (openingSpreadsElements.size() != 2) {
//                        throw new RuntimeException("Unexpected number of participants elements " + openingSpreadsElements.size());
//                    }
//
//                    Element participantsElement = participantsElements.get(0);
//                    String firstTeam = participantsElement.children().get(0).children().get(0).children().get(0).text();
//                    String secondTeam = participantsElement.children().get(2).children().get(0).children().get(0).text();
//
//                    Spread spread = new Spread();
//
//                    String dateString = "2025 " + gameDate + " " + matchTime.trim();
//                    System.out.println(dateString);
//                    Date gameTime = formatter.parse(dateString);
//                    spread.setDateTime(gameTime);
//
//
//                    String spreadString = openingSpreadsElements.get(0).children().get(0).text();
//
//                    double spreadValue = ("Ev".equals(spreadString) || "-".equals(spreadString)) ? 0.5 : Double.parseDouble(spreadString);
//
//                    if (spreadValue > 0) {
//                        spread.setFavorite(secondTeam);
//                        spread.setUnderdog(firstTeam);
//                        spread.setSpread(spreadValue);
//                    } else {
//                        spread.setFavorite(firstTeam);
//                        spread.setUnderdog(secondTeam);
//                        spread.setSpread(-1 * spreadValue);
//                    }
//
//                    if ((spreadValue % 1 == 0) && (spreadValue != 0)) {
//                        spread.setSpread(spread.getSpread() - 0.5);
//                    }
//
//                    rawSpreads.add(spread);
//
//                }
//            }
//        }
        data.setSpreads(rawSpreads);
        return data;
    }

}

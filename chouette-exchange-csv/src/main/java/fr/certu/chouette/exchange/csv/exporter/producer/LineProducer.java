package fr.certu.chouette.exchange.csv.exporter.producer;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.plugin.report.Report;

public class LineProducer extends AbstractCSVNeptuneProducer<Line>
{

   public static final String LINE_NAME_TITLE = "Nom de la ligne";

   private static final SimpleDateFormat dfHM = new SimpleDateFormat("HH:mm");
   private static final SimpleDateFormat dfHMS = new SimpleDateFormat(
         "HH:mm:ss");

   private static final String PUBLISHED_LINE_NAME_TITLE = "Nom public";
   private static final String NUMBER_TITLE = "Numero de la ligne";
   private static final String CODE_TITLE = "Code de la ligne";
   private static final String COMMENT_TITLE = "Commentaire de la ligne";
   private static final String TRANSPORT_MODE_NAME_TITLE = "Mode de Transport (BUS,METRO,RER,TRAIN ou TRAMWAY)";

   private static final String DIRECTION_TITLE = "Direction (ALLER/RETOUR)";
   private static final String TIMETABLE_TITLE = "Calendriers d'application";
   private static final String SPECIFIC_TITLE = "Particularités";

   private static final String[] BOARDING_POSITION_LINE_TITLES = { "X", "Y",
         "Latitude", "Longitude", "Adresse", "Code Postal", "Zone",
         "Liste des arrêts" };

   private static final int X_COLUMN = 0;
   private static final int Y_COLUMN = 1;
   private static final int LATITUDE_COLUMN = 2;
   private static final int LONGITUDE_COLUMN = 3;
   private static final int ADDRESS_COLUMN = 4;
   private static final int ZIPCODE_COLUMN = 5;
   private static final int AREAZONE_COLUMN = 6;
   private static final int STOPNAME_COLUMN = 7;

   @Override
   public List<String[]> produce(Line line, Report report)
   {
      List<String[]> csvLinesList = new ArrayList<String[]>();
      csvLinesList.add(createCSVLine(LINE_NAME_TITLE, line.getName()));
      csvLinesList.add(createCSVLine(PUBLISHED_LINE_NAME_TITLE,
            line.getPublishedName()));
      csvLinesList.add(createCSVLine(NUMBER_TITLE, line.getNumber()));
      // ajout code ligne si différent de numero ligne
      if (line.getRegistrationNumber() != null
            && !line.getRegistrationNumber().isEmpty())
      {
         if (!line.getRegistrationNumber().equals(line.getNumber()))
         {
            csvLinesList.add(createCSVLine(CODE_TITLE,
                  line.getRegistrationNumber()));
         }
      }
      csvLinesList.add(createCSVLine(COMMENT_TITLE, line.getComment()));
      csvLinesList.add(createCSVLine(TRANSPORT_MODE_NAME_TITLE, line
            .getTransportModeName().toString().toUpperCase()));

      // make a copy of line routes for sorting
      List<Route> routes = new ArrayList<Route>(line.getRoutes());
      if (routes.size() > 2)
      {
         // normally reported before this call
         throw new IllegalArgumentException(
               "cannot export lines with more than 2 routes");

      }
      // sort routes (A before R)
      Collections.sort(routes, new WaybackRouteComparator());

      int vehicleJourneysCount = routes.size();// add one dummy vehicle
      // journey for each route
      for (Route route : routes)
      {
         if (route.getJourneyPatterns() != null)
         {
            List<JourneyPattern> journeyPatterns = route.getJourneyPatterns();
            for (JourneyPattern journeyPattern : journeyPatterns)
            {
               if (journeyPattern.getVehicleJourneys() != null)
                  vehicleJourneysCount += journeyPattern.getVehicleJourneys()
                        .size();
            }
         }
      }

      String[] vehicleJourneyDirectionCSVLine = new String[TITLE_COLUMN + 1
            + vehicleJourneysCount];
      vehicleJourneyDirectionCSVLine[TITLE_COLUMN] = DIRECTION_TITLE;
      csvLinesList.add(vehicleJourneyDirectionCSVLine);

      String[] vehicleJourneyTimetableCSVLine = new String[TITLE_COLUMN + 1
            + vehicleJourneysCount];
      vehicleJourneyTimetableCSVLine[TITLE_COLUMN] = TIMETABLE_TITLE;
      csvLinesList.add(vehicleJourneyTimetableCSVLine);

      String[] vehicleJourneySpecificCSVLine = new String[TITLE_COLUMN + 1
            + vehicleJourneysCount];
      vehicleJourneySpecificCSVLine[TITLE_COLUMN] = SPECIFIC_TITLE;
      csvLinesList.add(vehicleJourneySpecificCSVLine);

      csvLinesList.add(BOARDING_POSITION_LINE_TITLES);

      HashMap<StopPoint, String[]> csvLinesByStopPoint = new HashMap<StopPoint, String[]>();
      int vehicleJourneyColumn = TITLE_COLUMN + 1; // must not be reseted for
      // second route !
      for (Route route : routes)
      {
         if (route.getStopPoints() == null)
            break;
         for (StopPoint stopPointOnRoute : route.getStopPoints())
         {
            StopArea boardingPosition = stopPointOnRoute
                  .getContainedInStopArea();
            String[] csvLine = createBoardingPositionCsvLine(boardingPosition,
                  vehicleJourneysCount);
            csvLine[vehicleJourneyColumn] = "00:00";
            csvLinesByStopPoint.put(stopPointOnRoute, csvLine);
            csvLinesList.add(csvLine);
         }
         List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
         for (JourneyPattern journeyPattern : route.getJourneyPatterns())
         {
            List<VehicleJourney> vjByJourney = new ArrayList<VehicleJourney>();
            vjByJourney.addAll(journeyPattern.getVehicleJourneys());
            Collections.sort(vjByJourney, new VjComparator());
            vehicleJourneys.addAll(vjByJourney);
         }

         vehicleJourneyDirectionCSVLine[vehicleJourneyColumn] = ("R"
               .equals(route.getWayBack()) ? "RETOUR" : "ALLER");

         vehicleJourneyColumn++;

         for (VehicleJourney vehicleJourney : vehicleJourneys)
         {
            vehicleJourneyDirectionCSVLine[vehicleJourneyColumn] = ("R"
                  .equals(vehicleJourney.getRoute().getWayBack()) ? "RETOUR"
                  : "ALLER");
            List<Timetable> timetables = vehicleJourney.getTimetables();
            if (timetables != null && timetables.size() > 0)
            {
               {
                  String tmCode = timetables.get(0).getVersion();
                  if (tmCode == null || tmCode.isEmpty())
                     tmCode = timetables.get(0).getComment();
                  vehicleJourneyTimetableCSVLine[vehicleJourneyColumn] = tmCode;
               }
               for (int i = 1; i < timetables.size(); i++)
               {
                  String tmCode = timetables.get(i).getVersion();
                  if (tmCode == null || tmCode.isEmpty())
                     tmCode = timetables.get(i).getComment();
                  vehicleJourneyTimetableCSVLine[vehicleJourneyColumn] += ","
                        + tmCode;
               }
            } else
            {
               // TODO add report item
            }
            vehicleJourneySpecificCSVLine[vehicleJourneyColumn] = vehicleJourney
                  .getVehicleTypeIdentifier();

            for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney
                  .getVehicleJourneyAtStops())
            {
               csvLinesByStopPoint.get(vehicleJourneyAtStop.getStopPoint())[vehicleJourneyColumn] = convertTimeToString(vehicleJourneyAtStop
                     .getDepartureTime());
            }
            vehicleJourneyColumn++;
         }
      }

      return csvLinesList;
   }

   private String[] createBoardingPositionCsvLine(StopArea boardingPosition,
         int vehicleJourneysCount)
   {
      String[] csvLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
      if (boardingPosition.hasProjection())
      {
         csvLine[X_COLUMN] = asString(boardingPosition.getX());
         csvLine[Y_COLUMN] = asString(boardingPosition.getY());
      }
      if (boardingPosition.hasCoordinates())
      {
         csvLine[LATITUDE_COLUMN] = asString(boardingPosition.getLatitude());
         csvLine[LONGITUDE_COLUMN] = asString(boardingPosition.getLongitude());
         if (boardingPosition.getStreetName() != null)
         {
            csvLine[ADDRESS_COLUMN] = boardingPosition.getStreetName();
         }
         if (boardingPosition.getCountryCode() != null)
         {
            csvLine[ZIPCODE_COLUMN] = boardingPosition.getCountryCode();
         }
      }
      StopArea parent = boardingPosition.getParent();
      if (parent != null)
      {
         csvLine[AREAZONE_COLUMN] = parent.getName();
      }
      csvLine[STOPNAME_COLUMN] = boardingPosition.getName();

      return csvLine;
   }

   public String convertTimeToString(Time time)
   {
      long h = time.getTime() / 1000;
      long s = h % 60;
      if (s > 0)
      {
         return dfHMS.format(time);
      } else
      {
         return dfHM.format(time);
      }
   }

   private class WaybackRouteComparator implements Comparator<Route>
   {

      @Override
      public int compare(Route o1, Route o2)
      {
         return o1.getWayBack().compareTo(o2.getWayBack());
      }

   }

   private class VjComparator implements Comparator<VehicleJourney>
   {

      @Override
      public int compare(VehicleJourney o1, VehicleJourney o2)
      {
         return o1
               .getVehicleJourneyAtStops()
               .get(0)
               .getDepartureTime()
               .compareTo(
                     o2.getVehicleJourneyAtStops().get(0).getDepartureTime());
      }

   }
}

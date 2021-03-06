/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.validation.checkpoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import lombok.extern.log4j.Log4j;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

/**
 * @author michel
 * 
 */
@Log4j
public abstract class AbstractValidation
{
   // test keys
   protected static final String STOP_AREA_1 = "3-StopArea-1";
   protected static final String STOP_AREA_2 = "3-StopArea-2";
   protected static final String STOP_AREA_3 = "3-StopArea-3";
   protected static final String STOP_AREA_4 = "3-StopArea-4";
   protected static final String STOP_AREA_5 = "3-StopArea-5";
   protected static final String STOP_AREA_6 = "3-StopArea-6";
   protected static final String ACCESS_POINT_1 = "3-AccessPoint-1";
   protected static final String ACCESS_POINT_2 = "3-AccessPoint-2";
   protected static final String ACCESS_POINT_3 = "3-AccessPoint-3";
   protected static final String CONNECTION_LINK_1 = "3-ConnectionLink-1";
   protected static final String CONNECTION_LINK_2 = "3-ConnectionLink-2";
   protected static final String CONNECTION_LINK_3 = "3-ConnectionLink-3";
   protected static final String ACCESS_LINK_1 = "3-AccessLink-1";
   protected static final String ACCESS_LINK_2 = "3-AccessLink-2";
   protected static final String ACCESS_LINK_3 = "3-AccessLink-3";
   protected static final String LINE_1 = "3-Line-1";
   protected static final String LINE_2 = "3-Line-2";
   protected static final String LINE_3 = "3-Line-3";
   protected static final String ROUTE_1 = "3-Route-1";
   protected static final String ROUTE_2 = "3-Route-2";
   protected static final String ROUTE_3 = "3-Route-3";
   protected static final String ROUTE_4 = "3-Route-4";
   protected static final String ROUTE_5 = "3-Route-5";
   protected static final String ROUTE_6 = "3-Route-6";
   protected static final String ROUTE_7 = "3-Route-7";
   protected static final String ROUTE_8 = "3-Route-8";
   protected static final String ROUTE_9 = "3-Route-9";
   protected static final String JOURNEY_PATTERN_1 = "3-JourneyPattern-1";
   protected static final String VEHICLE_JOURNEY_1 = "3-VehicleJourney-1";
   protected static final String VEHICLE_JOURNEY_2 = "3-VehicleJourney-2";
   protected static final String VEHICLE_JOURNEY_3 = "3-VehicleJourney-3";
   protected static final String VEHICLE_JOURNEY_4 = "3-VehicleJourney-4";
   protected static final String VEHICLE_JOURNEY_5 = "3-VehicleJourney-5";
   protected static final String VEHICLE_JOURNEY_6 = "3-VehicleJourney-6";
   protected static final String FACILITY_1 = "3-Facility-1";
   protected static final String FACILITY_2 = "3-Facility-2";

   // parameter keys
   protected static final String STOP_AREAS_AREA = "stop_areas_area";
   protected static final String INTER_STOP_AREA_DISTANCE_MIN = "inter_stop_area_distance_min";
   protected static final String PARENT_STOP_AREA_DISTANCE_MAX = "parent_stop_area_distance_max";
   protected static final String INTER_ACCESS_POINT_DISTANCE_MIN = "inter_access_point_distance_min";
   protected static final String INTER_CONNECTION_LINK_DISTANCE_MAX = "inter_connection_link_distance_max";
   protected static final String WALK_DEFAULT_SPEED_MAX = "walk_default_speed_max";
   protected static final String WALK_OCCASIONAL_TRAVELLER_SPEED_MAX = "walk_occasional_traveller_speed_max";
   protected static final String WALK_FREQUENT_TRAVELLER_SPEED_MAX = "walk_frequent_traveller_speed_max";
   protected static final String WALK_MOBILITY_RESTRICTED_TRAVELLER_SPEED_MAX = "walk_mobility_restricted_traveller_speed_max";
   protected static final String INTER_ACCESS_LINK_DISTANCE_MAX = "inter_access_link_distance_max";
   protected static final String INTER_STOP_DURATION_MAX = "inter_stop_duration_max";
   protected static final String FACILITY_STOP_AREA_DISTANCE_MAX = "facility_stop_area_distance_max";
   protected static final String MODE_PREFIX = "mode_";
   protected static final String MODE_OTHER = "mode_other";
   protected static final String INTER_STOP_AREA_DISTANCE_MAX = "inter_stop_area_distance_max";
   protected static final String SPEED_MAX = "speed_max";
   protected static final String SPEED_MIN = "speed_min";
   protected static final String INTER_STOP_DURATION_VARIATION_MAX = "inter_stop_duration_variation_max";
   protected static final String CHECK_ALLOWED_TRANSPORT_MODES = "check_allowed_transport_modes";
   protected static final String ALLOWED_TRANSPORT = "allowed_transport";
   protected static final String VEHICLE_JOURNEY_NUMBER_MIN = "vehicle_journey_number_min";
   protected static final String VEHICLE_JOURNEY_NUMBER_MAX = "vehicle_journey_number_max";

   protected static final JSONObject mode_default = new JSONObject(" {"
         + "\"allowed_transport\": 1, "
         + "\"inter_stop_area_distance_min\": 300, "
         + "\"inter_stop_area_distance_max\": 30000, " + "\"speed_max\": 40, "
         + "\"speed_min\": 10, " + "\"inter_stop_duration_variation_max\": 10 "
         + "}");

   private GeometryFactory geometryFactory;

   /**
    * create checkPoint entry with status uncheck
    * 
    * @param validationItem
    * @param checkPointKey
    * @param severity
    */
   protected void initCheckPoint(PhaseReportItem validationItem,
         String checkPointKey, CheckPointReportItem.SEVERITY severity)
   {
      int order = validationItem.hasItems() ? validationItem.getItems().size()
            : 0;
      validationItem.addItem(new CheckPointReportItem(checkPointKey, order,
            Report.STATE.UNCHECK, severity));
   }

   /**
    * pass checkpoint to ok if uncheck
    * 
    * @param validationReport
    * @param checkPointKey
    */
   protected void prepareCheckPoint(PhaseReportItem validationReport,
         String checkPointKey)
   {
      CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
      if (!checkPoint.hasItems())
         checkPoint.updateStatus(Report.STATE.OK);
   }

   /**
    * add a detail on a checkpoint
    * 
    * @param validationReport
    * @param checkPointKey
    * @param item
    */
   protected void addValidationError(PhaseReportItem validationReport,
         String checkPointKey, DetailReportItem item)
   {
      CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
      checkPoint.addItem(item);

   }

   /**
    * check if an object has coordinates
    * 
    * @param obj
    * @return
    */
   protected boolean hasCoordinates(NeptuneLocalizedObject obj)
   {
      return obj.getLongLatType() != null && obj.getLongitude() != null
            && obj.getLatitude() != null;
   }

   /**
    * calculate distance on spheroid
    * 
    * return 0 if one object has no coordinate
    * 
    * @param obj1
    * @param obj2
    * @return
    */
   protected double distance(NeptuneLocalizedObject obj1,
         NeptuneLocalizedObject obj2)
   {
      if (obj1.hasCoordinates() && obj2.hasCoordinates())
         return computeHaversineFormula(obj1, obj2);
      else
         return 0;
   }

   /**
    * @see http://mathforum.org/library/drmath/view/51879.html
    */
   private double computeHaversineFormula(NeptuneLocalizedObject obj1,
         NeptuneLocalizedObject obj2)
   {

      double lon1 = Math.toRadians(obj1.getLongitude().doubleValue());
      double lat1 = Math.toRadians(obj1.getLatitude().doubleValue());
      double lon2 = Math.toRadians(obj2.getLongitude().doubleValue());
      double lat2 = Math.toRadians(obj2.getLatitude().doubleValue());

      final double R = 6371008.8;

      double dlon = lon2 - lon1;
      double dlat = lat2 - lat1;

      double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1)
            * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      double d = R * c;

      return d;
   }

   protected boolean isEmpty(List<? extends Object> list)
   {
      return list == null || list.isEmpty();
   }

   protected boolean isEmpty(String string)
   {
      return string == null || string.isEmpty();
   }

   protected String toUnderscore(String camelcase)
   {

      return camelcase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
   }

   protected long getModeParameter(JSONObject parameters, String modeKey,
         String key)
   {
      // find transportMode :
      modeKey = MODE_PREFIX + toUnderscore(modeKey);
      JSONObject mode = parameters.optJSONObject(modeKey);
      JSONObject modeOther = parameters.optJSONObject(MODE_OTHER);
      if (mode == null || !mode.has(key))
      {
         log.error("no parameter " + key + " for mode " + modeKey);
         mode = parameters.optJSONObject(MODE_OTHER);
         if (modeOther == null || !modeOther.has(key))
         {
            log.error("no parameter " + key + " for mode " + MODE_OTHER);
            mode = mode_default;
         }
      }
      return mode.getLong(key);
   }

   /**
    * @param parameters
    * @return
    */
   protected Polygon getEnveloppe(JSONObject parameters)
   {
      // validationPerimeter : defalut = France
      String perimeter = parameters.optString(STOP_AREAS_AREA,
            "[[-5.2,42.25],[-5.2,51.1],[8.23,51.1],[8.23,42.25],[-5.2,42.25]]");
      JSONArray array = new JSONArray(perimeter);
      List<Coordinate> listCoordinates = new ArrayList<Coordinate>();
      for (int i = 0; i < array.length(); i++)
      {
         JSONArray coords = array.getJSONArray(i);
         Coordinate coord = new Coordinate(coords.getDouble(0),
               coords.getDouble(1));
         listCoordinates.add(coord);
      }
      if (!listCoordinates.get(0).equals(
            listCoordinates.get(listCoordinates.size() - 1)))
      {
         listCoordinates.add(listCoordinates.get(0));
      }
      Coordinate[] coordinates = listCoordinates.toArray(new Coordinate[0]);
      LinearRing shell = getGeometryFactory().createLinearRing(coordinates);
      LinearRing[] holes = null;
      Polygon polygon = getGeometryFactory().createPolygon(shell, holes);
      return polygon;
   }

   private GeometryFactory getGeometryFactory()
   {
      if (geometryFactory == null)
      {
         PrecisionModel precisionModel = new PrecisionModel(
               PrecisionModel.maximumPreciseValue);
         geometryFactory = new GeometryFactory(precisionModel,
               LongLatTypeEnum.WGS84.getValue());
      }
      return geometryFactory;
   }

   protected Point buildPoint(NeptuneLocalizedObject obj)
   {
      double y1 = obj.getLatitude().doubleValue();
      double x1 = obj.getLongitude().doubleValue();
      Coordinate coordinate = new Coordinate(x1, y1);
      Point point = getGeometryFactory().createPoint(coordinate);
      return point;
   }

   /**
    * @param report
    * @param object
    * @param duration
    * @param distance
    * @param maxDefaultSpeed
    * @param testCode
    * @param resultCode
    */
   protected void checkLinkSpeed(PhaseReportItem report,
         NeptuneIdentifiedObject object, Time duration, double distance,
         int maxDefaultSpeed, String testCode, String resultCode)
   {
      if (duration != null)
      {
         long time = getTimeInSeconds(duration); // in seconds

         if (time > 0)
         {
            int speed = (int) (distance / (double) time * 36 / 10 + 0.5); // (km/h)

            if (speed > maxDefaultSpeed)
            {
               ReportLocation location = new ReportLocation(object);

               Map<String, Object> map = new HashMap<String, Object>();
               map.put("name", object.getName());
               map.put("speed", Integer.valueOf(speed));
               map.put("speedLimit", Integer.valueOf(maxDefaultSpeed));

               DetailReportItem detail = new DetailReportItem(testCode
                     + resultCode, object.getObjectId(), Report.STATE.WARNING,
                     location, map);
               addValidationError(report, testCode, detail);
            }
         }
      }
   }

   protected long getTimeInSeconds(Time time)
   {
      TimeZone tz = TimeZone.getDefault();
      long millis = 0;
      millis = time.getTime() + tz.getRawOffset();
      return millis / 1000;
   }

}

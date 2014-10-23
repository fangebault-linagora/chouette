package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;

public class StopTimeByTrip extends IndexImpl<GtfsStopTime> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled
   };

   public static final String FILENAME = "stop_times.txt";
   public static final String KEY = FIELDS.trip_id.name();

   private GtfsStopTime _bean = new GtfsStopTime();
   private String[] _array = new String[FIELDS.values().length];

   private String _tripId = null;
   private String _stopId = null;

   public StopTimeByTrip(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsStopTime build(GtfsIterator reader, int id)
   {

      int i = 0;
      for (FIELDS field : FIELDS.values())
      {
         _array[i++] = getField(reader, field.name());
      }

      i = 0;
      _bean.setId(id);
      _bean.setTripId(STRING_CONVERTER.from(_array[i++], true));
      _bean.setStopId(STRING_CONVERTER.from(_array[i++], true));
      _bean.setStopSequence(INTEGER_CONVERTER.from(_array[i++], true));
      _bean.setArrivalTime(GTFSTIME_CONVERTER.from(_array[i++], true));
      _bean.setDepartureTime(GTFSTIME_CONVERTER.from(_array[i++], true));
      _bean.setStopHeadsign(STRING_CONVERTER.from(_array[i++], false));
      _bean.setPickupType(PICKUP_CONVERTER.from(_array[i++],
            PickupType.Scheduled, false));
      _bean.setDropOffType(DROPOFFTYPE_CONVERTER.from(_array[i++],
            DropOffType.Scheduled, false));
      _bean.setShapeDistTraveled(FLOAT_CONVERTER.from(_array[i++], false));

      return _bean;
   }

   @Override
   public boolean validate(GtfsStopTime bean, GtfsImporter dao)
   {
      boolean result = true;
      String tripId = bean.getTripId();
      if (!tripId.equals(_tripId))
      {
         if (!dao.getTripById().containsKey(tripId))
         {
            throw new GtfsException("[DSU] error trip_id : " + tripId);
         }
         _tripId = tripId;
      }

      String stopId = bean.getStopId();
      if (!stopId.equals(_stopId))
      {
         if (!dao.getStopById().containsKey(stopId))
         {
            throw new GtfsException("[DSU] error stopid : " + _stopId);
         }
         _stopId = stopId;
      }

      return result;
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index<GtfsStopTime> create(String name) throws IOException
      {
         return new StopTimeByTrip(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(StopTimeByTrip.class.getName(), factory);
   }

}
package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.File;
import java.nio.file.Paths;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class GtfsImporter
{
   private String _path;

   private Importer<GtfsAgency> agencyImporter;
   private Importer<GtfsCalendar> calendarImporter;
   private Importer<GtfsCalendarDate> calendarDateImporter;
   private Importer<GtfsFrequency> frequencyImporter;
   private Importer<GtfsRoute> routeImporter;
   private Importer<GtfsStop> stopImporter;
   private Importer<GtfsStopTime> stopTimeImporter;
   private Importer<GtfsTransfer> transferImporter;
   private Importer<GtfsTrip> tripImporter;

   public GtfsImporter(String path)
   {
      _path = path;
   }

   public void dispose()
   {
      if (agencyImporter != null)
      {
         agencyImporter.dispose();
      }
      if (calendarImporter != null)
      {
         calendarImporter.dispose();
      }
      if (calendarDateImporter != null)
      {
         calendarDateImporter.dispose();
      }
      if (frequencyImporter != null)
      {
         frequencyImporter.dispose();
      }
      if (routeImporter != null)
      {
         routeImporter.dispose();
      }
      if (stopImporter != null)
      {
         stopImporter.dispose();
      }
      if (stopTimeImporter != null)
      {
         stopTimeImporter.dispose();
      }
      if (transferImporter != null)
      {
         transferImporter.dispose();
      }
      if (tripImporter != null)
      {
         tripImporter.dispose();
      }
   }

   public Importer<GtfsAgency> getAgencyImporter() throws Exception
   {
      if (agencyImporter == null)
      {
         agencyImporter = ImporterFactory.build(Paths.get(_path,
               AgencyImporter.FILENAME).toString());
      }
      return agencyImporter;
   }
   
   public boolean hasAgencyImporter()
   {
      return hasImporter(AgencyImporter.FILENAME);
   }

   public boolean hasCalendarImporter()
   {
      return hasImporter(CalendarImporter.FILENAME);
   }

   public boolean hasCalendarDateImporter()
   {
      return hasImporter(CalendarDatesImporter.FILENAME);
   }

   public boolean hasFrequencyImporter()
   {
      return hasImporter(FrequenciesImporter.FILENAME);
   }

   public boolean hasRouteImporter()
   {
      return hasImporter(RoutesImporter.FILENAME);
   }

   public boolean hasStopImporter()
   {
      return hasImporter(StopsImporter.FILENAME);
   }

   public boolean hasStopTimeImporter()
   {
      return hasImporter(StopTimesImporter.FILENAME);
   }

   public boolean hasTransferImporter()
   {
      return hasImporter(TransfersImporter.FILENAME);
   }

   public boolean hasTripImporter()
   {
      return hasImporter(TripsImporter.FILENAME);
   }

   private boolean hasImporter(String filename)
   {
      File f = new File(_path,filename);
      return f.exists();
   }

   public Importer<GtfsCalendar> getCalendarImporter() throws Exception
   {
      if (calendarImporter == null)
      {
         calendarImporter = ImporterFactory.build(Paths.get(_path,
               CalendarImporter.FILENAME).toString());
      }
      return calendarImporter;
   }

   public Importer<GtfsCalendarDate> getCalendarDateImporter() throws Exception
   {
      if (calendarDateImporter == null)
      {
         calendarDateImporter = ImporterFactory.build(Paths.get(_path,
               CalendarDatesImporter.FILENAME).toString());

      }
      return calendarDateImporter;
   }

   public Importer<GtfsFrequency> getFrequencyImporter() throws Exception
   {
      if (frequencyImporter == null)
      {
         frequencyImporter = ImporterFactory.build(Paths.get(_path,
               FrequenciesImporter.FILENAME).toString());
      }
      return frequencyImporter;
   }

   public Importer<GtfsRoute> getRouteImporter() throws Exception
   {
      if (routeImporter == null)
      {
         routeImporter = ImporterFactory.build(Paths.get(_path,
               RoutesImporter.FILENAME).toString());

      }
      return routeImporter;
   }

   public Importer<GtfsStop> getStopImporter() throws Exception
   {
      if (stopImporter == null)
      {
         stopImporter = ImporterFactory.build(Paths.get(_path,
               StopsImporter.FILENAME).toString());

      }
      return stopImporter;
   }

   public Importer<GtfsStopTime> getStopTimeImporter() throws Exception
   {
      if (stopTimeImporter == null)
      {
         stopTimeImporter = ImporterFactory.build(Paths.get(_path,
               StopTimesImporter.FILENAME).toString());

      }
      return stopTimeImporter;
   }

   public Importer<GtfsTransfer> getTransferImporter() throws Exception
   {
      if (transferImporter == null)
      {
         transferImporter = ImporterFactory.build(Paths.get(_path,
               TransfersImporter.FILENAME).toString());

      }
      return transferImporter;
   }

   public Importer<GtfsTrip> getTripImporter() throws Exception
   {
      if (tripImporter == null)
      {
         tripImporter = ImporterFactory.build(Paths.get(_path,
               TripsImporter.FILENAME).toString());

      }
      return tripImporter;
   }

}

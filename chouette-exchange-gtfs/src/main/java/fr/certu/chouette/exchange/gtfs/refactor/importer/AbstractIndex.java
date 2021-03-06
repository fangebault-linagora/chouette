package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;
import java.util.Set;

import lombok.ToString;

public abstract class AbstractIndex<T> implements Index<T>
{

   public static final String IDS = "ids";

   protected abstract void initialize() throws IOException;

   protected abstract Set<String> getFieldIds();

   protected abstract void index() throws IOException;

//   protected abstract ByteBuffer getBuffer(String id, Context context);
//
//   protected abstract ByteBuffer getBuffer(Token id, Context context);

   protected abstract T build(GtfsIterator reader, Context context);

   @ToString
   class Token
   {
      int offset = -1;
      int lenght = 0;
   }

}

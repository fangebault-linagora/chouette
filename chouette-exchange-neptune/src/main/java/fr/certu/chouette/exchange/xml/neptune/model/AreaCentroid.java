package fr.certu.chouette.exchange.xml.neptune.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

/**
 * Neptune AreaCentroid : Geographic informations for a unique StopArea
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@NoArgsConstructor
public class AreaCentroid extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -5908896146442329392L;

   @Getter
   @Setter
   private String name;

   /**
    * postal Address <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Address address;
   /**
    * Spatial Referential Type (actually only WGS84 is valid) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private LongLatTypeEnum longLatType;
   /**
    * Latitude position of area <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private BigDecimal latitude;
   /**
    * Longitude position of area <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private BigDecimal longitude;
   /**
    * Optional other Spatial Referential position <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private ProjectedPoint projectedPoint;
   /**
    * Optional comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String comment;
   /**
    * StopArea Neptune identifier <br/>
    * Changes have no effect on database (see StopArea container) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   private String containedInStopAreaId;
   /**
    * backward reference to attached StopArea <br/>
    * Changes have no effect on database (see StopArea container) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopArea containedInStopArea;

   public AreaCentroid(StopArea area)
   {
      this.containedInStopArea = area;
      if (area.hasAddress())
         this.address = new Address(area);
      this.longLatType = area.getLongLatType();
      this.latitude = area.getLatitude();
      this.longitude = area.getLongitude();
      if (area.hasProjection())
         this.projectedPoint = new ProjectedPoint(area);
   }

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));

      if (address != null)
      {
         sb.append("\n").append(indent).append("  address = ").append(address);
      }

      if (longLatType != null)
      {
         sb.append("\n").append(indent).append("  longLatType = ")
               .append(longLatType);
      }

      sb.append("\n").append(indent).append("  latitude = ").append(latitude);
      sb.append("\n").append(indent).append("  longitude = ").append(longitude);

      if (projectedPoint != null)
      {
         sb.append("\n").append(indent).append("  projectedPoint = ")
               .append(projectedPoint);
      }

      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  containedInStopAreaId = ")
            .append(containedInStopAreaId);

      return sb.toString();
   }

   public void setContainedInStopAreaId(String stopareaId)
   {
      this.containedInStopAreaId = stopareaId;
      if (getObjectId() == null && stopareaId != null)
      {
         String[] ids = stopareaId.split(":");
         if (ids.length == 3)
            setObjectId(ids[0] + ":" + AREACENTROID_KEY + ":" + ids[2]);
      }
   }

   public void populateStopArea(StopArea area)
   {
      if (address != null)
         address.populateStoparea(area);
      area.setLongLatType(longLatType);
      area.setLatitude(latitude);
      area.setLongitude(longitude);
      if (projectedPoint != null)
         projectedPoint.populateStopArea(area);
   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof AreaCentroid)
      {
         AreaCentroid another = (AreaCentroid) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getLatitude(), another.getLatitude()))
            return false;
         if (!sameValue(this.getLongitude(), another.getLongitude()))
            return false;
         if (!sameValue(this.getLongLatType(), another.getLongLatType()))
            return false;
         if (!sameValue(this.getAddress(), another.getAddress()))
            return false;
         if (!sameValue(this.getProjectedPoint(), another.getProjectedPoint()))
            return false;
         return true;
      } else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return null;
   }

}

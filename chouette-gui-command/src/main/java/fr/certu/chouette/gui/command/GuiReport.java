/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.gui.command;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 *
 */
public class GuiReport extends Report
{

   /**
	 * 
	 */
   public GuiReport()
   {
   }

   public GuiReport(String originKey, STATE status)
   {
      updateStatus(status);
      setOriginKey(originKey);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin
    * .report.ReportItem)
    */
   @Override
   public void addItem(ReportItem item)
   {
      if (getItems() == null)
      {
         super.addItem(item);
      } else
      {
         getItems().add(item);
      }
   }

}

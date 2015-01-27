package mobi.chouette.model;

import java.sql.Time;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.util.ObjectIdTypes;

@Entity
@Table(name = "time_slots")
@Cacheable
@NoArgsConstructor
public class TimeSlot extends NeptuneLocalizedObject implements ObjectIdTypes {
	private static final long serialVersionUID = 748418019787098435L;
	@Getter
	@Setter
	@Column(name = "beginningSlotTime")
	private Time beginningSlotTime;
	@Getter
	@Setter
	@Column(name = "endSlotTime")
	private Time endSlotTime;
	@Getter
	@Setter
	@Column(name = "name")
	private String name;
	@Getter
	@Setter
	@Column(name = "firstDepartureTimeInSlot")
	private Time firstDepartureTimeInSlot;
	@Getter
	@Setter
	@Column(name = "lastDepartureTimeInSlot")
	private Time lastDepartureTimeInSlot;
}

package molab.main.java.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CtSubRunner entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "CT_SUB_RUNNER", catalog = "MOLAB")
public class CtSubRunner implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer runnerId;
	private Float cpu;
	private Long memory;
	private long uptraffic;
	private long downtraffic;
	private Float batteryCpu;
	private Float batteryScreen;
	private Float batteryWifi;
	private Float fps;
	private int state;

	// Constructors

	/** default constructor */
	public CtSubRunner() {
	}

	/** minimal constructor */
	public CtSubRunner(Integer runnerId) {
		this.runnerId = runnerId;
	}

	/** full constructor */
	public CtSubRunner(Integer runnerId, Float cpu, Long memory) {
		this.runnerId = runnerId;
		this.cpu = cpu;
		this.memory = memory;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "RUNNER_ID", nullable = false)
	public Integer getRunnerId() {
		return this.runnerId;
	}

	public void setRunnerId(Integer runnerId) {
		this.runnerId = runnerId;
	}

	@Column(name = "CPU", precision = 12, scale = 0)
	public Float getCpu() {
		return this.cpu;
	}

	public void setCpu(Float cpu) {
		this.cpu = cpu;
	}

	@Column(name = "MEMORY")
	public Long getMemory() {
		return this.memory;
	}

	public void setMemory(Long memory) {
		this.memory = memory;
	}
	
	@Column(name = "UPTRAFFIC")
	public long getUptraffic() {
		return uptraffic;
	}

	public void setUptraffic(long uptraffic) {
		this.uptraffic = uptraffic;
	}

	@Column(name = "DOWNTRAFFIC")
	public long getDowntraffic() {
		return downtraffic;
	}

	public void setDowntraffic(long downtraffic) {
		this.downtraffic = downtraffic;
	}

	@Column(name = "BATTERY_CPU", precision = 12, scale = 0)
	public Float getBatteryCpu() {
		return batteryCpu;
	}

	public void setBatteryCpu(Float batteryCpu) {
		this.batteryCpu = batteryCpu;
	}

	@Column(name = "BATTERY_SCREEN", precision = 12, scale = 0)
	public Float getBatteryScreen() {
		return batteryScreen;
	}

	public void setBatteryScreen(Float batteryScreen) {
		this.batteryScreen = batteryScreen;
	}

	@Column(name = "BATTERY_WIFI", precision = 12, scale = 0)
	public Float getBatteryWifi() {
		return batteryWifi;
	}

	public void setBatteryWifi(Float batteryWifi) {
		this.batteryWifi = batteryWifi;
	}

	@Column(name = "FPS", precision = 12, scale = 0)
	public Float getFps() {
		return fps;
	}

	public void setFps(Float fps) {
		this.fps = fps;
	}

	@Column(name = "STATE")
	public Integer getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
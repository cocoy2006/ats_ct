package molab.main.java.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Device entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "DEVICE", catalog = "MOLAB")
public class Device implements java.io.Serializable {

	// Fields

	private Integer id;
	private String server;
	private Integer port;
	private String sn;
	private String label;
	private String manufacturer;
	private String model;
	private String os;
	private int sdk;
	private String rom;
	private int width;
	private int height;
	private int type;
	private int uses;
	private int state;

	// Constructors

	/** default constructor */
	public Device() {
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

	@Column(name = "SERVER", nullable = false, length = 255)
	public String getServer() {
		return this.server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@Column(name = "PORT", nullable = false)
	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Column(name = "SN", nullable = false, length = 255)
	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(name = "LABEL", length = 255)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "MANUFACTURER", length = 255)
	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Column(name = "MODEL", length = 255)
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name = "OS", length = 255)
	public String getOs() {
		return this.os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	@Column(name = "SDK")
	public Integer getSdk() {
		return this.sdk;
	}

	public void setSdk(Integer sdk) {
		this.sdk = sdk;
	}

	@Column(name = "ROM", length = 255)
	public String getRom() {
		return this.rom;
	}

	public void setRom(String rom) {
		this.rom = rom;
	}

	@Column(name = "WIDTH")
	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Column(name = "HEIGHT")
	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@Column(name = "TYPE", nullable = false)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "USES", nullable = false)
	public Integer getUses() {
		return this.uses;
	}

	public void setUses(Integer uses) {
		this.uses = uses;
	}

	@Column(name = "STATE", nullable = false)
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
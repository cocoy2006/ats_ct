package molab.main.java.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CtDispatcher entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "CT_DISPATCHER", catalog = "MOLAB")
public class CtDispatcher implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer customerId;
	private Integer applicationId;
	private Integer holdTime;
	private Long oprTime;
	@Basic(fetch = FetchType.EAGER)
	private Integer state;

	// Constructors

	/** default constructor */
	public CtDispatcher() {
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

	@Column(name = "CUSTOMER_ID", nullable = false)
	public Integer getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	@Column(name = "APPLICATION_ID", nullable = false)
	public Integer getApplicationId() {
		return this.applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}
	
	@Column(name = "HOLD_TIME")
	public Integer getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(Integer holdTime) {
		this.holdTime = holdTime;
	}

	@Column(name = "OPR_TIME")
	public Long getOprTime() {
		return this.oprTime;
	}

	public void setOprTime(Long oprTime) {
		this.oprTime = oprTime;
	}

	@Column(name = "STATE", nullable = false)
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
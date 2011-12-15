
/**
 *
 * Roleset.java 
 * @author echeng (table2type.pl)
 *  
 * the Roleset
 *
 *
 */

package edu.yu.einstein.wasp.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import org.hibernate.validator.constraints.*;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Audited
@Table(name="roleset")
public class Roleset extends WaspModel {

	/** 
	 * rolesetId
	 *
	 */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer rolesetId;

	/**
	 * setRolesetId(Integer rolesetId)
	 *
	 * @param rolesetId
	 *
	 */
	
	public void setRolesetId (Integer rolesetId) {
		this.rolesetId = rolesetId;
	}

	/**
	 * getRolesetId()
	 *
	 * @return rolesetId
	 *
	 */
	public Integer getRolesetId () {
		return this.rolesetId;
	}




	/** 
	 * parentroleId
	 *
	 */
	@Column(name="parentroleid")
	protected Integer parentroleId;

	/**
	 * setParentroleId(Integer parentroleId)
	 *
	 * @param parentroleId
	 *
	 */
	
	public void setParentroleId (Integer parentroleId) {
		this.parentroleId = parentroleId;
	}

	/**
	 * getParentroleId()
	 *
	 * @return parentroleId
	 *
	 */
	public Integer getParentroleId () {
		return this.parentroleId;
	}




	/** 
	 * childroleId
	 *
	 */
	@Column(name="childroleid")
	protected Integer childroleId;

	/**
	 * setChildroleId(Integer childroleId)
	 *
	 * @param childroleId
	 *
	 */
	
	public void setChildroleId (Integer childroleId) {
		this.childroleId = childroleId;
	}

	/**
	 * getChildroleId()
	 *
	 * @return childroleId
	 *
	 */
	public Integer getChildroleId () {
		return this.childroleId;
	}




	/**
	 * role
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="parentroleid", insertable=false, updatable=false)
	protected Role role;

	/**
	 * setRole (Role role)
	 *
	 * @param role
	 *
	 */
	public void setRole (Role role) {
		this.role = role;
		this.parentroleId = role.roleId;
	}

	/**
	 * getRole ()
	 *
	 * @return role
	 *
	 */
	
	public Role getRole () {
		return this.role;
	}


	/**
	 * roleVia
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="childroleid", insertable=false, updatable=false)
	protected Role roleVia;

	/**
	 * setRoleVia (Role role)
	 *
	 * @param role
	 *
	 */
	public void setRoleVia (Role role) {
		this.role = role;
		this.childroleId = role.roleId;
	}

	/**
	 * getRoleVia ()
	 *
	 * @return role
	 *
	 */
	
	public Role getRoleVia () {
		return this.role;
	}


}

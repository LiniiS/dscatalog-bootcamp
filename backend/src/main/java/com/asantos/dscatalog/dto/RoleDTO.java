package com.asantos.dscatalog.dto;

import java.io.Serializable;

import com.asantos.dscatalog.entities.Role;

public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String authority;

	public RoleDTO() {
	}

	public RoleDTO(Long id, String authority) {
		this.id = id;
		this.authority = authority;
	}

	//usado pra popular o cnj de Roles dentro do UserDTO
	public RoleDTO(Role role) {
		id = role.getId();
		authority = role.getAuthority();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

}

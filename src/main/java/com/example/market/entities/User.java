package com.example.market.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
@Entity
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(mappedBy="user",fetch=FetchType.EAGER)
	private List<Item> items;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="orders",
		joinColumns=@JoinColumn(name="user_id",referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="item_id",referencedColumnName="id"))
	private Set<Item> orders;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="likes",
		joinColumns=@JoinColumn(name="user_id",referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="item_id",referencedColumnName="id"))
	private Set<Item> likeItems = new HashSet<Item>();
	
	@Column(name="name",length=255,nullable=false)
	private String name;
	
	@Column(name="email",length=255,nullable=false,unique=true)
	private String email;
	
	@Column(name="password",length=100,nullable=false)
	private String password;
	
	@Column(name="roles")
	private String roles;
	
	@Column(name="enable_flag",nullable=false)
	private Boolean enable;
	
	@Column(name="profile",length=1000)
	private String profile;
	
	@Column(name="image")
	private String image;
	
	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(o==null || getClass()!=getClass())
			return false;
		User user=(User) o;
		return Objects.equals(id,user.id) && Objects.equals(name,user.name)
				&& Objects.equals(email,user.email);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id,name,email);
	}
}

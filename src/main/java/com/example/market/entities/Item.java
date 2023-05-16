package com.example.market.entities;

import java.time.ZonedDateTime;
import java.util.HashSet;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="item")
@Entity
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private User user;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="orders",
		joinColumns=@JoinColumn(name="item_id",referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="user_id",referencedColumnName="id"))
	private Set<User> orders=new HashSet<User>();
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="likes",
		joinColumns=@JoinColumn(name="item_id",referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="user_id",referencedColumnName="id"))
	private Set<User> likedUsers = new HashSet<User>();
	
	@Column(name="name",nullable=false)
	private String name;
	
	@Column(name="description",nullable=false,length=1000)
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Category category;
	
	@Column(name="price",nullable=false)
	private Integer price;
	
	@Column(name="image")
	private String image;
	
	@Column(name="createdat",nullable=false,updatable=false,insertable=false,
	columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdat;
	
	@Column(name="updatedat",nullable=false,updatable=false,insertable=false,
	columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedat;
}

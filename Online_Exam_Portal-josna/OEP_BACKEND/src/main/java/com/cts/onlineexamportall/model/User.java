package com.cts.onlineexamportall.model;

import java.util.Set;
import java.util.UUID;

import com.cts.onlineexamportall.enums.Role;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "User")
public class User {
		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private UUID userId;
		
		@Column(name = "user_name", unique = true, nullable = false)
		@NotBlank(message = "User name is required")
		@Size(max = 50, message = "User name cannot exceed 50 characters")
		private String userName;
	
		@Column(name = "email", unique = true, nullable = false)
		@NotBlank(message = "Email is required")
		@Email(message = "Email should be valid")
		private String email;
	
		@Column(name = "password", nullable = false)
		@NotBlank(message = "Password is required")
		@Size(min = 6, message = "Password should be at least 6 characters long")
		private String password;
	
		@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
		@Enumerated(EnumType.STRING)
		@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
		@Column(name = "role")
		private Set<Role> roles;
}

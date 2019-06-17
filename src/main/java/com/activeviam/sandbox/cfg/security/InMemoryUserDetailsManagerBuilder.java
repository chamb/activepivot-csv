/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.activeviam.sandbox.cfg.security;

import java.util.ArrayList;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * An In-memory {@link UserDetailsService} builder which can be used without
 * {@link AuthenticationManagerBuilder} contrary to {@link InMemoryUserDetailsManagerConfigurer}.
 *
 * @author ActiveViam
 */
public class InMemoryUserDetailsManagerBuilder extends UserDetailsManagerConfigurer<AuthenticationManagerBuilder, InMemoryUserDetailsManagerBuilder> {

	/** Creates a new instance */
	public InMemoryUserDetailsManagerBuilder() {
		super(new InMemoryUserDetailsManager(new ArrayList<UserDetails>()));
	}

	@Override
	public void configure(AuthenticationManagerBuilder builder) throws Exception {
		if (null != builder) {
			throw new IllegalArgumentException();
		}
		initUserDetailsService();
	}

	/**
	 * Builds the In-memory {@link UserDetailsManager} and returns it
	 * @return user details manager
	 */
	public UserDetailsManager build() {
		try {
			configure(null);
			return getUserDetailsService();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

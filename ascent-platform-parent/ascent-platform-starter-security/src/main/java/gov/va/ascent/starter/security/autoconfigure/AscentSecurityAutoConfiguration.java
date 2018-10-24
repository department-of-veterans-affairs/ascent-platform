package gov.va.ascent.starter.security.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gov.va.ascent.security.TokenResource;
import gov.va.ascent.security.handler.JwtAuthenticationEntryPoint;
import gov.va.ascent.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.ascent.security.jwt.JwtAuthenticationFilter;
import gov.va.ascent.security.jwt.JwtAuthenticationProperties;
import gov.va.ascent.security.jwt.JwtAuthenticationProvider;
import gov.va.ascent.security.jwt.JwtParser;
import gov.va.ascent.security.jwt.JwtTokenService;

/**
 * Autoconfiguration for various authentication types on the Ascent Platform (basic auth, JWT)
 */
@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(JwtAuthenticationProperties.class)
public class AscentSecurityAutoConfiguration {

	/**
	 * Adapter for basic auth (username / password)
	 */
	@Configuration
	@Order(JwtAuthenticationProperties.AUTH_ORDER - 1)
	protected static class BasicAuthWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {

		@Autowired
		private SecurityProperties securityProperties;

		@Autowired
		@Override
		protected void configure(AuthenticationManagerBuilder auth) {
			addInMemoryAuthenticationProvider(auth);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic().and()
					.authorizeRequests()
					.antMatchers(HttpMethod.GET, "/info").permitAll()//
					.antMatchers("/actuator/**").authenticated()//
					.and().csrf().disable();
		}

		private void addInMemoryAuthenticationProvider(AuthenticationManagerBuilder auth) {
			try {
				auth.inMemoryAuthentication()
						.withUser(securityProperties.getUser().getName())
						.password(securityProperties.getUser().getPassword())
						.roles(securityProperties.getUser().getRole().stream().toArray(String[]::new));
			} catch (Exception ex) {
				throw new IllegalStateException("Cannot add InMemory users!", ex);
			}
		}

	}

	/**
	 * Adapter for JWT
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "ascent.security.jwt", name = "enabled", matchIfMissing = true)
	@Order(JwtAuthenticationProperties.AUTH_ORDER)
	protected static class JwtWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {
		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/token**").permitAll()
					.antMatchers(jwtAuthenticationProperties.getFilterProcessUrl()).authenticated()
					.and()
					.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
					.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and().csrf().disable();
			http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
			http.headers().cacheControl();

		}

		@Bean
		protected AuthenticationEntryPoint authenticationEntryPoint() {
			return new JwtAuthenticationEntryPoint();
		}

		@Bean
		protected AuthenticationProvider jwtAuthenticationProvider() {
			return new JwtAuthenticationProvider(new JwtParser(jwtAuthenticationProperties));
		}

		@Bean
		protected AuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
			return new JwtAuthenticationSuccessHandler();
		}

		@Bean
		protected JwtAuthenticationFilter jwtAuthenticationFilter() {
			return new JwtAuthenticationFilter(jwtAuthenticationProperties, jwtAuthenticationSuccessHandler(),
					jwtAuthenticationProvider());
		}
	}

	/**
	 * Adapter that only processes URLs specified in the filter
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "ascent.security.jwt", name = "enabled", havingValue = "false")
	@Order(JwtAuthenticationProperties.AUTH_ORDER)
	protected static class JwtNoWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(jwtAuthenticationProperties.getFilterProcessUrl());
		}

	}

	/**
	 * Adapter that only excludes specified URLs
	 */
	@Configuration
	@Order(JwtAuthenticationProperties.NO_AUTH_ORDER)
	protected static class NoWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(jwtAuthenticationProperties.getExcludeUrls());
		}

	}

	/**
	 * Security properties used for both JWT and Basic Auth authentication.
	 * Spring configuration (yml / properties, etc) provides values to this object.
	 *
	 * @return JwtAuthenticationProperties the properties
	 */
	@Bean
	@ConditionalOnMissingBean
	public JwtAuthenticationProperties jwtAuthenticationProperties() {
		return new JwtAuthenticationProperties();
	}

	/**
	 * The service component for processing JWT
	 *
	 * @return JwtTokenService the service component
	 */
	@Bean
	@ConditionalOnMissingBean
	public JwtTokenService jwtTokenService() {
		return new JwtTokenService();
	}

	/**
	 * The REST Controller that creates a "valid" JWT token that can be used for testing.
	 *
	 * @return TokenResource the rest controller
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnExpression("${ascent.security.jwt.enabled:true} && ${ascent.security.jwt.generate.enabled:true}")
	public TokenResource tokenResource() {
		return new TokenResource();
	}
}
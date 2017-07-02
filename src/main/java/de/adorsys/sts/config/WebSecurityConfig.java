package de.adorsys.sts.config;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;

import de.adorsys.sts.token.TokenService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private TokenService tokenAuthenticationService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/", "/pop", "/api-docs/**", "/v2/api-docs/**", "/health", "/health.json", "/info", "/info.json").permitAll()
			.antMatchers("/token/**").permitAll()// TOken Endpoint
			.antMatchers(actuatorEndpoints()).hasRole("ADMIN")
			.antMatchers("/accounts").hasRole("USER")
			.antMatchers("/bankAccess").hasRole("ADMIN")
			.antMatchers(actuatorEndpoints()).denyAll()
			.anyRequest().authenticated()
//			.and()
//			.anonymous().disable()
			;
		// And filter other requests to check the presence of JWT in header
		 http
		 	.addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
		 	.addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)		
		 ;
	}
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Create a default account
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
	}
	
	private String[] actuatorEndpoints() {
        return new String[]{"/auditevents", "/auditevents.json", "/dump", "/dump.json", "/metrics/**", "/metrics", "/metrics.json",
        		"/beans", "/beans.json", "/loggers/**", "/loggers", "/loggers.json", "/trace", "/trace.json","/configprops", "/configprops.json",
        		"/heapdump", "/heapdump.json", "/autoconfig", "/autoconfig.json", "/mappings", "/mappings.json", "/env/**", "/env", "/env.json"};
    }	

	@Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST,proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Principal getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();

    }

//	UserDataNamingPolicy userDataNamingPolicy = new UserDataNamingPolicy("STS");
//	
//	@Bean
//    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST,proxyMode = ScopedProxyMode.TARGET_CLASS)
//	public ObjectPersistenceAdapter persistenceAdapter(){
//		String subject = getPrincipal().getName();
//		KeyCredentials keyCredentials = userDataNamingPolicy.newKeyCredntials(subject, );
//		BlobStoreContextFactory blobStoreContextFactory = new FileSystemBlobStoreContextFactory(keyCredentials.getHandle().getContainer());
//		EncObjectService encObjectService = new EncObjectService(blobStoreContextFactory);
//		return new ObjectPersistenceAdapter(encObjectService, keyCredentials);
//	}
}

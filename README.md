# Embed Pentaho Server Sample

This project it's a sample of how embed the Pentaho server with a 3rd Web APP using a TOKEN hash.

- Video tutorial [pt_BR]

[![Video tutorial [pt_BR]](https://img.youtube.com/vi/2dpNhCOVt7A/0.jpg)](https://www.youtube.com/watch?v=2dpNhCOVt7A)


### References:
* https://help.pentaho.com/Documentation/8.0/Setup/Administration/User_Security/Implement_Advanced_Security/080

### Credits

Original code by Francisco Gregorio:
* https://github.com/FranciscoGregorio


## Flow Authentication
1. Authentication filter by token ([EmbedLoginFilter](src/main/java/br/com/bovbi/embed/filter/EmbedLoginFilter.java))
    - http://localhost:8080/pentaho/embed-login?token=MY_TOKEN
2. Authentication Provider validating the *token* parameter by API rest ([EmbedAuthenticationProvider](src/main/java/br/com/bovbi/embed/authentication/EmbedAuthenticationProvider.java))  

3. APP-API ([EmbedRestTemplate](src/main/java/br/com/bovbi/embed/rest/EmbedRestTemplate.java))  
3.1. Search the user logged (UserDetails) by login ([EmbedUserDetailService](src/main/java/br/com/bovbi/embed/service/EmbedUserDetailService.java))

## Build and Installation

### Build the Filter

```
mvn clean 
mvn compile
mvn install
```

After the build, copy the `/target/embed-pentaho-1.0.jar` file to pentaho-server/tomcat/webapps/pentaho/WEB-INF/lib/

### Pentaho Server XML Injection

Inside folder `pentaho-server/pentaho-solutions/system`  

Create a new file as `applicationContext-spring-security-embed.xml` and copy the following code to inject the `<bean>` calls.  

##### applicationContext-spring-security-embed.xml 
- EmbedRestTemplate 
- EmbedAuthenticationProvider 
- EmbedUserDetailService
  
````xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:pen="http://www.pentaho.com/schema/pentaho-system"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://www.pentaho.com/schema/pentaho-system http://www.pentaho.com/schema/pentaho-system.xsd" default-lazy-init="true">

	<bean id="embedRestTemplate"
		class="br.com.bovbi.embed.rest.EmbedRestTemplate">
   		<constructor-arg index="0" value="http://localhost:3000"/> 
      <constructor-arg index="1" value=""/>
	</bean>

	<bean id="embedAuthenticationProvider"
		class="br.com.bovbi.embed.authentication.EmbedAuthenticationProvider">
	    <constructor-arg>
	      <ref bean="embedRestTemplate"/>
	    </constructor-arg>		
		<pen:publish as-type="org.springframework.security.authentication.AuthenticationProvider">
	      <pen:attributes>
	        <pen:attr key="providerName" value="embed"/>
	      </pen:attributes>
	    </pen:publish>
	</bean>


	<bean id="embedUserDetailsService" class="br.com.bovbi.embed.service.EmbedUserDetailService">
		<pen:publish as-type="INTERFACES">
	      <pen:attributes>
	        <pen:attr key="providerName" value="embed"/>
	      </pen:attributes>
	    </pen:publish>
	</bean>
</beans>
````

Inject the new file in the ending of `pentaho-spring-beans.xml` to be imported.

````xml
  <import resource="applicationContext-spring-security-embed.xml" />
````

To inject the Login Filter, edit the file `applicationContext-spring-security.xml`

First add a `<bean>` tag containing:

````xml
  <bean id="embedLoginFilter" class="br.com.bovbi.embed.filter.EmbedLoginFilter">
    <constructor-arg ref="authenticationManager" />
    <property name="authenticationSuccessHandler">
      <bean class="br.com.bovbi.embed.authenticated.EmbedAuthenticationSuccessHandler">
        <property name="targetUrlParameter" value="/Home" />
      </bean>
    </property>
  </bean>
````

* It's worth noting the presence of the property `targetUrlParameter` pointing the URL to be redirected after successful login.

Add the `embedLoginFilter` bean inside the filter chain locate in the `filterChainProxy` bean definition

````xml
  <bean id="filterChainProxy" class="org.springframework.security.web.FilterChainProxy">
    <constructor-arg>
      <util:list>
        <sec:filter-chain
             ...
````  

In the filter chain with `pattern="/**"` add `embedLoginFilter`
- Attention to the position, because it is possible to have filters that alter the flow of the request!
- Add the `embedLoginFilter` before the `filterInvocationInterceptor`

````xml
     <sec:filter-chain
                    pattern="/**" 
                    filters="..., embedLoginFilter, filterInvocationInterceptor" />
````

After injecting `embedLoginFilter` into the filter `filterChainProxy`, change the injection of ProviderManager to add `EmbedAuthenticationProvider` to the list of providers.
- Locate the `authenticationManager` bean definition

````xml
  <bean id="authenticationManager" class="org.springframework.security.authentication.ProviderManager">
    <constructor-arg>
      <util:list>
        <pen:bean class="org.springframework.security.authentication.AuthenticationProvider"/>
        <ref bean="anonymousAuthenticationProvider" /> 
         ...
````

In the list being injected into the constructor, `<constructor-arg>`, add the bean `AuthenticationProvider` from embed in the first position
- It should look like this
````xml
  <bean id="authenticationManager" class="org.springframework.security.authentication.ProviderManager">
    <constructor-arg>
      <util:list>
        <pen:bean class="org.springframework.security.authentication.AuthenticationProvider">
          <pen:attributes>
            <pen:attr key="providerName" value="embed"/>
          </pen:attributes>
        </pen:bean>
        <pen:bean class="org.springframework.security.authentication.AuthenticationProvider"/>
        <ref bean="anonymousAuthenticationProvider" /> 
      </util:list>
      ...
````

Inside the file `pentahoObjects.spring.xml` locate the `UserDetailsService` bean definition

````xml
  <bean id="UserDetailsService" class="org.pentaho.platform.plugin.services.security.userrole.ChainedUserDetailsService">
    <constructor-arg>
      <list>
        <ref bean="activeUserDetailsService"/>
        <ref bean="systemUserDetailsService"/>
      </list>
    </constructor-arg>
  </bean>
````
Add `embedUserDetailsService` to the list of pentaho services
- It should look like this 
```xml
  <bean id="UserDetailsService" class="org.pentaho.platform.plugin.services.security.userrole.ChainedUserDetailsService">
    <constructor-arg>
      <list>
        <ref bean="activeUserDetailsService"/>
        <ref bean="embedUserDetailsService"/>
        <ref bean="systemUserDetailsService"/>
      </list>
    </constructor-arg>
  </bean>
```

Restart the server and monitor the pentaho log file 

```bash
tail -n 300 -f tomcat/logs/catalina.out 
```

Wait for the follow messages to be sure that is all right!

```bash
br.com.bovbi.embed.rest.EmbedRestTemplate - on
br.com.bovbi.embed.authentication.EmbedAuthenticationProvider - on
br.com.bovbi.embed.service.EmbedUserDetailService - on
br.com.bovbi.embed.filter.EmbedLoginFilter - on
```

Access the URL:
* http://localhost:8080/pentaho/embed-login?token=12345&url=/api/repos/:public:Steel Wheels:Dashboards:CTools_dashboard.wcdf/generatedContent
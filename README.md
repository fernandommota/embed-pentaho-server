# Embed Pentaho Server Sample

This project it's a sample of how embed the Pentaho server with a 3rd Web APP using a TOKEN hash.

References:
* https://help.pentaho.com/Documentation/8.0/Setup/Administration/User_Security/Implement_Advanced_Security/080
 
## Flow Authentication
1. Authentication filter by token ([EmbedLoginFilter](src/main/java/br/com/bovbi/embed/filter/EmbedLoginFilter.java))
    - http://localhost:8080/pentaho/embed-login?token=MY_TOKEN
2. Authentication Provider validating the *token* parameter by API rest ([EmbedAuthenticationProvider](src/main/java/br/com/bovbi/embed/authentication/EmbedAuthenticationProvider.java))  

3. APP-API ([EmbedRestTemplate](src/main/java/br/com/bovbi/embed/rest/EmbedRestTemplate.java))  
3.1. Search the user logged (UserDetails) por login ([EmbedUserDetailService](src/main/java/br/com/bovbi/embed/service/EmbedUserDetailService.java))


## Build

```
mvn clean 
mvn compile
mvn install
```

### Pentaho Server XML Injection
No diretório de `pentaho-server\pentaho-solutions\system`  
Criar um arquivo .xml contendo as injeções por `<bean>`  

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
   		<constructor-arg index = "0" value = "http://localhost:3000"/> 
      <constructor-arg index = "1" value = ""/>
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

Criado esse arquivo então é necessario importa eles no .xml `pentaho-spring-beans.xml`  

````xml
  <import resource="applicationContext-spring-security-embed.xml" />
````

Faltando agora injetar agora o nosso Filtro de Login, iremos editar o arquivo `applicationContext-spring-security.xml`  
Primeiramente iremos adicionar uma tag `<bean>` contendo:
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
* Vale observar a presenção da property *targetUrlParameter* apontando a URL a ser redirecionada nossa chamada após o login com sucesso.


Agora precisamos adicionar nosso bean `embedLoginFilter` dentro da cadeia de filtros  
Localize a definição do bean `filterChainProxy`
````xml
  <bean id="filterChainProxy" class="org.springframework.security.web.FilterChainProxy">
    <constructor-arg>
      <util:list>
        <sec:filter-chain
             ...
````  

Na cadeia de filtros com `pattern="/**"` adicionar o `embedLoginFilter`   
Atenção na posição, pois é possível ter filtros que alteram o fluxo da requisição!    
Adicione o `embedLoginFilter` antes dos `filterInvocationInterceptor`
````xml
     <sec:filter-chain
                    pattern="/**" 
                    filters="..., embedLoginFilter, filterInvocationInterceptor" />
````

Após a injeção do `embedLoginFilter` na cadeia de filtros, iremos alterar a injeção do ProviderManager para adicionar na lista de providers o  `EmbedAuthenticationProvider`  
Localize a definição do bean `authenticationManager`
````xml
  <bean id="authenticationManager" class="org.springframework.security.authentication.ProviderManager">
    <constructor-arg>
      <util:list>
        <pen:bean class="org.springframework.security.authentication.AuthenticationProvider"/>
        <ref bean="anonymousAuthenticationProvider" /> 
         ...
````

Na lista sendo injetada dentro do construtor, `<constructor-arg>`, adicione o bean AuthenticationProvider do embed na primeira posição  
Deverá ficar assim:
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

Iremos agora adicionar o `embedUserDetailsService` dentro da lista dos services do pentaho.  
Dentro do arquivo `pentahoObjects.spring.xml`  
Localize a definição do bean `UserDetailsService`
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

Adicione então dentro da lista a referência do bean `embedUserDetailsService`  
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

Restart the server and access the URL:
* http://localhost:8080/pentaho/embed-login?token=12345&url=/plugin/pentaho-cdf-dd/api/renderer/cde-embed.js
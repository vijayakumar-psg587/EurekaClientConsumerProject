package com.homedepot.EurekaClientConsumerProject.services;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.core.net.SyslogOutputStream;

@RestController
@RequestMapping(path="/consumeAPI")
public class EurekaClientConsumerController {
	
	@Autowired
	private DiscoveryClient disClient;
	
	@Autowired
	private Environment env;
	
	@RequestMapping(path="/DVRServices/getAllDocs", method=RequestMethod.POST, 
			consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, 
			produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public @ResponseBody ResponseEntity<String> getAllDocsFromDVRService(){
		
		
		MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
		//get all the env prop values
		Spliterator<org.springframework.core.env.PropertySource<?>> ps = propSrcs.spliterator();
		
		Stream<org.springframework.core.env.PropertySource<?>> ss = StreamSupport.stream(ps, false);
		ss.filter(getOnlyEnumerablePropertySources()).map(pis -> ((EnumerablePropertySource)pis).getPropertyNames())
		.flatMap(Arrays::stream).forEach(propNames -> {System.out.println("valies from sps:"+"propnames:"+propNames+":"+env.getProperty(propNames));});;
		
		
		RestTemplate rts = new RestTemplate();
		List<ServiceInstance> serviceInstances = disClient.getInstances(env.getProperty("DVRServiceInstanceName"));
		serviceInstances.forEach(s -> {System.out.println("URL values:"+s.getUri()+"metadata:"+s.getMetadata()+"ServiceID:"+s.getServiceId());} );
		
		
		return null;
	}
	
private static Predicate<org.springframework.core.env.PropertySource<?>> getOnlyEnumerablePropertySources(){
		
		return prop -> (prop instanceof EnumerablePropertySource);
	}
}

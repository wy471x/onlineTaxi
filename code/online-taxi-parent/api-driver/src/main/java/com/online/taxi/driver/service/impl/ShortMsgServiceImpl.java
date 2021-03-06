package com.online.taxi.driver.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.online.taxi.common.dto.sms.SmsSendRequest;
import com.online.taxi.common.dto.sms.SmsTemplateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.online.taxi.common.dto.ResponseResult;
import com.online.taxi.driver.service.RestTemplateRequestService;
import com.online.taxi.driver.service.ShortMsgService;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.web.client.RestTemplate;

/**
 * @author yueyi2019
 */
@Service
@Slf4j
public class ShortMsgServiceImpl implements ShortMsgService {
	
	@Autowired
	private RestTemplateRequestService restTemplateRequestService;

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public ResponseResult send(String phoneNumber, String code) {
		
		System.out.println("手机号和验证码："+phoneNumber+","+code);
		String http = "http://";
		String serviceName = "SERVICE-SMS";
		String uri = "/send/alisms-template";
		
		String url = http + serviceName + uri;
		SmsSendRequest smsSendRequest = new SmsSendRequest();
		String[] phoneNumbers = new String[] {phoneNumber};
		smsSendRequest.setReceivers(phoneNumbers);
		
		List<SmsTemplateDto> data = new ArrayList<SmsTemplateDto>();
		SmsTemplateDto dto = new SmsTemplateDto();
		dto.setId("SMS_144145499");
		int templateSize = 1;
		HashMap<String, Object> templateMap = new HashMap<String, Object>(templateSize);
		templateMap.put("code", code);
		dto.setTemplateMap(templateMap);
		data.add(dto);
		
		smsSendRequest.setData(data);

//		url = "";
//		ServiceInstance serviceInstance = loadBalance(serviceName);
//		url = http + serviceInstance.getHost() + ":" + serviceInstance.getPort() + uri;
//		ResponseEntity<ResponseResult> resultEntity = restTemplate.postForEntity(url, smsSendRequest, ResponseResult.class);
//		ResponseResult result = resultEntity.getBody();

//		 正常 ribbon调用
		ResponseResult result =  restTemplateRequestService.smsSend(smsSendRequest);


		
		System.out.println("调用短信服务返回的结果"+JSONObject.fromObject(result));
		return result;
	}
	
	/*
	 *	下面代码手写ribbon
	 *  以下代码：自定义负载均衡
	 */
	
	@Autowired
	DiscoveryClient discoveryClient;
	
	private ServiceInstance loadBalance(String serviceName) {
		List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
		ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
		log.info("负载均衡 选出来的ip："+instance.getHost()+",端口："+instance.getPort());
		Map<String, String> metadata = instance.getMetadata();
		return instance;
	}
	
	/*
	 *	上面代码手写ribbon 
	 */
	
}

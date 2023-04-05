package com.innobit.simulator.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innobit.simulator.service.SimulatorService;


@RestController
@CrossOrigin("*")
@RequestMapping("/simulator")
public class SimulatorController {
	@Autowired
	private SimulatorService simulatorService;
	
	private static final Logger logger = LoggerFactory.getLogger(SimulatorController.class);

	/**
	 * @author: Fardeen Mirza
	 * Functionality: API for sending data to Display Devices.
	 * @return: Custom Response
	 * @createdAt: 22/11/2022
	 * @param: no params.
	 */
	// @CrossOrigin("*")
	// @GetMapping("/train-data")
	// public List<OnlineTrain> getData() {
	// 	return  ivdOvdService.getOnlineTrainList();
	// }
	
	// @CrossOrigin("*")
	// @GetMapping("/train-color-data")
	// public ResponseEntity<ColorConfig> getColorData() {
	// 	ColorConfig colorConfig = ivdOvdService.getColorConfig();
	// 	if(colorConfig != null) {
	// 		return new ResponseEntity<>(colorConfig, HttpStatus.OK);
	// 	}
	// 	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); 
		
	// }
	
	// @CrossOrigin("*")
	// @GetMapping("/get-ad-videos")
	// public List<String> getVideoData() {
	// 	return  ivdOvdService.getVideosList();
	// }

	// @CrossOrigin("*")
	// @GetMapping("/get-default-message")
	// public ResponseEntity<DefaultMessage> getDefaultMessage() {
	// 	DefaultMessage defaultMessage = ivdOvdService.getDefaultMessage();
	// 	if(defaultMessage != null) {
	// 		return new ResponseEntity<>(defaultMessage, HttpStatus.OK);
	// 	}
	// 	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); 
	// }


	@PostConstruct
	public void postContruct() {
		try {
			 (new Thread(() -> simulatorService.startListen())).start();
			 (new Thread(() -> simulatorService.startListenColorConfig())).start();
			 (new Thread(() -> simulatorService.startListenDefaultMessage())).start();
			 //WriteAllBytes(videoFilePath, videoBytesArray);
			// ivdOvdService.sendCommand("127.0.0.1", "Hal, can you read me?");
		}catch(Exception e) {
			logger.error("Error occured in Post Contruct: {}",e.getMessage());
		}

	}

}

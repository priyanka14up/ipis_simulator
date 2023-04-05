package com.innobit.simulator.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.innobit.simulator.common.Constants;
import com.innobit.simulator.common.Util;

@Service
public class SimulatorService {
	private static final Logger logger = LoggerFactory.getLogger(SimulatorService.class);

	@Value("${simulator-server-port}")
	private static String simulatorServerPort;

	@Value("${simulator-task-start-or-stop}")
	 private String taskStop;

	@Value("${simulator-color-conf-handler-server-port}")
	private static String colorConfServerPortForReceivingMessage;

	@Value("${simulator-default-message-handler-server-port}")
	private static String defaultMessageServerPortForReceivingMessage;

	private boolean acceptingContinue = true;


	private List onlineTrainList = new ArrayList<>();
	private static Object colorConfig = null;
	private static Object defaultMessage = null;
	public static byte[] temp;
	public static int sourceIp3;
	public static int sourceIp4;
	public static int destinationIp3;
	public static int destinationIp4;
	public static int crcMsb;
	public static int crcLsb;
	public static int functionCode;

	public void startListen() {
		Socket socket = null;
		InputStream incoming = null;
		OutputStream outgoing = null;
		ServerSocket serverSocket = null;
		try {
			reconnectingToSocket(socket, incoming, outgoing, serverSocket);
		} catch (Exception e) {
			logger.error("Error occured in startListen: {} ", e.getMessage());
		} finally {
			logger.error("Error occured in startListen finally block");
			reconnectingToSocket(socket, incoming, outgoing, serverSocket);
		}
	}

	public void startListenColorConfig() {
		Socket socket = null;
		InputStream incoming = null;
		OutputStream outgoing = null;
		ServerSocket serverSocket = null;
		try {
			reconnectingToSocketForColorConfiguration(socket, incoming, outgoing, serverSocket);
		} catch (Exception e) {
			logger.error("Error occured in startListen: {} ", e.getMessage());
		} finally {
			logger.error("Error occured in startListen finally block");
			reconnectingToSocketForColorConfiguration(socket, incoming, outgoing, serverSocket);
		}
	}

	public static void startListenDefaultMessage() {
		Socket socket = null;
		InputStream incoming = null;
		OutputStream outgoing = null;
		ServerSocket serverSocket = null;
		try {
			reconnectingToSocketForDefaultMessage(socket, incoming, outgoing, serverSocket);
		} catch (Exception e) {
			logger.error("Error occured in startListen: {} ", e.getMessage());
		} finally {
			logger.error("Error occured in startListen finally block");
			reconnectingToSocketForDefaultMessage(socket, incoming, outgoing, serverSocket);
		}
	}

	public void reconnectingToSocketForColorConfiguration(Socket socket, InputStream incoming, OutputStream outgoing,
			ServerSocket serverSocket) {
		try {
			while (Boolean.TRUE.equals(Boolean.valueOf(taskStop))) {
				logger.info("Listening on port: {} ", Integer.parseInt("5051"));
				serverSocket = new ServerSocket(Integer.parseInt("5050"));
				socket = serverSocket.accept();
				logger.info("incoming call...");
				incoming = socket.getInputStream();
				outgoing = socket.getOutputStream();
				colorConfig = getColorData(incoming);
				outgoing.close();
				incoming.close();
				socket.close();
				serverSocket.close();
			}
		} catch (Exception e) {
			logger.error("Error occured in startListen finally block :{} ", e.getMessage());
		} finally {
			try {
				outgoing.close();
				incoming.close();
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				logger.error("Error occured in startListen finally block :{} ", e.getMessage());
			}
		}
	}
	public static void reconnectingToSocketForDefaultMessage(Socket socket, InputStream incoming, OutputStream outgoing,
			ServerSocket serverSocket) {
		try {
			while (Boolean.TRUE.equals(Boolean.valueOf("true"))) {
				logger.info("Listening on port: {} ", Integer.parseInt("5056"));
				serverSocket = new ServerSocket(Integer.parseInt("5055"));
				socket = serverSocket.accept();
				logger.info("incoming call...");
				incoming = socket.getInputStream();
				outgoing = socket.getOutputStream();
				defaultMessage = getDefaultMessageData(incoming);
				outgoing.close();
				incoming.close();
				socket.close();
				serverSocket.close();
			}
		} catch (Exception e) {
			logger.error("Error occured in startListen finally block :{} ", e.getMessage());
		} finally {
			try {
				outgoing.close();
				incoming.close();
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				logger.error("Error occured in startListen finally block :{} ", e.getMessage());
			}
		}
	}

	public static void sendCommand(String ip, byte[] cmd, int port) {
		try {
			Socket socket = new Socket(ip, port);
			InputStream fromServer = socket.getInputStream();
			OutputStream toServer = socket.getOutputStream();
			socket.setSoTimeout(0);
			toServer.write(cmd);
			fromServer.close();
			toServer.close();
			socket.close();
		} catch (IOException ex) {
			logger.error("Error occured while sending the byte array to ivd-ovd :{}", ex.getMessage());
		}
	}

	public void reconnectingToSocket(Socket socket, InputStream incoming, OutputStream outgoing,
			ServerSocket serverSocket) {
		try {
			while (Boolean.TRUE.equals(Boolean.valueOf(taskStop))) {
				// logger.info("reconnectingToSocket on port: {} ", Integer.parseInt(simulatorServerPort));
				serverSocket = new ServerSocket(Integer.parseInt("5001"));
				socket = serverSocket.accept();
				logger.info("incoming call...");
				incoming = socket.getInputStream();
				outgoing = socket.getOutputStream();
				getData(incoming);
				outgoing.close();
				incoming.close();
				socket.close();
				serverSocket.close();
			}
		} catch (Exception e) {
			logger.error("Error occured in reconnectingToSocket  :{} ", e.getMessage());
		} finally {
			try {
				// outgoing.close();
				incoming.close();
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				logger.error("Error occured in reconnectingToSocket finally block :{} ", e.getMessage());
			}
		}
	}

	private static void getData(InputStream stream2server) {
		List<Byte> lengthBytes = new ArrayList<>();
		List<Byte> tempBytes = new ArrayList<>();
		try {
			byte[] response = new byte[stream2server.available()];
			int totalByteRead = stream2server.read(response);
			logger.info("totalByteRead: {}", totalByteRead);
			temp = new byte[response.length - 6];
			// temp=response[3:response.length-4]
			for (int i = 3; i < response.length - 3; i++) {
				tempBytes.add(response[i]);
			}
			for (int i = 5; i < response.length - 1; i++) {
				lengthBytes.add(response[i]);
			}
			for (int i = 0; i < tempBytes.size(); i++) {
				temp[i] = tempBytes.get(i);
			}
			destinationIp3 = response[5] & 0xFF;
			destinationIp4 = response[6] & 0xFF;
			String destinationIp = "10.10." + destinationIp3 + "." + destinationIp4;
			sourceIp3 = response[7] & 0xFF;
			sourceIp4 = response[8] & 0xFF;
			functionCode = response[10] & 0xFF;
			crcMsb = response[response.length - 3];
			crcLsb = response[response.length - 2];
			int dataPacketStart = response[11];
			int deviceIdentifier = response[2];
			int packLength = response[3] & 0xFF + response[4] & 0xFF;
			int actualPackLength = lengthBytes.size();
			String identifierResult = checkPacketIdentifier(deviceIdentifier);
			if (identifierResult == "pass") {
				String sourceAddressResult = checkSourceAddress(sourceIp4);
				if (sourceAddressResult == "pass") {
					String destinationIpResult = checkDestinationAddress(destinationIp);

					if (destinationIpResult == "pass") {
						String dataPackLength = checkPackLength(packLength, actualPackLength);

						if (dataPackLength == "pass") {
							String invalidDataResult = checkStartOfDataPacket(dataPacketStart);

							if (invalidDataResult == "pass") {
								String invalidFuncCodeResult = checkfunctionCode(functionCode);

								if (invalidFuncCodeResult == "pass") {
									String crcMsbResult = checkCrcMsb(crcMsb, temp);
									if (crcMsbResult == "pass") {
										String crcLsbResult = checkCrcLsb(crcLsb, temp);
										if (crcLsbResult == "pass") {
											logger.info("data recieved Successfully");
										} else {
											getCommunicationResponse(0xE0,destinationIp);
										}
									} else {
										getCommunicationResponse(0xE0,destinationIp);
									}
								} else {
									getCommunicationResponse(0xE3,destinationIp);
								}
							} else {
								getCommunicationResponse(0xE5,destinationIp);
							}
						} else {
							getCommunicationResponse(0xE8,destinationIp);
						}
					} else {
						getCommunicationResponse(0xE1,destinationIp);
					}
				} else {
					getCommunicationResponse(0xE2,destinationIp);
				}
			} else {
				getCommunicationResponse(0xE1,destinationIp);
			}

			logger.info("\ndata complete.");
		} catch (IOException ex) {
			logger.error("Error occured in getData: {}", ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static Object getColorData(InputStream stream2server) {
		List<Byte> tempBytes = new ArrayList<>();
		List<Byte> lengthBytes = new ArrayList<>();
		try {
			logger.info("reading incoming data...");
			byte[] response = new byte[stream2server.available()];
			int totalByteRead = stream2server.read(response);
			logger.info("totalByteRead: {}", totalByteRead);
			temp = new byte[response.length - 6];
			// temp=response[3:response.length-4]
			for (int i = 3; i < response.length - 3; i++) {
				tempBytes.add(response[i]);
			}
			for (int i = 5; i < response.length - 1; i++) {
				lengthBytes.add(response[i]);
			}
			for (int i = Constants.ZERO; i < tempBytes.size(); i++) {
				temp[i] = tempBytes.get(i);
			}
			destinationIp3 = response[5] & 0xFF;
			destinationIp4 = response[6] & 0xFF;
			String destinationIp = "10.10." + destinationIp3 + "." + destinationIp4;
			sourceIp3 = response[7] & 0xFF;
			sourceIp4 = response[8] & 0xFF;
			functionCode = response[10] & 0xFF;
			crcMsb = response[response.length - 3];
			crcLsb = response[response.length - 2];
			int dataPacketStart = response[11];
			int deviceIdentifier = response[2];
			int packLength = response[3] & 0xFF + response[4] & 0xFF;
			int actualPackLength = lengthBytes.size();
			String identifierResult = checkPacketIdentifier(deviceIdentifier);
			if (identifierResult == "pass") {
				String sourceAddressResult = checkSourceAddress(sourceIp4);
				if (sourceAddressResult == "pass") {
					String destinationIpResult = checkDestinationAddress(destinationIp);

					if (destinationIpResult == "pass") {
						String dataPackLength = checkPackLength(packLength, actualPackLength);

						if (dataPackLength == "pass") {
							String invalidDataResult = checkStartOfDataPacket(dataPacketStart);

							if (invalidDataResult == "pass") {
								String invalidFuncCodeResult = checkfunctionCode(functionCode);

								if (invalidFuncCodeResult == "pass") {
									String crcMsbResult = checkCrcMsb(crcMsb, temp);
									if (crcMsbResult == "pass") {
										String crcLsbResult = checkCrcLsb(crcLsb, temp);
										if (crcLsbResult == "pass") {
											logger.info("Successfully recieved");
										} else {
											getCommunicationResponse(0xE0,destinationIp);
										}
									} else {
										getCommunicationResponse(0xE0,destinationIp);
									}
								} else {
									getCommunicationResponse(0xE3,destinationIp);
								}
							} else {
								getCommunicationResponse(0xE5,destinationIp);
							}
						} else {
							getCommunicationResponse(0xE8,destinationIp);
						}
					} else {
						getCommunicationResponse(0xE1,destinationIp);
					}
				} else {
					getCommunicationResponse(0xE2,destinationIp);
				}
			} else {
				getCommunicationResponse(0xE1,destinationIp);
			}

		} catch (IOException ex) {
			logger.error("Error occured in getColorData: {}", ex.getMessage());
		}
		return colorConfig;
	}

	private static Object getDefaultMessageData(InputStream stream2server) {
		List<Byte> messageByte = new ArrayList<>();
		List<Byte> tempBytes = new ArrayList<>();
		List<Byte> lengthBytes = new ArrayList<>();
		try {
			logger.info("reading incoming data...");
			byte[] response = new byte[stream2server.available()];
			int totalByteRead = stream2server.read(response);
			temp = new byte[response.length - 6];
			// temp=response[3:response.length-4]
			for (int i = 3; i < response.length - 3; i++) {
				tempBytes.add(response[i]);
			}
			for (int i = 5; i < response.length - 1; i++) {
				lengthBytes.add(response[i]);
			}
			for (int i = Constants.ZERO; i < tempBytes.size(); i++) {
				temp[i] = tempBytes.get(i);
			}
			destinationIp3 = response[5] & 0xFF;
			destinationIp4 = response[6] & 0xFF;
			String destinationIp = "10.10." + destinationIp3 + "." + destinationIp4;
			sourceIp3 = response[7] & 0xFF;
			sourceIp4 = response[8] & 0xFF;
			functionCode = response[10] & 0xFF;
			crcMsb = response[response.length - 3];
			crcLsb = response[response.length - 2];
			int dataPacketStart = response[11];
			int deviceIdentifier = response[2];
			int packLength = response[3] & 0xFF + response[4] & 0xFF;
			int actualPackLength = lengthBytes.size();
			String identifierResult = checkPacketIdentifier(deviceIdentifier);
			if (identifierResult == "pass") {
				String sourceAddressResult = checkSourceAddress(sourceIp4);
				if (sourceAddressResult == "pass") {
					String destinationIpResult = checkDestinationAddress(destinationIp);

					if (destinationIpResult == "pass") {
						String dataPackLength = checkPackLength(packLength, actualPackLength);

						if (dataPackLength == "pass") {
							String invalidDataResult = checkStartOfDataPacket(dataPacketStart);

							if (invalidDataResult == "pass") {
								String invalidFuncCodeResult = checkfunctionCode(functionCode);

								if (invalidFuncCodeResult == "pass") {
									String crcMsbResult = checkCrcMsb(crcMsb, temp);
									if (crcMsbResult == "pass") {
										String crcLsbResult = checkCrcLsb(crcLsb, temp);
										if (crcLsbResult == "pass") {
											logger.info("Successfully recieved");
										} else {
											getCommunicationResponse(0xE0,destinationIp);
										}
									} else {
										getCommunicationResponse(0xE0,destinationIp);
									}
								} else {
									getCommunicationResponse(0xE3,destinationIp);
								}
							} else {
								getCommunicationResponse(0xE5,destinationIp);
							}
						} else {
							getCommunicationResponse(0xE8,destinationIp);
						}
					} else {
						getCommunicationResponse(0xE1,destinationIp);
					}
				} else {
					getCommunicationResponse(0xE2,destinationIp);
				}
			} else {
				getCommunicationResponse(0xE1,destinationIp);
			}
		} catch (Exception e) {
			logger.error("Error occured in getVideoByte :{}", e.getMessage());
		}

		return defaultMessage;

	}

	public static String checkPacketIdentifier(int identifier) throws UnknownHostException {
		int deviceTypeCode = 0;
		InetAddress ip = InetAddress.getLocalHost();
		String ipstr = ip.toString();
		String ipstrarr[] = ipstr.split("/");
		String[] ipAddArray = ipstrarr[1].split("[, . ']+");
		if (Integer.parseInt(ipAddArray[3]) >= 71 && Integer.parseInt(ipAddArray[3]) <= 100) {
			deviceTypeCode = 6;
		} else if (Integer.parseInt(ipAddArray[3]) >= 40 && Integer.parseInt(ipAddArray[3]) <= 70) {
			deviceTypeCode = 7;
		}
		if (identifier != deviceTypeCode) {
			return "pass";
		} else {
			return "fail";
		}
	}

	public static String checkPackLength(int packLength, int actualPackLength) throws UnknownHostException {
		if (packLength == actualPackLength) {
			return "pass";
		} else {
			return "fail";
		}
	}

	public static String checkSourceAddress(int address) throws UnknownHostException {
		if (address == 253 || address == 254) {
			return "pass";
		} else {
			return "fail";
		}
	}

	public static String checkCrcMsb(int crcMsb, byte[] data) {
		byte crcMsbActual = Util.crc16ccitt(data, "MSB");
		// byte crcMsbActual = 10;
		if (crcMsb == crcMsbActual) {
			return "pass";
		} else {
			return "fail";
		}
	}

	public static String checkCrcLsb(int crcLsb, byte[] data) {
		// byte crcLsbActual = Util.crc16ccitt(data, "LSB");
		byte crcLsbActual = Util.crc16ccitt(data, "LSB");
		if (crcLsb == crcLsbActual) {
			return "pass";
		} else {
			return "fail";
		}
	}

	public static String checkDestinationAddress(String destinationIp) {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			String ipstr = ip.toString();
			String ipstrarr[] = ipstr.split("/");
			String hostname = ip.getHostName();
			if (destinationIp.equals(ipstrarr[1])) {
				return "pass";
			} else {
				return "fail";
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String checkfunctionCode(int functionCode) {
		String functionHexCode = Util.decimalToHex3(functionCode);
		if (functionHexCode.equals("0x80") || functionHexCode.equals("0x81") || functionHexCode.equals("0x82")
				|| functionHexCode.equals("0x83") || functionHexCode.equals("0x84") || functionHexCode.equals("0x85")
				|| functionHexCode.equals("0x86") || functionHexCode.equals("0x87") || functionHexCode.equals("0x88")
				|| functionHexCode.equals("0x89")) {
			return "pass";
		} else {
			return "fail";
		}
	}

	public static String checkStartOfDataPacket(int dataPacket) {
		String functionHexCode = Util.decimalToHex3(functionCode);
		if (dataPacket == 2) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private static int header_start_first = 0xAA;

	private static int header_start_second = 0xCC;

	public static byte[] getCommunicationResponse(int errorCode,String destinationIp) {

		// String[] ipArray = ipAddress.split("[, . ']+");
		String[] ipArray = destinationIp.split("[, . ']+");

		byte[] tempData = new byte[0];

		int packetIdentifier = 0x00;

		// int fixedLength = 7;

		// int varibleLength = actualData.length + 3; // N+3 //88

		int packetLength = 8; // 95

		byte[] length = new byte[2];

		length[0] = (byte) (packetLength & 0Xff);
		length[1] = (byte) ((packetLength >> 8) & 0xff);

		int destAddressMsb = Util.hexToInt(Util.decimalToHex2(ipArray[2]));

		int destAddressLsb = Util.hexToInt(Util.decimalToHex2(ipArray[3]));

		int sourecAddressMsb = Util.getSAMSB();

		int sourecAddressLsb = Util.getSALSB();

		int serialNumber = Util.hexToInt(Util.decimalToHex2(ipArray[3]));

		int errorType = errorCode;

		int eot = 0x04; // End of transmission

		// Calculate CRC16

		byte crcMsb = Util.crc16ccitt(tempData, "MSB");

		byte crcLsb = Util.crc16ccitt(tempData, "LSB");

		System.out.println("sourecAddressLsb before preparing packet data" + sourecAddressLsb);

		List<Byte> res = new ArrayList<Byte>();
		res.add((byte) header_start_first);
		res.add((byte) header_start_second);
		res.add((byte) packetIdentifier);
		res.add(length[0]);
		res.add(length[1]);
		res.add((byte) destAddressMsb);
		res.add((byte) destAddressLsb);
		res.add((byte) sourecAddressMsb);
		res.add((byte) sourecAddressLsb);
		res.add((byte) serialNumber);
		res.add((byte) errorType);
		res.add(crcMsb);
		res.add(crcLsb);
		res.add((byte) eot);
		byte[] packetData = new byte[res.size()];

		for (int i = 0; i < res.size(); i++) {
			packetData[i] = res.get(i);

			System.out.println("packetData" + (i + 1) + "--------->" + res.get(i));

		}
		System.out.println();
		System.out.println();
		System.out.println("packet in hexa");
		for (int i = 0; i < res.size(); i++) {
			String s = Util.decimalToHex1(res.get(i));

			System.out.println("byte no-" + (i + 1) + "----" + s);

		}
		sendCommand("10.10.3.47", packetData, 5050);

		return packetData;

	}

}

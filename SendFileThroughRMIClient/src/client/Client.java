package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.StringTokenizer;

import common.FileProcessor;

public class Client {
	private FileProcessor fileProcessor;
	private String CLIENT_DIR = "";

	public static void main(String[] args) {
		try {
			Client client = new Client();
			Registry registry = LocateRegistry.getRegistry(12345);
			client.fileProcessor = (FileProcessor) registry.lookup("FileHandler");
			System.out.println("Welcome");

			Scanner scanner = new Scanner(System.in);
			while (true) {
				try {
					String request = scanner.nextLine();
					StringTokenizer tokens = new StringTokenizer(request);

					String command = tokens.nextToken();

					if (command.equalsIgnoreCase("SET_SERVER_DIR")) {
						String dir = tokens.nextToken();
						if (client.fileProcessor.setServerDir(dir)) {
							System.out.println("Set server directory: ok");
						} else {
							System.out.println("Ser server direcotry: fail");
						}
					} else if (command.equalsIgnoreCase("SET_CLIENT_DIR")) {
						String dir = tokens.nextToken();
						File file = new File(dir);
						if (file.isDirectory()) {
							client.CLIENT_DIR = dir;
							System.out.println("Set client directory: ok");
						} else {
							System.out.println("Set client directory: fail");
						}
					} else if (command.equalsIgnoreCase("GET")) {
						String sourceFile = tokens.nextToken();
						String destFile = tokens.nextToken();
						client.downloadFile(sourceFile, destFile);
					} else if (command.equalsIgnoreCase("SEND")) {
						String sourceFile = tokens.nextToken();
						String destFile = tokens.nextToken();
						client.sendFile(sourceFile, destFile);
					} else if (command.equalsIgnoreCase("QUIT")) {
						System.out.println("Bye!");
						break;
					} else {
						System.out.println("Command invalid");
					}
				} catch (Exception e) {
					System.out.println("Request invalid");
				}
			}
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void sendFile(String source, String dest) {
		try {
			File file = new File(CLIENT_DIR + source);
			if (!file.exists()) {
				System.out.println("File not found!");
				return;
			}

			fileProcessor.setFileName(dest);
			fileProcessor.initStreaming(false);

			FileInputStream fileInputStream = new FileInputStream(file);
			int byteReaded = 0;
			byte[] data = new byte[1204];
			while ((byteReaded = fileInputStream.read(data)) != -1) {
				fileProcessor.sendFile(data, 0, byteReaded);
			}
			System.out.println("Send successful");
			fileInputStream.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void downloadFile(String source, String dest) {
		try {
			fileProcessor.setFileName(source);
			if (!fileProcessor.avaliable()) {
				System.out.println("File not found!");
				return;
			}

			fileProcessor.initStreaming(true);
			int fileSize = fileProcessor.getFileSize();
			// Start download
			FileOutputStream fileOutputStream = new FileOutputStream(CLIENT_DIR + dest);
			int byteHaveToGet = (int) fileSize > 1024 ? 1024 : fileSize;
			while (fileSize != 0) {
				byte[] fileData = fileProcessor.getFile(byteHaveToGet);
				fileOutputStream.write(fileData, 0, fileData.length);
				fileSize -= fileData.length;
				byteHaveToGet = (int) fileSize > 1024 ? 1024 : fileSize;
			}
			System.out.println("Get successful");
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

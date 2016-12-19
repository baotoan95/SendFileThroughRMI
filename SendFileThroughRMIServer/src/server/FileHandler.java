package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.FileProcessor;

public class FileHandler extends UnicastRemoteObject implements FileProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2150067423945685077L;

	private String SERVER_DIR = "";
	private String fileName = "";
	private FileOutputStream fileOutputStream;
	private FileInputStream fileInputStream;

	protected FileHandler() throws RemoteException {
		super();
	}

	@Override
	public void initStreaming(boolean download) {
		try {
			if (download) {
				fileInputStream = new FileInputStream(SERVER_DIR + fileName);
			} else {
				fileOutputStream = new FileOutputStream(SERVER_DIR + fileName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] getFile(int length) throws RemoteException {
		byte[] data = new byte[length];
		try {
			fileInputStream.read(data, 0, length);
			return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean sendFile(byte[] data, int offset, int length) throws RemoteException {
		try {
			fileOutputStream.write(data, 0, length);
			fileOutputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getFileSize() {
		File file = new File(SERVER_DIR + fileName);
		if (file.exists()) {
			return (int) file.length();
		}
		return -1;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean setServerDir(String dir) {
		File file = new File(dir);
		if (file.isDirectory()) {
			this.SERVER_DIR = dir;
			return true;
		}
		return false;
	}

	@Override
	public boolean avaliable() throws RemoteException {
		File file = new File(SERVER_DIR + fileName);
		return file.exists();
	}

}

package torrent.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import torrent.download.peer.Peer;

public class ByteInputStream extends DataInputStream {

	/**
	 * The peer associated with this inputStream
	 */
	private Peer peer;
	/**
	 * The speed in bytes that this inputStream is being read
	 */
	private int speed;
	/**
	 * A buffer to store data in before we make it be processed
	 */
	private Stream buffer;
	/**
	 * The last time a buffer was created
	 */
	private long lastBufferCreate;

	public ByteInputStream(Peer peer, InputStream in) {
		super(in);
		this.peer = peer;
		speed = 0;
	}

	@Override
	public int read() throws IOException {
		int b = super.read();
		if (b != -1) {
			++speed;
			peer.updateLastActivity();
		}
		return b;
	}

	public String readString(int length) throws IOException {
		String s = "";
		while (length > 0) {
			s += (char) (read() & 0xFF);
			length--;
		}
		return s;
	}

	public String readString() throws IOException {
		return readString(read());
	}

	public byte[] readByteArray(int length) throws IOException {
		byte[] array = new byte[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = (byte) (read() & 0xFF);
		}
		return array;
	}

	public int getSpeed() {
		return speed;
	}

	public void reset(int downloadRate) {
		speed -= downloadRate;
	}

	public Stream getBuffer() {
		return buffer;
	}

	/**
	 * Resets the temporary stream which is used for peeking
	 */
	public void resetBuffer() {
		buffer = null;
	}

	/**
	 * Creates an empty buffer to hold the data
	 */
	public void initialiseBuffer() {
		buffer = new Stream(0);
		lastBufferCreate = System.currentTimeMillis();
	}

	/**
	 * The time in milliseconds that this buffer has existed
	 * 
	 * @return
	 */
	public int getBufferLifetime() {
		return (int) (System.currentTimeMillis() - lastBufferCreate);
	}

}

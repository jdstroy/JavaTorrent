package torrent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.johnnei.utils.ThreadUtils;
import org.johnnei.utils.config.Config;

import torrent.download.peer.Peer;
import torrent.network.protocol.TcpSocket;

public class PeerConnector extends Thread {

	private ServerSocket serverSocket;

	public PeerConnector() throws IOException {
		super("PeerConnector");
		serverSocket = new ServerSocket(Config.getConfig().getInt("download-port"));
	}

	public void run() {
		while (true) {
			try {
				Peer peer = new Peer();
				Socket peerSocket = (Socket) serverSocket.accept();
				peer.setSocket(new TcpSocket(peerSocket));
				long handshakeStart = System.currentTimeMillis();
				while (!peer.canReadMessage() && (System.currentTimeMillis() - handshakeStart) < 5000) {
					ThreadUtils.sleep(10);
				}
				if (peer.canReadMessage()) {
					peer.processHandshake();
					if (peer.getPassedHandshake()) {
						peer.sendHandshake();
						peer.getTorrent().addPeer(peer);
					} else {
						peer.close();
					}
				} else {
					peer.close();
				}
			} catch (IOException e) {
			}
		}
	}

}

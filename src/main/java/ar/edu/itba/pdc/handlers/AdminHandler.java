package ar.edu.itba.pdc.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.parser.AdminParser;
import ar.edu.itba.pdc.proxy.BufferType;
import ar.edu.itba.pdc.proxy.ChannelBuffers;

public class AdminHandler implements TCPHandler {

	private Map<SocketChannel, ChannelBuffers> config;
	private Selector selector;
	private AdminParser parser;

	public AdminHandler(Selector selector) {
		this.selector = selector;
		config = new HashMap<SocketChannel, ChannelBuffers>();
		parser = new AdminParser();
	}

	public void accept(SocketChannel channel) throws IOException {
		config.put(channel, new ChannelBuffers());
	}

	public SocketChannel read(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel) key.channel();
		ChannelBuffers channelBuffers = config.get(s);
		s.read(channelBuffers.getBuffer(BufferType.read));

		try {
			if (!parser.parseCommand(channelBuffers.getBuffer(BufferType.read)))
				System.out.println("Mala sintaxis");
		} catch (JSONException e) {
			System.out.println("Bad syntax");
		}
		// parseCommand(channelBuffers.getReadBuffer());
		// channelBuffers.autoSynchronizeBuffers();
		// String strCommand = new
		// String(channelBuffers.getReadBuffer().array());

		channelBuffers.getBuffer(BufferType.read).clear();
		updateSelectionKeys(s);
		return null;
	}

	public void write(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel) key.channel();
		ByteBuffer wrBuffer = config.get(s).getBuffer(BufferType.write);
		wrBuffer.flip();
		s.write(wrBuffer);
		updateSelectionKeys(s);
		wrBuffer.compact();
	}

	private void updateSelectionKeys(SocketChannel s)
			throws ClosedChannelException {
		ChannelBuffers buffers = config.get(s);
		if (buffers.getBuffer(BufferType.write).capacity() != buffers.getBuffer(BufferType.write)
				.remaining()) {
			s.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			s.register(selector, SelectionKey.OP_READ);
		}
	}
}

package src;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

import src.sign.signlink;

public final class OnDemandFetcher extends OnDemandFetcherParent implements Runnable {

	/**
	 * Grabs the checksum of a file from the cache.
	 * 
	 * @param type
	 *            The type of file (0 = model, 1 = anim, 2 = midi, 3 = map).
	 * @param id
	 *            The id of the file.
	 * @return
	 */
	public int getChecksum(int type, int id) {
		int crc = 0;
		byte[] data = clientInstance.cacheIndices[type + 1].get(id);
		if (data != null) {
			int length = data.length - 2;
			crc32.reset();
			crc32.update(data, 0, length);
			crc = (int) crc32.getValue();
		}
		return crc;
	}

	/**
	 * Grabs the version of a file from the cache.
	 * 
	 * @param type
	 *            The type of file (0 = model, 1 = anim, 2 = midi, 3 = map).
	 * @param id
	 *            The id of the file.
	 * @return
	 */
	public int getVersion(int type, int id) {
		int version = 1;
		byte[] data = clientInstance.cacheIndices[type + 1].get(id);
		if (data != null) {
			int length = data.length - 2;
			version = ((data[length] & 0xff) << 8) + (data[length + 1] & 0xff);
		}
		return version;
	}

	/**
	 * Writes the checksum list for the specified archive type and length.
	 * 
	 * @param type
	 *            The type of archive (0 = model, 1 = anim, 2 = midi, 3 = map).
	 * @param length
	 *            The number of files in the archive.
	 */
	public void writeChecksumList(int type) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(signlink.findcachedir() + type + "_crc.dat"));
			for (int index = 0; index < clientInstance.cacheIndices[type + 1].getFileCount(); index++) {
				out.writeInt(getChecksum(type, index));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the version list for the specified archive type and length.
	 * 
	 * @param type
	 *            The type of archive (0 = model, 1 = anim, 2 = midi, 3 = map).
	 * @param length
	 *            The number of files in the archive.
	 */
	public void writeVersionList(int type) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(signlink.findcachedir() + type + "_version.dat"));
			for (int index = 0; index < clientInstance.cacheIndices[type + 1].getFileCount(); index++) {
				out.writeShort(getVersion(type, index));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compare Cache file buffer checksum with most recent file checksum;
	 * 
	 * @param fileVersion
	 *            : Version of updated file;
	 * @param fileCrc
	 *            : Checksum of updated file;
	 * @param buf
	 *            : Buffer of current cache file;
	 * @return: true if checksum of cache file and most updated files match;
	 */
	private boolean crcMatches(int fileVersion, int fileCrc, byte buf[]) {
		if (buf == null || buf.length < 2) {
			return false;
		}

		int len = buf.length - 2;
		int version = ((buf[len] & 0xff) << 8) + (buf[len + 1] & 0xff);
		crc32.reset();
		crc32.update(buf, 0, len);
		int crc = (int) crc32.getValue();
		return crc == fileCrc;// version == fileVersion &&
	}

	/**
	 * Read received data from Update Server First read 6 bytes. Put those 6
	 * bytes in a byte array {@code ioBuffer}; Decode array into file type, file
	 * ID, size of the file and chunk of the file.
	 */
	private void readData() {
		try {
			int available = inputStream.available();
			if (expectedSize == 0 && available >= 7) {
				waiting = true;// O update server manda informação do arquivo em
				// 6 bytes
				for (int i = 0; i < 7; i += inputStream.read(ioBuffer, i, 7 - i))
					;// Read all 6 bytes into {@code ioBuffer}

				int dataType = ioBuffer[0] & 0xff;// First Byte is Data Type - 1
				// Byte = data type
				int fileID = ((ioBuffer[1] & 0xff) << 8) + (ioBuffer[2] & 0xff);
				int fileLength = ((ioBuffer[3] & 0xff) << 16) + ((ioBuffer[4] & 0xff) << 8) + (ioBuffer[5] & 0xff);
				int chunk = ioBuffer[6] & 0xff;// Sixth byte represent location
				// of this data in the cache.
				current = null;
				// 1 - Você precisa ir ao seu update server e mudar o que ele
				// envia ao cliente, de short para medium Dica:
				// (OnDemandResponseEncoder)
				// 2 - Você precisa mudar o read int para ler 7 bytes invés de 6
				// 3 - Você precisa arrumar o ioBuffer para 3 bytes serem usados
				// para o fileLength, sendo assim ioBuffer[6] seria o chunk.
				System.out.println("Receiving data " + fileID + " Type: " + dataType + " Length: " + fileLength + " Chunk: " + chunk);

				// Search {@code onDemandRequest} request from the request queue
				// which match with current dataType and file ID;
				for (OnDemandRequest onDemandRequest = (OnDemandRequest) requested.getFront(); onDemandRequest != null; onDemandRequest = (OnDemandRequest) requested.getNext()) {
					if (onDemandRequest.dataType == dataType && onDemandRequest.id == fileID) {
						current = onDemandRequest;
					}
					if (current != null) {
						onDemandRequest.loopCycle = 0;
					}
				}
				// If request that match with received data is found
				if (current != null) {
					loopCycle = 0;
					if (fileLength == 0) {// If file length is 0 then there's an
						// error and put data to incomplete
						// queue.
						signlink.reporterror("Rej: " + dataType + "," + fileID);
						current.buffer = null;
						if (current.incomplete) {
							synchronized (incompleteList) {
								incompleteList.insertBack(current);
							}
						} else {
							current.unlink();
						}
						current = null;
					} else {
						// If current request buffer is null and chunk is 0
						// means that file data is not started yet, so create
						// the buffer.
						if (current.buffer == null && chunk == 0) {
							current.buffer = new byte[fileLength];
						}
						// If the buffer is not null means that it was already
						// created but if chunk is not 0 then it's missing
						// initial data of the file.
						if (current.buffer == null && chunk != 0) {
							throw new IOException("missing start of file");
						}
					}
				}
				// Each chunk is 500L of size total size completed is current
				// chunk * memory block size(500)
				completedSize = chunk * 500;
				expectedSize = 500;
				if (expectedSize > fileLength - chunk * 500) {
					expectedSize = fileLength - chunk * 500;
				}
			}
			// If received data size is bigger than expected data size which is
			// usually 500 unless there's no more than 500 bytes of data left
			if (expectedSize > 0 && available >= expectedSize) {
				waiting = true;
				byte buf[] = ioBuffer;
				int off = 0;
				if (current != null) {
					buf = current.buffer;
					off = completedSize;
				}
				// Read file bytes into array buff.
				for (int i = 0; i < expectedSize; i += inputStream.read(buf, i + off, expectedSize - i))
					;

				// if (current.dataType == 3) //TODO
				// System.out.println("Loading Map: " + incompleteList.getSize()
				// + "/" + queue.getSize());

				if (expectedSize + completedSize >= buf.length && current != null) {
					// Write file bytes into cache based on its data type;
					if (clientInstance.cacheIndices[0] != null) {
						System.out.println("Packing downloaded data: " + current.id + " Type: " + current.dataType);
						clientInstance.cacheIndices[current.dataType + 1].method234(buf.length, buf, (int) current.id);
					}
					if (!current.incomplete && current.dataType == 3) {
						current.incomplete = true;
						current.dataType = 93;
					}
					if (current.incomplete) {
						synchronized (incompleteList) {
							incompleteList.insertBack(current);
						}
					} else {
						current.unlink();
					}
				}
				expectedSize = 0;
			}
		} catch (IOException ioexception) {
			try {
				socket.close();
			} catch (Exception exception) {
			}
			socket = null;
			inputStream = null;
			outputStream = null;
			expectedSize = 0;
		}
	}

	/**
	 * Start on demand fetcher data.
	 * 
	 * @param archive
	 *            : Cache Archive that contains update list.
	 * @param client
	 *            : Client instance.
	 */
	public void start(StreamLoader archive, Client client) {
		if (Configuration.JAGCACHED_ENABLED) {
			/*String versionNames[] = { "model_version", "anim_version", "midi_version", "map_version" };
			for (int i = 0; i < versionNames.length; i++) {
				byte buf[] = archive.getDataForName(versionNames[i]);
				int len = buf.length / 2;
				Stream buffer = new Stream(buf);
				files[i] = new int[len];
				fileStatus[i] = new byte[len];
				for (int l = 0; l < len; l++) {
					files[i][l] = buffer.readUnsignedWord();
				}
			}*/
			String crc[] = { "model_crc", "anim_crc", "midi_crc", "map_crc" };
			for (int type = 0; type < 4; type++) {
				byte data[] = archive.getDataForName(crc[type]);
				int total = data.length / 4;
				Stream crcStream = new Stream(data); 
				crcs[type] = new int[total];
				fileStatus[type] = new byte[total];
				for (int id = 0; id < total; id++)
					crcs[type][id] = crcStream.readInt();
			}
		}
		byte[] data = archive.getDataForName("map_index");
		ByteBuffer buffer = new ByteBuffer(data);
		int count = buffer.readUnsignedWord();
		mapIndices1 = new int[count];
		mapIndices2 = new int[count];
		mapIndices3 = new int[count];
		for (int i2 = 0; i2 < count; i2++) {
			mapIndices1[i2] = buffer.readUnsignedWord();
			mapIndices2[i2] = buffer.readUnsignedWord();
			mapIndices3[i2] = buffer.readUnsignedWord();
		}
		System.out.println("Loaded Maps: " + count + "");

		if(Configuration.JAGCACHED_ENABLED) {
			byte abyte2[] = archive.getDataForName("model_index");
			int j1 = crcs[0].length;
			modelIndices = new byte[j1];
			for(int k1 = 0; k1 < j1; k1++)
				if(k1 < abyte2.length)
					modelIndices[k1] = abyte2[k1];
				else
					modelIndices[k1] = 0;

			data = archive.getDataForName("anim_index");
			buffer = new ByteBuffer(data);
			count = data.length / 2;
			animationIndices = new int[count];
			for (int i = 0; i < count; i++) {
				animationIndices[i] = buffer.readUnsignedWord();
			}
		}
		data = archive.getDataForName("midi_index");
		buffer = new ByteBuffer(data);
		count = data.length;
		midiIndices = new int[count];
		for (int k2 = 0; k2 < count; k2++)
			midiIndices[k2] = buffer.readUnsignedByte();
		clientInstance = client;
		running = true;
		clientInstance.startRunnable(this, 2);
	}

	/**
	 * Get total of data left to be downloaded.
	 * 
	 * @return: Total of data to be downloaded.
	 */
	public int getRemaining() {
		synchronized (queue) {
			return queue.getSize();
		}
	}

	/**
	 * Stop On Demand Fetcher service
	 */
	public void dispose() {
		running = false;
	}

	/**
	 * Load priority maps*
	 * 
	 * @param isMember
	 *            : 'Is Member' flag
	 */
	public void preloadFetchMapFiles(boolean isMember) {
		int j = mapIndices1.length;
		for (int k = 0; k < j; k++) {
			if (isMember || mapIndices4[k] != 0) {
				loadPriorityFile((byte) 2, 3, mapIndices3[k]);
				loadPriorityFile((byte) 2, 3, mapIndices2[k]);
			}
		}
	}

	/**
	 * Get total of files in a cache index.
	 * 
	 * @param cacheIndex
	 *            : Index of the cache.
	 * @return: Amount of files that contains in the cache at index
	 *          {@code cacheIndex}
	 */
	public int getFileCount(int cacheIndex) {
		return crcs[cacheIndex].length;
	}

	/**
	 * Request a data to update server.
	 * 
	 * @param onDemandRequest
	 *            : Request to be sent to update server.
	 */
	private void closeRequest(OnDemandRequest onDemandRequest) {
		try {
			if (socket == null) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - openSocketTime < 4000L) {
					return;
				}
				openSocketTime = currentTime;
				socket = clientInstance.openSocket(43596 + Client.portOff);
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(15);
				for (int j = 0; j < 8; j++) {
					inputStream.read();
				}
				loopCycle = 0;
			}
			/** Write request data into buffer {@code ioBuffer}; **/
			ioBuffer[0] = (byte) onDemandRequest.dataType;
			ioBuffer[1] = (byte) (onDemandRequest.id >> 8);
			ioBuffer[2] = (byte) onDemandRequest.id;
			/** Write file priority */
			if (onDemandRequest.incomplete) {
				ioBuffer[3] = 2;
			} else if (!clientInstance.loggedIn) {
				ioBuffer[3] = 1;
			} else {
				ioBuffer[3] = 0;
			}
			System.out.println("Sending data request: " + onDemandRequest.id + " Type: " + onDemandRequest.dataType);
			/** Send buffer data to update server **/
			outputStream.write(ioBuffer, 0, 4);
			writeLoopCycle = 0;
			anInt1349 = -10000;
			return;
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
		try {
			/** Close connection **/
			socket.close();
		} catch (Exception exception) {
		}
		socket = null;
		inputStream = null;
		outputStream = null;
		expectedSize = 0;
		anInt1349++;
	}

	/**
	 * Get total of animations at cache index.
	 * 
	 * @return: Total of animations.
	 */
	public int getAnimCount() {
		return Configuration.JAGCACHED_ENABLED ? animationIndices.length :
			29192;
	}

	/**
	 * Start a file data request, if it wasn't requested already.
	 * 
	 * @param dataType
	 *            : Data type of the file.
	 * @param fileID
	 *            : ID of the file.
	 */
	public void requestFileData(int dataType, int fileID) {
		if (Configuration.JAGCACHED_ENABLED) {
			if (dataType < 0 || dataType > crcs.length || fileID < 0 || fileID > crcs[dataType].length) {
				return;
			}
			//if (files[dataType][fileID] == 0) {
			// return;
			//	}
		} else {
			if (dataType < 0 || fileID < 0) {
				return;
			}
		}
		synchronized (queue) {
			/** Search for current request in queue and stop if it exists **/
			for (OnDemandRequest onDemandRequest = (OnDemandRequest) queue.getFront(); onDemandRequest != null; onDemandRequest = (OnDemandRequest) queue.getNext()) {
				if (onDemandRequest.dataType == dataType && onDemandRequest.id == fileID) {
					return;
				}
			}
			/** Create a new request and insert it into a queue **/
			OnDemandRequest onDemandRequest = new OnDemandRequest();
			onDemandRequest.dataType = dataType;
			onDemandRequest.id = fileID;
			onDemandRequest.incomplete = true;
			synchronized (loadRequestList) {
				loadRequestList.insertBack(onDemandRequest);
			}
			queue.insertBack(onDemandRequest);
		}
	}

	/**
	 * Load data with priority
	 * 
	 * @param priority
	 *            : Priority of the request.
	 * @param dataType
	 *            : Type of the data to be loaded.
	 * @param fileID
	 *            : ID of the data.
	 */
	public void loadPriorityFile(byte priority, int dataType, int fileID) {
		if (clientInstance.cacheIndices[0] == null) {
			return;
		}
		if (Configuration.JAGCACHED_ENABLED) {
			//	if (files[dataType][fileID] == 0) {
			// return;
			//	}
			byte fileBuffer[] = clientInstance.cacheIndices[dataType + 1].get(fileID);
			if (crcMatches(crcs[dataType][fileID], crcs[dataType][fileID], fileBuffer)) {
				return;
			}
		}
		fileStatus[dataType][fileID] = priority;
		if (priority > anInt1332) {
			anInt1332 = priority;
		}
		totalFiles++;
	}

	public void method560(int file, int cache) {
		if (clientInstance.cacheIndices[0] == null) {
			return;
		}
		if (Configuration.JAGCACHED_ENABLED) {
			//	if (files[cache][file] == 0) {
			// return;
			//}
			if (fileStatus[cache][file] == 0) {
				return;
			}
		}
		if (anInt1332 == 0) {
			return;
		}
		OnDemandRequest onDemandRequest = new OnDemandRequest();
		onDemandRequest.dataType = cache;
		onDemandRequest.id = file;
		onDemandRequest.incomplete = false;
		synchronized (extraFilesList) {
			extraFilesList.insertBack(onDemandRequest);
		}
	}

	/**
	 * Get model data which contains its priority.
	 * 
	 * @param id
	 *            : Id of the model
	 * @return: Return model priority.
	 */
	public int getModelIndex(int id) {
		return modelIndices[id] & 0xff;
	}

	/**
	 * Service loop.
	 */
	@Override
	public void run() {
		try {
			while (running) {
				onDemandCycle++;
				int sleepTime = 20;
				if (anInt1332 == 0 && clientInstance.cacheIndices[0] != null) {
					sleepTime = 50;
				}
				try {
					Thread.sleep(sleepTime);
				} catch (Exception exception) {
				}
				waiting = true;
				for (int i = 0; i < 100; i++) {
					if (Configuration.JAGCACHED_ENABLED) {
						if (!waiting) {
							break;
						}
					}
					waiting = false;
					checkReceived();
					if (Configuration.JAGCACHED_ENABLED)
						handleFailed();
					if (incomplete == 0 && i >= 5) {
						break;
					}
					//getExtras();
					if (inputStream != null) {
						readData();
					}
				}
				boolean incomplete = false;
				for (OnDemandRequest onDemandRequest = (OnDemandRequest) requested.getFront(); onDemandRequest != null; onDemandRequest = (OnDemandRequest) requested.getNext()) {
					if (onDemandRequest.incomplete) {
						incomplete = true;
						onDemandRequest.loopCycle++;
						if (onDemandRequest.loopCycle > 30) {
							onDemandRequest.loopCycle = 0;
							closeRequest(onDemandRequest);
						}
					}
				}
				if (!incomplete) {
					for (OnDemandRequest onDemandRequest = (OnDemandRequest) requested.getFront(); onDemandRequest != null; onDemandRequest = (OnDemandRequest) requested.getNext()) {
						incomplete = true;
						onDemandRequest.loopCycle++;
						if (onDemandRequest.loopCycle > 30) {
							onDemandRequest.loopCycle = 0;
							closeRequest(onDemandRequest);
						}
					}
				}
				if (incomplete) {
					loopCycle++;
					if (loopCycle > 750) {
						try {
							socket.close();
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						socket = null;
						inputStream = null;
						outputStream = null;
						expectedSize = 0;
					}
				} else {
					loopCycle = 0;
					statusString = "";
				}
				if (clientInstance.loggedIn && socket != null && outputStream != null && (anInt1332 > 0 || clientInstance.cacheIndices[0] == null)) {
					writeLoopCycle++;
					if (writeLoopCycle > 500) {
						writeLoopCycle = 0;
						ioBuffer[0] = 0;
						ioBuffer[1] = 0;
						ioBuffer[2] = 0;
						ioBuffer[3] = 10;
						try {
							outputStream.write(ioBuffer, 0, 4);
						} catch (IOException ioexception) {
							loopCycle = 5000;
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			signlink.reporterror("od_ex " + exception.getMessage());
		}
	}

	public OnDemandRequest getNextNode() {
		OnDemandRequest onDemandRequest;
		synchronized (incompleteList) {
			onDemandRequest = (OnDemandRequest) incompleteList.popFront();
		}
		if (onDemandRequest == null) {
			return null;
		}
		synchronized (queue) {
			onDemandRequest.unlinkSub();
		}
		if (onDemandRequest.buffer == null) {
			return onDemandRequest;
		}
		int off = 0;
		try {
			GZIPInputStream gzipinputstream = new GZIPInputStream(new ByteArrayInputStream(onDemandRequest.buffer));
			do {
				if (off == gzipInputBuffer.length) {
					throw new RuntimeException("buffer overflow!");
				}
				int len = gzipinputstream.read(gzipInputBuffer, off, gzipInputBuffer.length - off);
				if (len == -1) {
					break;
				}
				off += len;
			} while (true);
		} catch (IOException exception) {
			System.out.println("Failed to unzip data [" + onDemandRequest.id + "] type = " + onDemandRequest.dataType);
			exception.printStackTrace();
			throw new RuntimeException("error unzipping");
		}
		onDemandRequest.buffer = new byte[off];
		System.arraycopy(gzipInputBuffer, 0, onDemandRequest.buffer, 0, off);
		return onDemandRequest;
	}

	public int getModelCount() {
		return 65331;
	}

	public int getMapCount(int arg0, int arg1, int arg2) {
		int id = (arg2 << 8) + arg1;
		for (int i = 0; i < mapIndices1.length; i++) {
			if (mapIndices1[i] == id) {
				if (arg0 == 0) {
					return mapIndices2[i];
				} else {
					return mapIndices3[i];
				}
			}
		}
		return -1;
	}

	@Override
	public void get(int id) {
		requestFileData(0, id);
	}

	public boolean method564(int id) {
		for (int i = 0; i < mapIndices1.length; i++) {
			if (mapIndices3[i] == id) {
				return true;
			}
		}
		return false;
	}

	private void handleFailed() {
		incomplete = 0;
		complete = 0;
		for (OnDemandRequest onDemandRequest = (OnDemandRequest) requested.getFront(); onDemandRequest != null; onDemandRequest = (OnDemandRequest) requested.getNext()) {
			if (onDemandRequest.incomplete) {
				// System.out.println("Error: model is incomplete or missing  [ type = "+onDemandRequest.dataType+"]  "
				// +
				// "[id = "+onDemandRequest.id+"]");
				incomplete++;
			} else {
				complete++;
			}
		}
		while (incomplete < 10) {
			OnDemandRequest onDemandRequest = (OnDemandRequest) next.popFront();
			if (onDemandRequest == null) {
				break;
			}
			if (fileStatus[onDemandRequest.dataType][onDemandRequest.id] != 0) {
				filesLoaded++;
			}
			fileStatus[onDemandRequest.dataType][onDemandRequest.id] = 0;
			// System.out.println("Error: file is missing  [ type = "+onDemandRequest.dataType+"]  [id = "+onDemandRequest.id+"]");
			requested.insertBack(onDemandRequest);
			incomplete++;
			closeRequest(onDemandRequest);
			waiting = true;
		}
	}

	public void method566() {
		synchronized (extraFilesList) {
			extraFilesList.clear();
		}
	}

	private void checkReceived() {
		OnDemandRequest onDemandRequest;
		synchronized (loadRequestList) {
			onDemandRequest = (OnDemandRequest) loadRequestList.popFront();
		}
		while (onDemandRequest != null) {
			waiting = true;
			byte fileBuffer[] = null;
			if (clientInstance.cacheIndices[0] != null) {
				fileBuffer = clientInstance.cacheIndices[onDemandRequest.dataType + 1].get(onDemandRequest.id);
			}
			if (Configuration.JAGCACHED_ENABLED) {
				if (!crcMatches(crcs[onDemandRequest.dataType][onDemandRequest.id], crcs[onDemandRequest.dataType][onDemandRequest.id], fileBuffer)) {
					fileBuffer = null;
				}
			}
			synchronized (loadRequestList) {
				if (fileBuffer == null) {
					next.insertBack(onDemandRequest);
				} else {
					onDemandRequest.buffer = fileBuffer;
					synchronized (incompleteList) {
						incompleteList.insertBack(onDemandRequest);
					}
				}
				onDemandRequest = (OnDemandRequest) loadRequestList.popFront();
			}
		}
	}

	/**
	 * Get extra data that does not contain in cache.
	 */
	private void getExtras() {
		while (incomplete == 0 && complete < 10) {
			if (anInt1332 == 0) {
				break;
			}
			OnDemandRequest onDemandRequest;
			synchronized (extraFilesList) {
				onDemandRequest = (OnDemandRequest) extraFilesList.popFront();
			}
			while (onDemandRequest != null) {
				if (fileStatus[onDemandRequest.dataType][onDemandRequest.id] != 0) {
					fileStatus[onDemandRequest.dataType][onDemandRequest.id] = 0;
					requested.insertBack(onDemandRequest);
					closeRequest(onDemandRequest);
					waiting = true;
					if (filesLoaded < totalFiles) {
						filesLoaded++;
					}
					statusString = "Loading extra files - " + filesLoaded * 100 / totalFiles + "%";
					complete++;
					if (complete == 10) {
						return;
					}
				}
				synchronized (extraFilesList) {
					onDemandRequest = (OnDemandRequest) extraFilesList.popFront();
				}
			}
			for (int i = 0; i < 4; i++) {
				byte buf[] = fileStatus[i];
				int len = buf.length;
				for (int j = 0; j < len; j++) {
					if (buf[j] == anInt1332) {
						buf[j] = 0;
						OnDemandRequest extras = new OnDemandRequest();
						extras.dataType = i;
						extras.id = j;
						extras.incomplete = false;
						requested.insertBack(extras);
						closeRequest(extras);
						waiting = true;
						if (filesLoaded < totalFiles) {
							filesLoaded++;
						}
						statusString = "Loading extra files - " + filesLoaded * 100 / totalFiles + "%";
						complete++;
						if (complete == 10) {
							return;
						}
					}
				}
			}
			anInt1332--;
		}
	}

	public boolean getMidiIndex(int id) {
		return midiIndices[id] == 1;
	}

	public OnDemandFetcher() {
		requested = new Deque();
		statusString = "";
		crc32 = new CRC32();
		ioBuffer = new byte[500];
		fileStatus = new byte[4][];
		extraFilesList = new Deque();
		running = true;
		waiting = false;
		incompleteList = new Deque();
		gzipInputBuffer = new byte[3000000]; // 300KB
		queue = new Queue();
		//files = new int[4][];
		crcs = new int[4][];
		next = new Deque();
		loadRequestList = new Deque();
	}

	private int totalFiles;
	private int anInt1332;
	public String statusString;
	private int writeLoopCycle;
	private long openSocketTime;
	private int[] mapIndices3;
	private final CRC32 crc32;
	private final byte[] ioBuffer;
	public int onDemandCycle;
	private final byte[][] fileStatus;
	private Client clientInstance;
	private int completedSize;
	private int expectedSize;
	private int[] midiIndices;
	public int anInt1349;
	private int[] mapIndices2;
	private int filesLoaded;
	private boolean running;
	private OutputStream outputStream;
	private int[] mapIndices4;
	private boolean waiting;
	private final byte[] gzipInputBuffer;
	private int[] animationIndices;
	private InputStream inputStream;
	private Socket socket;
	//private final int[][] files;
	private final int[][] crcs;
	private int incomplete;
	private int complete;
	private OnDemandRequest current;
	private final Deque next;
	private final Queue queue;
	private final Deque requested;
	private final Deque extraFilesList;
	private final Deque incompleteList;
	private final Deque loadRequestList;
	private int[] mapIndices1;
	private byte[] modelIndices;
	private int loopCycle;
}
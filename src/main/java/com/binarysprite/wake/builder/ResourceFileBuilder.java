package com.binarysprite.wake.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.binarysprite.wake.Builder;
import com.binarysprite.wake.BuilderParam;

public class ResourceFileBuilder implements Builder {

	@Override
	public void build(BuilderParam param) {

		FileChannel srcChannel = null;
		FileChannel destChannel = null;
		try {

			srcChannel = new FileInputStream(param.inputFile).getChannel();
			destChannel = new FileOutputStream(param.outputFile).getChannel();

			srcChannel.transferTo(0, srcChannel.size(), destChannel);

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (srcChannel != null) {
				try {
					srcChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (destChannel != null) {
				try {
					destChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

}

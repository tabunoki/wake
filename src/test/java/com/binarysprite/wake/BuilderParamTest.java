package com.binarysprite.wake;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class BuilderParamTest {

	@Test
	public void test01() {
		
		File inputRoot = new File("D:\\root\\aaa");
		File outputRoot = new File("D:\\root\\111");
		File inputFile = new File("D:\\root\\aaa\\bbb");

		BuilderParam param = new BuilderParam(inputRoot, outputRoot, inputFile);
		
		assertEquals("D:\\root\\111\\bbb", param.outputFile.getAbsolutePath());
		
	}

}

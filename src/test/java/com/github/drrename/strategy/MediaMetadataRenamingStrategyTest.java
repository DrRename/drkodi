package com.github.drrename.strategy;

import java.io.File;



import com.github.drrename.AbstractRenamingStrategyTest;
import com.kerner1000.drrename.RenamingStrategy;
import drrename.strategy.MediaMetadataRenamingStrategy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MediaMetadataRenamingStrategyTest extends AbstractRenamingStrategyTest {

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {

	}

	@Override
	protected RenamingStrategy getStrategy() {

		return new MediaMetadataRenamingStrategy();
	}

	@Override
	protected File getTestFile() {

		return testFile01;
	}

	protected static final File testFile01 = new File("src/test/resources/UPPERCASE.txt");

	@Test
	public void test() {

	}
}

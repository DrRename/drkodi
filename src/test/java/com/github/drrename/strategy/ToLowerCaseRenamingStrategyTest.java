package com.github.drrename.strategy;

import com.github.drrename.AbstractRenamingStrategyTest;
import drrename.strategy.RenamingStrategy;
import drrename.strategy.ToLowerCaseRenamingStrategy;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToLowerCaseRenamingStrategyTest extends AbstractRenamingStrategyTest {

	ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

	@Override
	protected RenamingStrategy getStrategy() {
		return new ToLowerCaseRenamingStrategy(bundle);
	}

	@Override
	protected File getTestFile() {
		return testFile01;
	}

	protected static final File testFile01 = new File("src/test/resources/UPPERCASE.txt");

	@Test
	public void test01() throws IOException, InterruptedException {

		assertEquals("test", s.getNameNew(Paths.get("/home/TEST")));
	}

	@Test
	public void test02() throws IOException, InterruptedException {

		renamedFile = s.rename(testFile01.toPath(), null);
		assertThat(Files.exists(renamedFile), is(true));
		assertThat(renamedFile.getFileName().toString(), is(testFile01.getName().toLowerCase()));
	}
}

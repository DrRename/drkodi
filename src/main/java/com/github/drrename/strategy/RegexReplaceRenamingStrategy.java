package com.github.drrename.strategy;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexReplaceRenamingStrategy extends RenamingStrategyProto {

	private static final Logger logger = LoggerFactory.getLogger(RegexReplaceRenamingStrategy.class);

	@Override
	public String getIdentifier() {

		return "Regex Replace";
	}

	@Override
	public String getNameNew(final Path file) throws InterruptedException {

		if(Thread.currentThread().isInterrupted())
			throw new InterruptedException("Cancelled");
		try {
			return file.getFileName().toString().replaceAll(getReplacementStringFrom(),getReplacementStringTo());
		} catch(final PatternSyntaxException e) {
			if(logger.isDebugEnabled())
				logger.debug(e.getLocalizedMessage());
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return true;
	}
}

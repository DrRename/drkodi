package drrename.strategy;

import java.nio.file.Path;

public class SimpleReplaceRenamingStrategy extends RenamingStrategyProto {

	@Override
	public String getIdentifier() {

		return "Simple Replace";
	}

	@Override
	public String getNameNew(final Path file) throws InterruptedException {

		if(Thread.currentThread().isInterrupted())
			throw new InterruptedException("Cancelled");
		return file.getFileName().toString().replace(getReplacementStringFrom(), getReplacementStringTo());
	}

	@Override
	public boolean isReplacing() {

		return true;
	}
}

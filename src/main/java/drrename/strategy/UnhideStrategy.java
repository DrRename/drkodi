
package drrename.strategy;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class UnhideStrategy extends RenamingStrategyProto {

	@Override
	public String getIdentifier() {

		return "Unhide";
	}

	@Override
	public String getNameNew(final Path file) throws InterruptedException {

		if(Thread.currentThread().isInterrupted())
			throw new InterruptedException("Cancelled");
		if(file.getFileName().toString().matches(".+\\~\\d+\\~")){
			return file.getFileName().toString().substring(0, file.getFileName().toString().indexOf("~") - 1);
		}
		if(file.getFileName().toString().endsWith("~")){
			return file.getFileName().toString().substring(0, file.getFileName().toString().length() - 1);
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}

package drrename.strategy;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import drrename.RenamingStrategy;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RenamingStrategyProto implements RenamingStrategy {

	private static final Logger logger = LoggerFactory.getLogger(RenamingStrategyProto.class);
	private final static Pattern pattern = Pattern.compile(".*_copy(\\d*)$");

	private  final ResourceBundle resourceBundle;

	private String replacementStringFrom = "";
	private String replacementStringTo = "";

	public RenamingStrategyProto(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	protected ResourceBundle getResourceBundle(){
		return  resourceBundle;
	}

	@Override
	public String getIdentifier() {

		return String.format(String.format(getResourceBundle().getString(getInternalId())));
	}

	protected abstract String getInternalId();

	/**
	 * Performs the rename. Does not override existing files, but creates
	 * numbered suffixes for file names that exist already.
	 *
	 * @param file
	 *            the file to rename
	 * @param nameNew
	 *            the new file name
	 * @return
	 * @throws IOException
	 */
	protected Path doRename(final Path file, String nameNew) throws IOException {

		int fileNameCounter = 1;
		try {
			final String nameOld = getNameOld(file);
			if(nameOld.equals(nameNew)) {
				if(logger.isDebugEnabled()) {
					logger.debug("Skipping '" + nameOld + "'");
				}
				return file;
			}
			if(logger.isDebugEnabled()) {
				logger.debug("Renaming" + IOUtils.LINE_SEPARATOR + "old:\t" + nameOld + IOUtils.LINE_SEPARATOR + "new:\t" + nameNew);
			}
			return Files.move(file, file.resolveSibling(nameNew));
		} catch(final FileAlreadyExistsException e) {
			logger.debug(e.getLocalizedMessage());
			return doRename(file, getFileAlreadyExistsFileName(nameNew, fileNameCounter));
		}
	}

	public String getFileAlreadyExistsFileName(String nameNew, int fileNameCounter) {
		final String extension = FilenameUtils.getExtension(nameNew);
		String baseName = FilenameUtils.getBaseName(nameNew);
		final Matcher matcher = pattern.matcher(baseName);
		if(matcher.matches()) {
			final String group = matcher.group(1);
			final int index = matcher.start(1);
			baseName = baseName.substring(0, index - 1); // also omit '_'
			// string
			fileNameCounter = Integer.parseInt(group);
		}
		fileNameCounter++;
		nameNew = baseName + "_copy" + fileNameCounter + "." + extension;
		return nameNew;
	}

	@Override
	public String getHelpText() {

		return getIdentifier();
	}

	protected String getNameOld(final Path file) {

		return file.getFileName().toString();
	}

	public String getReplacementStringFrom() {

		return replacementStringFrom;
	}

	public String getReplacementStringTo() {

		return replacementStringTo;
	}

	@Override
	public Path rename(final Path file, final BasicFileAttributes attrs) throws IOException, InterruptedException {

		return doRename(file, getNameNew(file));
	}

	@Override
	public void setReplacementStringFrom(final String replacementStringFrom) {

		this.replacementStringFrom = replacementStringFrom;
	}

	@Override
	public void setReplacementStringTo(final String replacementStringTo) {

		this.replacementStringTo = replacementStringTo;
	}

	@Override
	public String toString() {

		return getIdentifier();
	}
}

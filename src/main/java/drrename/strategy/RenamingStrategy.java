/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.strategy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface RenamingStrategy {

	/**
	 * Returns a help text that explains what this strategy will do.
	 * @return a help text explaining what this strategy will do
	 */
	String getHelpText();

	/**
	 * Returns the strategy's name, i.e., a short text to identify this strategy.
	 * @return the strategy's name
	 */
	String getName();

	/**
	 * Returns the new file name, i.e., the new file name provided by this strategy.
	 * @param file the file to rename
	 * @return the new file name
	 */
	String getNameNew(Path file);

	/**
	 *
	 * @return {code true}, if this strategy is replacing one (sub)string by another; {@code false} otherwise
	 * @see #setReplacementStringFrom(String)
	 * @see #setReplacementStringTo(String)
	 */
	boolean isReplacing();

	Path rename(Path file, BasicFileAttributes attrs) throws IOException, InterruptedException;

	/**
	 * Sets the string that should be replaced by a replacing strategy.
	 * @param replacement string to replace
	 * @return {@code this}
	 * @see #setReplacementStringTo(String)
	 * @see #isReplacing()
	 */
	RenamingStrategy setReplacementStringFrom(String replacement);

	/**
	 * Sets the replacement string, by which another string should be replaced.
	 * @param replacement the new string for a replacement
	 * @return {@code this}
	 * @see #setReplacementStringFrom(String)
	 * @see #isReplacing()
	 */
    RenamingStrategy setReplacementStringTo(String replacement);

	RenamingConfig getConfig();

	RenamingStrategy setConfig(RenamingConfig config);


}

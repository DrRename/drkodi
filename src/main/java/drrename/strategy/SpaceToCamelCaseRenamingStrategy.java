package drrename.strategy;

import org.apache.commons.text.CaseUtils;

import java.nio.file.Path;

public class SpaceToCamelCaseRenamingStrategy extends RenamingStrategyProto {

	@Override
	public String getIdentifier() {

		return "Space to Camel Case";
	}

	@Override
	public String getNameNew(final Path file)  {


		if(!file.getFileName().toString().matches("\\S+")){
			String newName = CaseUtils.toCamelCase(file.getFileName().toString(), false, ' ');
			return newName;
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}

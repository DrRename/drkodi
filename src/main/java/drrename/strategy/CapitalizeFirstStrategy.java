package drrename.strategy;

import drrename.strategy.RenamingStrategyProto;
import org.apache.commons.text.WordUtils;

import java.io.IOException;
import java.nio.file.Path;

public class CapitalizeFirstStrategy extends RenamingStrategyProto {
    @Override
    public String getIdentifier() {
        return "Capitalize First Letter";
    }

    @Override
    public String getNameNew(Path file)  {
        return WordUtils.capitalize(file.getFileName().toString(), ' ', '_','-');
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}

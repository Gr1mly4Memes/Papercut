package gr1mly4memes.papercut.region;

import java.io.IOException;

public interface IRegionCreateFunction {
    IRegionFile create(RegionCreatorInfo info) throws IOException;
}
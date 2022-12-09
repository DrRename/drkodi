/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.kodi.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class SearchResult {

    private final ObjectProperty<Number> id;

    private final StringProperty title;

    private final StringProperty plot;

    private final ObjectProperty<Integer> releaseDate;

    private final ObjectProperty<Image> image;

    private final ObjectProperty<byte[]> imageData;

    public SearchResult(SearchResult searchResult) {
        this.id = new SimpleObjectProperty<>(searchResult.getId());
        this.title = new SimpleStringProperty(searchResult.getTitle());
        this.releaseDate = new SimpleObjectProperty<>(searchResult.getReleaseDate());
        this.image = new SimpleObjectProperty<>(searchResult.getImage());
        this.imageData = new SimpleObjectProperty<>(searchResult.getImageData());
        this.plot= new SimpleStringProperty(searchResult.getPlot());
    }

    public SearchResult() {
        this.id = new SimpleObjectProperty<>();
        this.title = new SimpleStringProperty();
        this.releaseDate = new SimpleObjectProperty<>();
        this.image = new SimpleObjectProperty<>();
        this.imageData = new SimpleObjectProperty<>();
        this.plot= new SimpleStringProperty();
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "movieTitle=" + title +
                ", movieYear=" + releaseDate +
                '}';
    }
// FX Getter / Setter //


    public Number getId() {
        return id.get();
    }

    public ObjectProperty<Number> idProperty() {
        return id;
    }

    public void setId(Number id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public Integer getReleaseDate() {
        return releaseDate.get();
    }

    public ObjectProperty<Integer> releaseDateProperty() {
        return releaseDate;
    }

    public void setReleaseDate(Integer releaseDate) {
        this.releaseDate.set(releaseDate);
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public byte[] getImageData() {
        return imageData.get();
    }

    public ObjectProperty<byte[]> imageDataProperty() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData.set(imageData);
    }

    public String getPlot() {
        return plot.get();
    }

    public StringProperty plotProperty() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot.set(plot);
    }
}

package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;
import net.dorokhov.pony.core.search.SearchAnalyzer;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "genre")
@Indexed
public class Genre extends BaseEntity<Long> implements Comparable<Genre> {

	private String name;

	private StoredFile artwork;

	private List<Song> songs;

	@Column(name = "name")
	@Field(analyzer = @Analyzer(impl = SearchAnalyzer.class))
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "artwork_stored_file_id")
	public StoredFile getArtwork() {
		return artwork;
	}

	public void setArtwork(StoredFile aArtwork) {
		artwork = aArtwork;
	}

	@Override
	@SuppressWarnings("NullableProblems")
	public int compareTo(Genre aGenre) {
		return ObjectUtils.compare(getName(), aGenre.getName());
	}

	@Override
	public String toString() {
		return "Genre{" +
				"id=" + getId() +
				", name='" + name + '\'' +
				'}';
	}

}

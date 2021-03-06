package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.*;
import net.dorokhov.pony.web.client.mvp.common.HasLoadingState;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.SelectionMode;
import net.dorokhov.pony.web.client.service.BusyModeManager;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.PlayListImpl;
import net.dorokhov.pony.web.client.service.SongService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.client.util.ErrorUtils;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.*;

import javax.inject.Inject;
import java.util.*;

public class AlbumListPresenter extends PresenterWidget<AlbumListPresenter.MyView> implements AlbumListUiHandlers,
		ArtistSelectionEvent.Handler, RefreshRequestEvent.Handler, EmptyLibraryEvent.Handler, PlaybackRequestEvent.Handler,
		SongSelectionRequestEvent.Handler, SongChangeEvent.Handler, SongStartEvent.Handler, SongPauseEvent.Handler, SongEndEvent.Handler {

	public interface MyView extends View, HasUiHandlers<AlbumListUiHandlers>, HasLoadingState {

		public ArtistDto getArtist();

		public void setArtist(ArtistDto aArtist);

		public List<AlbumSongsDto> getAlbums();

		public void setAlbums(List<AlbumSongsDto> aAlbums);

		public Set<SongDto> getSelectedSongs();

		public void setSelectedSongs(Set<SongDto> aSongs);

		public SongDto getActiveSong();

		public void setActiveSong(SongDto aSong);

		public boolean isPlaying();

		public void setPlaying(boolean aPlaying);

		public void selectSong(SongDto aSong, SelectionMode aSelectionMode);

		public void scrollToTop();

		public void scrollToSong(SongDto aSong);

	}

	private final ErrorNotifier errorNotifier;

	private final BusyModeManager busyModeManager;

	private final SongService songService;

	private OperationRequest currentRequest;

	private boolean shouldHandleSongActivation = true;
	private boolean shouldScrollToSong = false;

	@Inject
	public AlbumListPresenter(EventBus aEventBus, MyView aView,
							  ErrorNotifier aErrorNotifier, BusyModeManager aBusyModeManager, SongService aSongService) {

		super(aEventBus, aView);

		errorNotifier = aErrorNotifier;
		busyModeManager = aBusyModeManager;
		songService = aSongService;

		getView().setUiHandlers(this);
		getView().setLoadingState(LoadingState.LOADING);
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(ArtistSelectionEvent.TYPE, this);
		addRegisteredHandler(RefreshRequestEvent.TYPE, this);
		addRegisteredHandler(EmptyLibraryEvent.TYPE, this);
		addRegisteredHandler(PlaybackRequestEvent.TYPE, this);

		addRegisteredHandler(SongSelectionRequestEvent.TYPE, this);
		addRegisteredHandler(SongChangeEvent.TYPE, this);
		addRegisteredHandler(SongStartEvent.TYPE, this);
		addRegisteredHandler(SongPauseEvent.TYPE, this);
		addRegisteredHandler(SongEndEvent.TYPE, this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		if (getView().getArtist() != null) {
			loadAlbums(getView().getArtist(), true);
		}
	}

	@Override
	public void onSongSelection(Set<SongDto> aSongs) {
		getEventBus().fireEvent(new SongSelectionEvent(aSongs));
	}

	@Override
	public void onSongActivation(SongDto aSong) {

		if (shouldHandleSongActivation) {

			List<SongDto> songs = new ArrayList<>();

			for (AlbumSongsDto album : getView().getAlbums()) {
				songs.addAll(album.getSongs());
			}

			getEventBus().fireEvent(new PlayListChangeEvent(new PlayListImpl(songs), songs.indexOf(aSong)));
		}

		shouldHandleSongActivation = true;
	}

	@Override
	public void onArtistSelection(ArtistSelectionEvent aEvent) {
		loadAlbums(aEvent.getArtist(), true);
	}

	@Override
	public void onRefreshRequest(RefreshRequestEvent aEvent) {
		if (getView().getArtist() != null) {
			loadAlbums(getView().getArtist(), !getView().getLoadingState().isEmptyOrLoaded());
		}
	}

	@Override
	public void onEmptyLibrary(EmptyLibraryEvent aEvent) {

		if (currentRequest != null) {

			currentRequest.cancel();

			busyModeManager.finishTask();
		}

		currentRequest = null;

		getView().setArtist(null);
		getView().setAlbums(null);

		getView().setLoadingState(LoadingState.EMPTY);
	}

	@Override
	public void onPlaybackRequest(PlaybackRequestEvent aEvent) {

		SongDto song = null;

		if (getView().getSelectedSongs().size() > 0) {

			List<SongDto> songList = new ArrayList<>(getView().getSelectedSongs());

			Collections.sort(songList);

			song = songList.get(0);
		}

		if (song == null) {

			AlbumSongsDto album = getView().getAlbums() != null && getView().getAlbums().size() > 0 ? getView().getAlbums().get(0) : null;

			if (album != null) {
				song = album.getSongs().size() > 0 ? album.getSongs().get(0) : null;
			}
		}

		if (song != null) {
			getView().setActiveSong(song);
		}
	}

	@Override
	public void onSongSelectionRequest(SongSelectionRequestEvent aEvent) {
		getView().selectSong(aEvent.getSong(), aEvent.getSelectionMode());
		getView().scrollToSong(aEvent.getSong());
	}

	@Override
	public void onSongChange(SongChangeEvent aEvent) {
		handleSongStart(aEvent.getSong());
	}

	@Override
	public void onSongStart(SongStartEvent aEvent) {
		handleSongStart(aEvent.getSong());
	}

	@Override
	public void onSongPause(SongPauseEvent aEvent) {
		getView().setPlaying(false);
	}

	@Override
	public void onSongEnd(SongEndEvent aEvent) {
		getView().setPlaying(false);
	}

	private void handleSongStart(SongDto aSong) {

		if (!aSong.equals(getView().getActiveSong())) {

			shouldHandleSongActivation = false;

			getView().setActiveSong(aSong);
		}

		getView().setPlaying(true);
	}

	private void loadAlbums(ArtistDto aArtist, final boolean aShouldShowLoadingState) {

		ArtistDto oldArtist = getView().getArtist();

		getView().setArtist(aArtist);

		if (!ObjectUtils.nullSafeEquals(aArtist, oldArtist)) {

			getView().scrollToTop();

			shouldScrollToSong = true;
		}

		if (aShouldShowLoadingState) {
			getView().setLoadingState(LoadingState.LOADING);
		}

		if (currentRequest != null) {

			currentRequest.cancel();

			busyModeManager.finishTask();
		}

		if (aArtist != null && aArtist.getId() != null) {

			busyModeManager.startTask();

			currentRequest = songService.getArtistSongs(aArtist.getId(), new OperationCallback<ArtistAlbumsDto>() {
				@Override
				public void onSuccess(ArtistAlbumsDto aArtistAlbums) {

					busyModeManager.finishTask();

					currentRequest = null;

					getView().setArtist(aArtistAlbums.getArtist());
					getView().setAlbums(aArtistAlbums.getAlbums());

					getView().setLoadingState(LoadingState.LOADED);

					if (shouldScrollToSong) {

						if (getView().getSelectedSongs().size() > 0) {
							scrollToSongs(getView().getSelectedSongs());
						} else if (getView().getActiveSong() != null) {
							getView().scrollToSong(getView().getActiveSong());
						}

						shouldScrollToSong = false;
					}
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					busyModeManager.finishTask();

					currentRequest = null;

					if (ErrorUtils.getErrorByCode(aErrors, ErrorCodes.ARTIST_NOT_FOUND) != null) {
						getView().setLoadingState(LoadingState.EMPTY);
					} else {

						errorNotifier.notifyOfErrors(aErrors);

						if (aShouldShowLoadingState) {

							getView().setAlbums(null);

							getView().setLoadingState(LoadingState.ERROR);
						}
					}
				}
			});

		} else {
			getView().setLoadingState(LoadingState.LOADED);
		}
	}

	private void scrollToSongs(Collection<SongDto> aSongs) {

		List<SongDto> songList = new ArrayList<>(aSongs);

		Collections.sort(songList);

		getView().scrollToSong(songList.get(0));
	}

}

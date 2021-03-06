package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import net.dorokhov.pony.web.client.event.SongSelectionRequestEvent;
import net.dorokhov.pony.web.client.event.SongStartRequestEvent;
import net.dorokhov.pony.web.client.mvp.common.SelectionMode;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.client.util.StringUtils;
import net.dorokhov.pony.web.shared.SongDto;

public class SongView extends Composite implements SongSelectionRequestEvent.HasHandler, SongStartRequestEvent.HasHandler {

	interface MyUiBinder extends UiBinder<FocusPanel, SongView> {}

	@SuppressWarnings("GwtCssResourceErrors")
	interface MyStyle extends CssResource {

		public String songView();

		public String withArtist();
		public String activated();
		public String playing();
		public String paused();
		public String selected();

	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final HandlerManager handlerManager = new HandlerManager(this);

	@UiField
	MyStyle style;

	@UiField
	FocusPanel songView;

	@UiField
	Label trackNumberLabel;

	@UiField
	Label nameLabel;

	@UiField
	Label artistLabel;

	@UiField
	Label durationLabel;

	private SongDto song;

	private boolean selected;

	private boolean activated;

	private boolean playing;

	private boolean artistShown;

	public SongView() {

		initWidget(uiBinder.createAndBindUi(this));

		setSelected(false);
		setActivated(false);
		setPlaying(false);
	}

	public SongDto getSong() {
		return song;
	}

	public void setSong(SongDto aSong) {

		song = aSong;

		updateSong();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean aSelected) {

		selected = aSelected;

		updateStyle();
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean aActivated) {

		activated = aActivated;

		updateStyle();
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean aPlaying) {

		playing = aPlaying;

		updateStyle();
	}

	public boolean isArtistShown() {
		return artistShown;
	}

	public void setArtistShown(boolean aArtistShown) {

		artistShown = aArtistShown;

		updateStyle();
	}

	@Override
	public HandlerRegistration addSongSelectionRequestHandler(SongSelectionRequestEvent.Handler aHandler) {
		return handlerManager.addHandler(SongSelectionRequestEvent.TYPE, aHandler);
	}

	@Override
	public HandlerRegistration addSongStartRequestHandler(SongStartRequestEvent.Handler aHandler) {
		return handlerManager.addHandler(SongStartRequestEvent.TYPE, aHandler);
	}

	@UiHandler("songView")
	void onClick(ClickEvent aEvent) {
		if (isSelected()) {

			handlerManager.fireEvent(new SongStartRequestEvent(getSong()));

		} else {

			SelectionMode selectionType;

			if (aEvent.isMetaKeyDown()) {
				selectionType = SelectionMode.ADD;
			} else if (aEvent.isShiftKeyDown()) {
				selectionType = SelectionMode.GROUP;
			} else {
				selectionType = SelectionMode.SINGLE;
			}

			handlerManager.fireEvent(new SongSelectionRequestEvent(getSong(), selectionType));
		}
	}

	private void updateSong() {
		trackNumberLabel.setText(song != null ? ObjectUtils.nullSafeToString(song.getTrackNumber()) : null);
		nameLabel.setText(song != null ? song.getName() : null);
		artistLabel.setText(song != null ? song.getArtistName() : null);
		durationLabel.setText(song != null ? StringUtils.secondsToMinutes(song.getDuration()) : null);
	}

	private void updateStyle() {

		songView.setStyleName(style.songView());

		if (isArtistShown()) {
			songView.addStyleName(style.withArtist());
		}
		if (isActivated()) {

			songView.addStyleName(style.activated());

			if (isPlaying()) {
				songView.addStyleName(style.playing());
			} else {
				songView.addStyleName(style.paused());
			}
		}
		if (isSelected()) {
			songView.addStyleName(style.selected());
		}
	}

}

package net.dorokhov.pony.web.client;

import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import net.dorokhov.pony.web.client.mvp.*;
import net.dorokhov.pony.web.client.service.*;
import net.dorokhov.pony.web.client.service.api.ApiService;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		install(new DefaultModule());

		bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class, LoginView.class, LoginPresenter.MyProxy.class);

		bindPresenter(LibraryPresenter.class, LibraryPresenter.MyView.class, LibraryView.class, LibraryPresenter.MyProxy.class);

		bindSingletonPresenterWidget(PlayerPresenter.class, PlayerPresenter.MyView.class, PlayerView.class);
		bindSingletonPresenterWidget(ToolbarPresenter.class, ToolbarPresenter.MyView.class, ToolbarView.class);
		bindSingletonPresenterWidget(LibraryContentPresenter.class, LibraryContentPresenter.MyView.class, LibraryContentView.class);

		bindPresenter(ErrorPresenter.class, ErrorPresenter.MyView.class, ErrorView.class, ErrorPresenter.MyProxy.class);

		bindConstant().annotatedWith(DefaultPlace.class).to(PlaceTokens.LIBRARY);
		bindConstant().annotatedWith(ErrorPlace.class).to(PlaceTokens.ERROR);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(PlaceTokens.LOGIN);

		bind(ApiService.class).asEagerSingleton();

		bind(ErrorNotifier.class).to(ErrorNotifierImpl.class).asEagerSingleton();

		bind(AuthenticationManager.class).asEagerSingleton();
		bind(AuthenticationDispatcherFilter.class).asEagerSingleton();

		bind(AuthenticationListener.class).asEagerSingleton();
	}

}
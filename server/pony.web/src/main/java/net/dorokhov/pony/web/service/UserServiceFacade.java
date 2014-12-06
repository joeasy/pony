package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.UserTokenDto;
import net.dorokhov.pony.web.domain.command.CreateUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateUserCommand;

import java.util.List;

public interface UserServiceFacade {

	public UserDto getById(Long aId);

	public List<UserDto> getAll();

	public UserDto create(CreateUserCommand aCommand) throws UserExistsException;
	public UserDto update(UpdateUserCommand aCommand) throws UserNotFoundException, UserExistsException;

	public UserTokenDto authenticate(String aEmail, String aPassword) throws InvalidCredentialsException;

	public void logout(UserTokenDto aToken) throws InvalidTokenException;

	public UserDto getAuthenticatedUser() throws NotAuthenticatedException;

	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommand aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidCredentialsException, UserNotFoundException, UserExistsException;
}
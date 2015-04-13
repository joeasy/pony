package net.dorokhov.pony.web.client.service.api;

import com.google.gwt.http.client.Request;
import net.dorokhov.pony.web.shared.*;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

@Options(expect = {200, 201, 204, 1223, 400, 401, 404, 500})
public interface ApiService extends RestService {

	@POST
	@Path("/authenticate")
	Request authenticate(CredentialsDto aCredentials, MethodCallback<ResponseDto<AuthenticationDto>> aCallback);

	@POST
	@Path("/logout")
	Request logout(MethodCallback<ResponseDto<UserDto>> aCallback);

	@GET
	@Path("/currentUser")
	Request getCurrentUser(MethodCallback<ResponseDto<UserDto>> aCallback);

	@POST
	@Path("/refreshToken")
	Request refreshToken(@HeaderParam(SecurityTokens.REFRESH_TOKEN_HEADER) String aRefreshToken, MethodCallback<ResponseDto<AuthenticationDto>> aCallback);

	@GET
	@Path("/artists")
	Request getArtists(MethodCallback<ResponseDto<List<ArtistDto>>> aCallback);

	@GET
	@Path("/artistAlbums/{aArtistIdOrName}")
	Request getArtistSongs(@PathParam("aArtistIdOrName") String aArtistIdOrName, MethodCallback<ResponseDto<ArtistAlbumsDto>> aCallback);

	@GET
	@Path("/scanStatus")
	Request getScanStatus(MethodCallback<ResponseDto<ScanStatusDto>> aCallback);

	@GET
	@Path("/admin/scanJobs")
	Request getScanJobs(@QueryParam("pageNumber") int aPageNumber, MethodCallback<ResponseDto<PagedListDto<ScanJobDto>>> aCallback);

	@GET
	@Path("/admin/scanJobs/{aId}")
	Request getScanJob(@PathParam("aId") Long aId, MethodCallback<ResponseDto<ScanJobDto>> aCallback);

	@POST
	@Path("/admin/startScanJob")
	Request startScanJob(MethodCallback<ResponseDto<ScanJobDto>> aCallback);

	@GET
	@Path("/admin/log")
	Request getLog(@QueryParam("pageNumber") int aPageNumber, @QueryParam("type") LogMessageDto.Type aType,
				   @QueryParam("minDate") Long aMinDate, @QueryParam("maxDate") Long aMaxDate,
				   MethodCallback<ResponseDto<PagedListDto<LogMessageDto>>> aCallback);

	@GET
	@Path("/admin/users")
	Request getUsers(@QueryParam("pageNumber") int aPageNumber, MethodCallback<ResponseDto<PagedListDto<UserDto>>> aCallback);

	@POST
	@Path("/admin/users")
	Request createUser(CreateUserCommandDto aCommand, MethodCallback<ResponseDto<UserDto>> aCallback);

	@PUT
	@Path("/admin/users")
	Request updateUser(UpdateUserCommandDto aCommand, MethodCallback<ResponseDto<UserDto>> aCallback);

	@GET
	@Path("/admin/users/{aId}")
	Request getUser(@PathParam("aId") Long aId, MethodCallback<ResponseDto<UserDto>> aCallback);

	@DELETE
	@Path("/admin/users/{aId}")
	Request deleteUser(@PathParam("aId") Long aId, MethodCallback<ResponseDto<Object>> aCallback);

}

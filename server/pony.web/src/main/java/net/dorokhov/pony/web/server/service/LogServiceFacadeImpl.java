package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.domain.LogMessage;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.shared.ListDto;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.LogQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogServiceFacadeImpl implements LogServiceFacade {

	private static final int MAX_PAGE_SIZE = 100;

	private LogService logService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	public ListDto<LogMessageDto> getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidArgumentException {

		if (aPageNumber < 0) {
			throw new InvalidArgumentException("errorPageNumberInvalid", "Page number [" + aPageNumber + "] is invalid", String.valueOf(aPageNumber));
		}
		if (aPageSize > MAX_PAGE_SIZE) {
			throw new InvalidArgumentException("errorPageSizeInvalid", "Page size [" + aPageNumber + "] must be less than or equal to [" + MAX_PAGE_SIZE + "]",
					String.valueOf(aPageSize), String.valueOf(MAX_PAGE_SIZE));
		}

		LogMessage.Type type = aQuery.getType();
		if (type == null) {
			type = LogMessage.Type.DEBUG;
		}

		Date minDate = aQuery.getMinDate();
		if (minDate == null) {
			minDate = new Date(0);
		}

		Date maxDate = aQuery.getMaxDate();
		if (maxDate == null) {
			maxDate = new Date();
		}

		return dtoConverter.listToDto(logService.getByTypeAndDate(type, minDate, maxDate, new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date")),
				new DtoConverter.ListConverter<LogMessage, LogMessageDto>() {
					@Override
					public LogMessageDto convert(LogMessage aItem) {
						return dtoConverter.logMessageToDto(aItem);
					}
				});
	}

}

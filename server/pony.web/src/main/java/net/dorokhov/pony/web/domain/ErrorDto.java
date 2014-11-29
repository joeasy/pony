package net.dorokhov.pony.web.domain;

import java.util.ArrayList;
import java.util.List;

public class ErrorDto {

	private String code;

	private String text;

	private List<String> arguments;

	public ErrorDto() {
		this(null, null, null);
	}

	public ErrorDto(String aCode, String aText) {
		this(aCode, aText, null);
	}

	public ErrorDto(String aCode, String aText, List<String> aArguments) {
		setCode(aCode);
		setText(aText);
		setArguments(aArguments);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String aCode) {
		code = aCode;
	}

	public String getText() {
		return text;
	}

	public void setText(String aText) {
		text = aText;
	}

	public List<String> getArguments() {

		if (arguments == null) {
			arguments = new ArrayList<>();
		}

		return arguments;
	}

	public void setArguments(List<String> aArguments) {
		arguments = aArguments;
	}
}
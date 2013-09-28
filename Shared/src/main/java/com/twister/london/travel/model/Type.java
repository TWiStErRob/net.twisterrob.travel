package com.twister.london.travel.model;

import java.util.Map;

public enum Type {
	Unknown("unknown"),
	Tube("tubeStyle"),
	DLR("dlrStyle"),
	Underground("overgroundStyle"),
	Bus,
	Taxi,
	Water,
	Cycle;
	private final String m_id;
	private String m_url;
	@Deprecated
	private Type() { // TODO find out other style names
		m_id = null;
	}
	private Type(String id) {
		m_id = id;
	}

	public String getId() {
		return m_id;
	}

	public String getUrl() {
		return m_url;
	}

	public static Type get(String body) {
		Type result = Type.Unknown;
		if (body != null) {
			String clean = body.trim().replaceFirst("#", "");
			if (clean.length() != body.length()) {
				result = get(clean);
			} else {
				for (Type type: values()) {
					if (type.getId() != null && type.getId().equals(body)) {
						result = type;
					}
				}
			}
		}
		return result;
	}

	public static void reset(Map<String, String> types) {
		for (Type type: values()) {
			String url = types.get(type.getId());
			type.setUrl(url);
		}
	}

	private void setUrl(String url) {
		m_url = url;
	}

}

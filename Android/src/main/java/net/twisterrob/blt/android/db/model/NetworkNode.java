package net.twisterrob.blt.android.db.model;

import java.util.*;

import net.twisterrob.java.model.Location;

public class NetworkNode implements Comparable<NetworkNode> {
	private int m_id;
	private Location m_pos;
	private String m_name;

	public NetworkNode(int id, String name, Location pos) {
		m_id = id;
		m_name = name;
		m_pos = pos;
	}

	public final Set<NetworkLink> in = new TreeSet<NetworkLink>();
	public final Set<NetworkLink> out = new TreeSet<NetworkLink>();

	public int getID() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public Location getPos() {
		return m_pos;
	}

	public int compareTo(NetworkNode another) {
		return Integer.valueOf(m_id).compareTo(another.m_id);
	}

	@Override
	public String toString() {
		return String.format("%s (%d)", m_name, m_id);
	}
}

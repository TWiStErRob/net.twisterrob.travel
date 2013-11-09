package net.twisterrob.blt.android.db.model;

import java.util.*;

import net.twisterrob.java.model.Location;

public class NetworkNode implements Comparable<NetworkNode> {
	private int m_id;
	private Location m_pos;

	public NetworkNode(int id, Location pos) {
		m_id = id;
		m_pos = pos;
	}

	public final Set<NetworkLink> in = new TreeSet<NetworkLink>();
	public final Set<NetworkLink> out = new TreeSet<NetworkLink>();

	public int getID() {
		return m_id;
	}

	public Location getPos() {
		return m_pos;
	}

	public int compareTo(NetworkNode another) {
		return Integer.valueOf(m_id).compareTo(another.m_id);
	}
}

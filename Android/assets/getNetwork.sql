select
	l.stopFrom  as fromID,
	stFrom.latitude as fromLat,
	stFrom.longitude as fromLon,
	stFrom.name,
	l.stopTo    as toID,
	stTo.latitude as toLat,
	stTo.longitude as toLon,
	stTo.name,
	l.distance  as distance,
	r.line      as lineID,
	line.name
from Route r
join Line line         ON r.line = line._id
join Route_Section rs  ON r._id = rs.route
join Section s         ON rs.section = s._id
join Section_Link sl   ON s._id = sl.section 
join Link l            ON sl.link = l._id
join Stop stFrom       ON stFrom._id = l.stopFrom
join Stop stTo         ON stTo._id = l.stopTo
--where line.name = 'Piccadilly'
order by
	r._id, rs.seq, sl.seq
;

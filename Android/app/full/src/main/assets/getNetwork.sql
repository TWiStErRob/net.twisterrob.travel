select distinct
	l.stopFrom        as fromID,
	stFrom.latitude   as fromLat,
	stFrom.longitude  as fromLon,
	stFrom.name       as fromName,
	l.stopTo          as toID,
	stTo.latitude     as toLat,
	stTo.longitude    as toLon,
	stTo.name         as toName,
	l.distance        as distance,
	line.name         as line
from Route r
join Line line         ON r.line = line._id
join Route_Section rs  ON r._id = rs.route
join Section s         ON rs.section = s._id
join Section_Link sl   ON s._id = sl.section 
join Link l            ON sl.link = l._id
join Stop stFrom       ON stFrom._id = l.stopFrom
join Stop stTo         ON stTo._id = l.stopTo
where 1=1
--and line.name = 'Central'
--and stFrom.name = 'Liverpool Street'
--order by r._id, rs.seq, sl.seq
--order by fromName, toName
;

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="versions" scope="application" class="net.twisterrob.blt.gapp.viewmodel.Versions" />
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Twister's Better London Travel Home</title>
</head>

<body>
	<h1>Twister's Better London Travel Home</h1>

	<h2>Available apps</h2>
	<ul>
		<li><a href="FeedCron?feed=TubeDepartureBoardsLineStatus">FeedCronServlet (/FeedCron?feed=)</a></li>
		<li><a href="LineStatusHistory?current=false&errors=false">LineStatusHistoryServlet, just history, without errors</a></li>
		<li><a href="LineStatusHistory?current=true&errors=true">LineStatusHistoryServlet, with errors and current status</a></li>
	</ul>

	<h2>Version info</h2>
	<dl>
		<dt>applicationId</dt>
		<dd>${versions.applicationIdKey}=${versions.applicationId}</dd>
		<dt>applicationVersion</dt>
		<dd>${versions.applicationVersionKey}=${versions.applicationVersion}</dd>
		<dt>environment</dt>
		<dd>${versions.environmentKey}=${versions.environment}</dd>
		<dt>version</dt>
		<dd>${versions.versionKey}=${versions.version}</dd>
	</dl>

	<script></script>

</body>
</html>

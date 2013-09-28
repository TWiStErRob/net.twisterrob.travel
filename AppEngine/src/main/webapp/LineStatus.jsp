<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>Line Status</title>
</head>
<body>
	<table id="filmList" class="tablesorter">
		<thead>
			<tr>
				<th>Line</th>
				<th>Disruption Type</th>
				<th>Active?</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="lineStatus" items="${feed.lineStatuses}">
				<c:set var="lineColor" value="${call.init[lineStatus.line].arg[colors].invoke['getBackground']}" />
				<c:set var="lineTextColor" value="${call.init[lineStatus.line].arg[colors].invoke['getForeground']}" />
				<c:set var="lineColor" value="${call.static['java.lang.Integer'].intArg[lineColor].invoke['toHexString']}" />
				<c:set var="lineTextColor" value="${call.static['java.lang.Integer'].intArg[lineTextColor].invoke['toHexString']}" />
				<c:set var="lineColor" value="${call.init[lineColor].intArg[2].invoke['substring']}" />
				<c:set var="lineTextColor" value="${call.init[lineTextColor].intArg[2].invoke['substring']}" />
				<c:set var="delayStyle" value="" />
				<c:if test="${not empty lineStatus.description}">
					<c:set var="delayStyle" value="font-weight:bold;" />
				</c:if>
				<tr>
					<td style="background-color: #${lineColor}; color: #${lineTextColor}">${lineStatus.line.title}</td>
					<td style="${delayStyle}" title="${lineStatus.description}">${lineStatus.type.title}</td>
					<td><input type="checkbox" ${lineStatus.active? 'checked="checked"':''} /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
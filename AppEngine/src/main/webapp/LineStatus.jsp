<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
	<title>Line Status</title>
	<script type="text/javascript" src="/static/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="/static/htmltooltip.js"></script>
	<style>
		th {
			background-color: lightgray;
		}
		div.htmltooltip {
			position: absolute; /*leave this and next 3 values alone*/
			z-index: 1000;
			left: -1000px;
			top: -1000px;
			background: #fafaaa;
			border: 1px solid black;
			padding: 3px;
			width: 700px; /*width of tooltip*/
		}
	</style>
</head>
<body>
	<c:forEach var="feedChange" items="${feedChanges}" varStatus="status">
	<c:set var="feed" value="${feedChange.new}" />
	<table style="float: left; width: 300px; border: 1px solid black;">
		<thead>
			<tr>
				<th colspan="3" style="font-style: italic">
					${status.index+1}/${fn:length(feedChanges)}:
					<fmt:formatDate value="${feed.when}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</th>
			</tr>
			<tr>
				<th>Line</th>
				<th>Disruption Type</th>
				<th>Change</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="lineStatus" items="${feed.content.lineStatuses}">
				<c:set var="lineColor" value="${call.init[lineStatus.line].arg[colors].invoke['getBackground']}" />
				<c:set var="lineTextColor" value="${call.init[lineStatus.line].arg[colors].invoke['getForeground']}" />
				<c:set var="lineColor" value="${call.static['java.lang.Integer'].intArg[lineColor].invoke['toHexString']}" />
				<c:set var="lineTextColor" value="${call.static['java.lang.Integer'].intArg[lineTextColor].invoke['toHexString']}" />
				<c:set var="lineColor" value="${call.init[lineColor].intArg[2].invoke['substring']}" />
				<c:set var="lineTextColor" value="${call.init[lineTextColor].intArg[2].invoke['substring']}" />
				<c:set var="delayStyle" value="" />
				<c:set var="changeStyle" value="" />
				<c:if test="${not empty lineStatus.description}">
					<c:set var="delayStyle" value="${delayStyle};font-weight:bold" />
				</c:if>
				<c:if test="${not lineStatus.active}">
					<c:set var="delayStyle" value="${delayStyle};color: lightgray" />
				</c:if>
				<c:if test="${feedChange.statuses[lineStatus.line] == 'better'}">
					<c:set var="delayStyle" value="${delayStyle};color: green;font-weight:bold" />
				</c:if>
				<c:if test="${feedChange.statuses[lineStatus.line] == 'worse'}">
					<c:set var="delayStyle" value="${delayStyle};color: red;font-weight:bold" />
				</c:if>
				<c:if test="${feedChange.statuses[lineStatus.line] == 'same'}">
					<c:set var="changeStyle" value="${changeStyle};color: lightgray" />
				</c:if>
				<tr>
					<td style="background-color: #${lineColor}; color: #${lineTextColor}">${lineStatus.line.title}</td>
					<td style="${delayStyle}">
					<c:choose>
							<c:when test="${empty lineStatus.description}">
								${lineStatus.type.title}
							</c:when>
							<c:otherwise>
								<a rel="htmltooltip">${lineStatus.type.title}</a>
								<div class="htmltooltip">${lineStatus.description}</div>
							</c:otherwise>
						</c:choose>
					</td>
					<td style="${changeStyle}">
						<c:choose>
							<c:when test="${empty feedChange.descriptions[lineStatus.line]}">
								${feedChange.statuses[lineStatus.line]}
							</c:when>
							<c:otherwise>
								<a rel="htmltooltip">${feedChange.statuses[lineStatus.line]}</a>
								<div class="htmltooltip">${feedChange.descriptions[lineStatus.line]}</div>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<a rel="htmltooltip">${feedChange.error}: ${feed.errorHeader}</a>
					<div class="htmltooltip"><pre style="font-size: xx-small;">${feed.fullError}</pre></div>
				</td>
			</tr>
		</tfoot>
	</table>
	</c:forEach>
</body>
</html>
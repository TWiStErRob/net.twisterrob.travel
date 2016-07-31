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
    <style type="text/css">
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

        table.info {
            float: left;
            width: 325px;
            border: 1px solid black;
            border-spacing: 0;
            border-collapse: collapse;
        }

        table.info > thead > tr > th {
            background-color: lightgray;
            border: 1px solid black;
        }

        table.info > thead > tr > th.title {
            font-style: italic;
        }

        table.info > * > tr > td,
        table.info > * > tr > th {
            padding: 2px;
        }

        .delay.hasDetails {
            font-weight: bold;
        }

        .delay.inactive {
            color: lightgray;
        }

        .delay.status-better {
            color: green;
            font-weight: bold;
        }

        .delay.status-worse {
            color: red;
            font-weight: bold;
        }

        .change.status-same {
            color: white;
        }

        <c:forEach var="lineColor" items="${colors.iterator}" varStatus="status">
        .line-${lineColor.line} {
            color: ${lineColor.foregroundColor};
            background-color: ${lineColor.backgroundColor};
        }

        </c:forEach>
	</style>
</head>
<body>
	<c:forEach var="feedChange" items="${feedChanges}" varStatus="status">
	<c:set var="feed" value="${feedChange.new}" />
        <table class="info">
		<thead>
			<tr>
                <th colspan="3" class="title">
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
				<c:set var="delayStyle" value="" />
				<c:if test="${not empty lineStatus.description}">
                    <c:set var="delayStyle" value="${delayStyle} hasDetails" />
				</c:if>
				<c:if test="${not lineStatus.active}">
                    <c:set var="delayStyle" value="${delayStyle} inactive" />
				</c:if>
				<tr>
                    <td class="line-${lineStatus.line}">${lineStatus.line.title}</td>
					<td class="delay ${feedChange.statuses[lineStatus.line].cssClass} ${delayStyle}">
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
					<td class="change ${feedChange.statuses[lineStatus.line].cssClass}">
						<c:choose>
							<c:when test="${empty feedChange.descriptions[lineStatus.line]}">
								${feedChange.statuses[lineStatus.line].title}
							</c:when>
							<c:otherwise>
								<a rel="htmltooltip">${feedChange.statuses[lineStatus.line].title}</a>
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
					<c:choose>
						<c:when test="${not empty feed.errorHeader}">
							<a rel="htmltooltip">${feedChange.error.title}: ${feed.errorHeader}</a>
							<div class="htmltooltip">
								<pre style="font-size: xx-small;">${feed.fullError}</pre>
							</div>
						</c:when>
						<c:when test="${not empty feedChange.error.title}">
							${feedChange.error.title}
						</c:when>
						<%-- Make sure there's always content, otherwise the height of the tables is not the same --%>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</tfoot>
	</table>
	</c:forEach>
</body>
</html>

<% response.setStatus(400); %>
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%@ page isErrorPage="true" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>Bad request (400)</title>
  <link rel="stylesheet" href="/webfiles/latest/assets/css/main.css">
</head>
<body>

  <%@ include file="header.html" %>

  <div class="ds_wrapper">
    <main class="ds_layout  ds_layout--article">
      <div class="ds_layout__header">
        <header class="ds_page-header">
          <h1 class="ds_page-header__title">Bad request (400)</h1>
        </header>
      </div>

      <div class="ds_layout__content">
        <p>The request cannot be fulfilled due to bad syntax.</p>
      </div>
    </main>
  </div>

  <%@ include file="footer.html" %>

</body>
</html>

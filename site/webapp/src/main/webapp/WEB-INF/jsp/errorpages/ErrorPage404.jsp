<% response.setStatus(404); %>
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%@ page isErrorPage="true" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>404 - Not Found</title>
  <link rel="stylesheet" href="/webfiles/latest/assets/css/main.css">
  <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>
</head>

<body>
<div class="ds_page">

  <%@ include file="header.html" %>

  <div class="ds_page__middle">
    <div class="ds_wrapper">
      <main id="main-content" class="ds_layout  ds_layout--article">
        <div class="ds_layout__header">
          <header class="ds_page-header">
            <h1 class="ds_page-header__title">Not Found (404)</h1>
          </header>
        </div>

        <div class="ds_layout__content">
          <p>
            Sorry, but the page you were trying to view does not exist. This could be the result of either:
          </p>
          <ul>
            <li>a mistyped address</li>
            <li>an out of date link</li>
          </ul>        </div>
      </main>
    </div>
  </div>

  <%@ include file="footer.html" %>

</div>
</body>

</html>
